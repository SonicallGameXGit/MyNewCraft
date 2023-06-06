package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.entity.AbstractEntity;

public abstract class LivingEntity extends AbstractEntity {
    protected boolean canJump;

    public double mass;
    public double speed;
    public double jumpPower;

    public Vector3d direction;

    public boolean processCollisions;
    public boolean applyPhysics;

    public LivingEntity(CubeCollider collider, Vector3d rotation, double mass, double speed, double jumpPower) {
        super(collider, rotation);

        this.mass = mass;
        this.speed = speed;
        this.jumpPower = jumpPower;

        canJump = false;
        direction = new Vector3d();
        processCollisions = true;
        applyPhysics = true;
    }

    protected void update(World world, Time time) {
        if(applyPhysics) direction.y -= world.gravity * mass * time.getDelta();
        canJump = false;

        collider.position.y += direction.y() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(CubeCollider block : chunk.getInteractiveBlocks()) {
                    if(new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), block.scale).checkCollision(collider)) {
                        collider.position.y -= direction.y() * time.getDelta();

                        canJump = direction.y() < 0.0;
                        direction.y = 0.0;

                        break;
                    }
                }
            }
        }

        collider.position.x += direction.x() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(CubeCollider block : chunk.getInteractiveBlocks()) {
                    if(new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), block.scale).checkCollision(collider)) {
                        collider.position.x -= direction.x() * time.getDelta();
                        break;
                    }
                }
            }
        }

        collider.position.z += direction.z() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(CubeCollider block : chunk.getInteractiveBlocks()) {
                    if(new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), block.scale).checkCollision(collider)) {
                        collider.position.z -= direction.z() * time.getDelta();
                        break;
                    }
                }
            }
        }
    }
    protected void jump(boolean ignoreGround) {
        if(canJump || ignoreGround) {
            direction.y = jumpPower;
            canJump = false;
        }
    }
}