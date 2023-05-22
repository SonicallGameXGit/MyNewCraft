package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Keyboard;
import org.mynewcraft.engine.io.Mouse;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.chunk.Chunk;

public class PlayerEntity extends LivingEntity {
    public PlayerEntity(CubeCollider collider, Vector3d rotation, double mass, double speed) {
        super(collider, rotation, mass, speed);
    }

    public void update(World world, Keyboard keyboard, Mouse mouse, Time time) {
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
        
        Vector3d realDirection = new Vector3d(direction).mul(speed * time.getDelta());
        Vector3d position = new Vector3d(collider.position);

        position.x += realDirection.x();
//        for(Chunk chunk : world.getNearChunks(new Vector2d(position.x(), position.z())))
//            for(Vector3i coordinate : chunk.getCoordinates())
//                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider))
//                    position.x -= realDirection.x();

        position.z += realDirection.z();
//        for(Chunk chunk : world.getNearChunks(new Vector2d(position.x(), position.z())))
//            for(Vector3i coordinate : chunk.getCoordinates())
//                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider))
//                    position.z -= realDirection.z();

        collider.position.y += realDirection.y();
//        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z())))
//            for(Vector3i coordinate : chunk.getCoordinates())
//                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider))
//                    collider.position.y -= realDirection.y();

        collider.position.x = position.x();
        collider.position.z = position.z();

        MyNewCraft.LOGGER.debug("(" + (float) collider.position.x() + " " + (float) collider.position.y() + " " + (float) collider.position.z() + ")");

        update(time);
    }
}