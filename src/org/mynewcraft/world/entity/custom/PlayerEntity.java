package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.MyNewCraft;
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
    public static final int SURVIVAL_GAMEMODE = 0;
    public static final int CREATIVE_GAMEMODE = 1;
    public static final int SPECTATOR_GAMEMODE = 2;

    protected static final double FLY_JUMP_FREQUENCY = 0.6;

    public Camera camera;

    public double flySpeedMultiplier;

    protected int gameMode;
    protected boolean flying;

    private int jumpClicks;
    private long lastJumpTime;

    public PlayerEntity(World world, CubeCollider collider, Vector3d rotation, double mass, double speed, double flySpeedMultiplier, double jumpPower) {
        super(collider, rotation, mass, speed, jumpPower);

        this.flySpeedMultiplier = flySpeedMultiplier;

        camera = new Camera(new Vector3d(collider.position.x() + collider.scale.x() / 2.0, collider.position.y + collider.scale.y() / 1.125, collider.position.z() + collider.scale.z() / 2.0), new Vector3d());
        gameMode = world.DEFAULT_GAMEMODE;
        jumpClicks = 0;
        lastJumpTime = 0L;
        flying = false;
    }

    public void update(World world, BlockSelection selection, Keyboard keyboard, Mouse mouse, Time time) {
        direction.set(new Vector3d(0.0, (gameMode == CREATIVE_GAMEMODE && flying) || gameMode == SPECTATOR_GAMEMODE ? 0.0 : direction.y(), 0.0));
        applyPhysics = !flying;

        if(canJump) flying = false;
        if(keyboard.getPress(Keyboard.KEY_W))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y())));
        if(keyboard.getPress(Keyboard.KEY_S))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() + 180.0)));
        if(keyboard.getPress(Keyboard.KEY_D))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() + 90.0)));
        if(keyboard.getPress(Keyboard.KEY_A))
            direction.add(MathUtil.angleToDirection(new Vector2d(0.0, rotation.y() - 90.0)));
        if(gameMode == SPECTATOR_GAMEMODE) {
            if(keyboard.getPress(Keyboard.KEY_SPACE)) direction.y += speed * flySpeedMultiplier;
            if(keyboard.getPress(Keyboard.KEY_LEFT_SHIFT)) direction.y -= speed * flySpeedMultiplier;
        } else if(gameMode == CREATIVE_GAMEMODE) {
            if(flying) {
                if(keyboard.getPress(Keyboard.KEY_SPACE)) direction.y += speed * flySpeedMultiplier;
                if(keyboard.getPress(Keyboard.KEY_LEFT_SHIFT)) direction.y -= speed * flySpeedMultiplier;
            } else if(keyboard.getPress(Keyboard.KEY_SPACE) && canJump) jump(false);
            if(keyboard.getClick(Keyboard.KEY_SPACE)) {
                if(jumpClicks == 0) lastJumpTime = System.currentTimeMillis();
                else if(System.currentTimeMillis() / 1000.0 - lastJumpTime / 1000.0 <= FLY_JUMP_FREQUENCY) flying = !flying;
                if(canJump) flying = false;

                jumpClicks = jumpClicks + 1 >= 2 ? 0 : jumpClicks + 1;
            }
        } else if(keyboard.getPress(Keyboard.KEY_SPACE)) jump(false);
        if(new Vector2d(direction.x(), direction.z()).length() > 1.0) {
            Vector2d normalizedDirection = new Vector2d(direction.x(), direction.z()).normalize();
            direction.x = normalizedDirection.x();
            direction.z = normalizedDirection.y();
        }

        double withFlySpeed = speed * (flying ? flySpeedMultiplier : 1.0);

        direction.mul(withFlySpeed, 1.0, withFlySpeed);

        rotation.add(new Vector3d(new Vector2d(mouse.getDirection().y(), mouse.getDirection().x()).mul(!mouse.getGrabbed() ? 0.0 : -0.05), 0.0));
        rotation.x = MathUtil.clamp(rotation.x(), -90.0, 90.0);
        rotation.y = rotation.y() >= 360.0 || rotation.y() <= -360.0 ? 0.0 : rotation.y();

        update(world, time);

        camera.updateInBB(collider, new Vector3d(0.5, 0.888, 0.5));
        camera.rotation.set(rotation);

        double nearestDistance = Double.POSITIVE_INFINITY;
        RayHitResult nearestHitResult = null;
        AbstractBlock nearestBlock = null;

        if(gameMode != SPECTATOR_GAMEMODE) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(CubeCollider block : chunk.INTERACTIVE_BLOCKS) {
                    RayHitResult hitResult = new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), block.scale).processRaycast(camera.position, MathUtil.angleToDirection(new Vector2d(camera.rotation.x(), camera.rotation.y())));

                    if(hitResult != null && hitResult.hitPoint().distance(camera.position) < nearestDistance) {
                        nearestDistance = hitResult.hitPoint().distance(camera.position);
                        nearestHitResult = hitResult;
                        nearestBlock = chunk.getMap().get(new Vector3i((int) block.position.x(), (int) block.position.y(), (int) block.position.z()));
                    }
                }
            }

            if(nearestHitResult != null && nearestDistance <= 6.0) {
                if(mouse.getClick(Mouse.BUTTON_RIGHT) && !collider.checkCollision(new CubeCollider(new Vector3d(nearestHitResult.hitObject().position.x() + nearestHitResult.hitNormal().x(), nearestHitResult.hitObject().position.y() + nearestHitResult.hitNormal().y(), nearestHitResult.hitObject().position.z() + nearestHitResult.hitNormal().z()), nearestHitResult.hitObject().scale))) {
                    Vector3d blockPos = new Vector3d(nearestHitResult.hitObject().position).add(nearestHitResult.hitNormal());
                    Vector3i intBlockPos = new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z());

                    world.placeBlock(intBlockPos, Blocks.COBBLESTONE);
                    MyNewCraft.updateMesh(world, new Vector2i((int) Math.floor(intBlockPos.x() / 16.0), (int) Math.floor(intBlockPos.z() / 16.0)));
                }
                if(mouse.getClick(Mouse.BUTTON_LEFT)) {
                    if(nearestBlock instanceof Block block && block.getBreakable() || !(nearestBlock instanceof Block)) {
                        Vector3i blockPos = new Vector3i((int) nearestHitResult.hitObject().position.x(), (int) nearestHitResult.hitObject().position.y(), (int) nearestHitResult.hitObject().position.z());

                        world.removeBlock(blockPos);
                        MyNewCraft.updateMesh(world, new Vector2i((int) Math.floor(blockPos.x() / 16.0), (int) Math.floor(blockPos.z() / 16.0)));
                    }
                }

                selection.position = new Vector3d(nearestHitResult.hitObject().position);
                selection.enabled();
            } else selection.disabled();
        } else selection.disabled();
    }
    public void setGameMode(int gameMode) {
        this.gameMode = gameMode > 2 ? 0 : gameMode;
        processCollisions = gameMode != SPECTATOR_GAMEMODE;
        flying = gameMode == SPECTATOR_GAMEMODE;
    }

    public int getGameMode() {
        return gameMode;
    }
}