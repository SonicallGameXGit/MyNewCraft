package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.client.graphics.Camera;
import org.mynewcraft.client.graphics.util.BlockSelection;
import org.mynewcraft.engine.io.Keyboard;
import org.mynewcraft.engine.io.Mouse;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.math.physics.RayHitResult;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;
import org.mynewcraft.world.block.custom.Block;
import org.mynewcraft.world.chunk.Chunk;

public class PlayerEntity extends LivingEntity {
    public Camera camera;

    public PlayerEntity(CubeCollider collider, Vector3d rotation, double mass, double speed) {
        super(collider, rotation, mass, speed);

        camera = new Camera(new Vector3d(collider.position.x() + collider.scale.x() / 2.0, collider.position.y + collider.scale.y() / 1.125, collider.position.z() + collider.scale.z() / 2.0), new Vector3d());
    }

    public void update(World world, BlockSelection selection, Keyboard keyboard, Mouse mouse, Time time) {
        direction.set(0.0);

        if(keyboard.getPress(Keyboard.KEY_W))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y())));
        if(keyboard.getPress(Keyboard.KEY_S))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() + 180.0)));
        if(keyboard.getPress(Keyboard.KEY_D))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() + 90.0)));
        if(keyboard.getPress(Keyboard.KEY_A))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() - 90.0)));
        if(keyboard.getPress(Keyboard.KEY_LEFT_SHIFT))
            direction.sub(new Vector3d(0.0, 1.0, 0.0));
        if(keyboard.getPress(Keyboard.KEY_SPACE))
            direction.add(new Vector3d(0.0, 1.0, 0.0));
        if(direction.length() > 1.0) direction.normalize();

        rotation.add(new Vector3d(new Vector2d(mouse.getDirection().y(), mouse.getDirection().x()).mul(!mouse.getGrabbed() ? 0.0 : -0.05), 0.0));
        rotation.x = MathUtil.clamp(rotation.x(), -90.0, 90.0);
        rotation.y = rotation.y() >= 360.0 || rotation.y() <= -360.0 ? 0.0 : rotation.y();

        update(world, time);

        camera.updateInBB(collider, new Vector3d(0.5, 0.888, 0.5));
        camera.rotation.set(rotation);

        double nearestDistance = Double.POSITIVE_INFINITY;
        RayHitResult nearestHitResult = null;
        AbstractBlock nearestBlock = null;

        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
            for(Vector3i coordinate : chunk.getCoordinates()) {
                RayHitResult hitResult = new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).processRaycast(camera.position, MathUtil.angleToDirection(new Vector2d(camera.rotation.x(), camera.rotation.y())));

                if(hitResult != null && hitResult.hitPoint().distance(camera.position) < nearestDistance) {
                    nearestDistance = hitResult.hitPoint().distance(camera.position);
                    nearestHitResult = hitResult;
                    nearestBlock = chunk.getMap().get(coordinate);
                }
            }
        }

        if(nearestHitResult != null && nearestDistance <= 6.0) {
            if(mouse.getClick(Mouse.BUTTON_RIGHT)) {
                Vector3d blockPos = new Vector3d(nearestHitResult.hitObject().position).add(nearestHitResult.hitNormal());
                Vector3i intBlockPos = new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z());

                world.placeBlock(intBlockPos, Blocks.COBBLESTONE);
                world.updateMesh(new Vector2i((int) Math.floor(intBlockPos.x() / 16.0), (int) Math.floor(intBlockPos.z() / 16.0)));
            }
            if(mouse.getClick(Mouse.BUTTON_LEFT)) {
                if(nearestBlock instanceof Block block && block.getBreakable() || !(nearestBlock instanceof Block)) {
                    Vector3i blockPos = new Vector3i((int) nearestHitResult.hitObject().position.x(), (int) nearestHitResult.hitObject().position.y(), (int) nearestHitResult.hitObject().position.z());

                    world.removeBlock(blockPos);
                    world.updateMesh(new Vector2i((int) Math.floor(blockPos.x() / 16.0), (int) Math.floor(blockPos.z() / 16.0)));
                }
            }

            selection.position = new Vector3d(nearestHitResult.hitObject().position);
            selection.enabled();
        } else selection.disabled();
    }
}