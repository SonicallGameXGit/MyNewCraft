package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.BlockCollider;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.entity.AbstractEntity;

public abstract class LivingEntity extends AbstractEntity {
    public static final String PASSABLE_TAG = "passable";
    public static final String WATER_LIKE_TAG = "water_like";

    protected boolean canJump;
    protected boolean underwater;

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
        underwater = false;
        processCollisions = true;
        applyPhysics = true;

        direction = new Vector3d();
    }

    protected void update(World world, Time time) {
        if(applyPhysics) direction.y = underwater ? (direction.y() <= 0.0 ? world.gravity * mass / world.waterDeceleration * time.getDelta() : direction.y() / world.waterDeceleration) : (direction.y() - world.gravity * mass * time.getDelta());
        canJump = false;
        underwater = false;

        if(processCollisions) {
            for(AbstractEntity entity : world.ENTITIES) {
                if(entity.collider.checkCollision(collider)) {
                    direction.x -= (entity.collider.position.x() - collider.position.x()) / 6.0 * mass;
                    direction.z -= (entity.collider.position.z() - collider.position.z()) / 6.0 * mass;
                }
            }
        }

        collider.position.y += direction.y() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(BlockCollider block : chunk.getInteractiveBlocks()) {
                    if(!block.containsTag(PASSABLE_TAG) && new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * World.chunkScale, 0.0, chunk.getOffset().y() * World.chunkScale), block.scale).checkCollision(collider)) {
                        boolean hasWaterTag = block.containsTag(WATER_LIKE_TAG);

                        collider.position.y -= direction.y() * time.getDelta() * (hasWaterTag ? 0.0 : 1.0);
                        canJump = direction.y() < 0.0 || hasWaterTag;

                        direction.y = hasWaterTag ? direction.y() : 0.0;

                        if(hasWaterTag) underwater = true;

                        break;
                    }
                }
            }
        }

        collider.position.x += direction.x() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(BlockCollider block : chunk.getInteractiveBlocks()) {
                    if(!block.containsTag(PASSABLE_TAG) && new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * World.chunkScale, 0.0, chunk.getOffset().y() * World.chunkScale), block.scale).checkCollision(collider)) {
                        boolean hasWaterTag = block.containsTag(WATER_LIKE_TAG);
                        collider.position.x -= direction.x() / (hasWaterTag ? world.waterDeceleration : 1.0) * time.getDelta();

                        if(hasWaterTag) underwater = true;
                        break;
                    }
                }
            }
        }

        collider.position.z += direction.z() * time.getDelta();
        if(processCollisions) {
            for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
                for(BlockCollider block : chunk.getInteractiveBlocks()) {
                    if(!block.containsTag(PASSABLE_TAG) && new CubeCollider(new Vector3d(block.position).add(chunk.getOffset().x() * World.chunkScale, 0.0, chunk.getOffset().y() * World.chunkScale), block.scale).checkCollision(collider)) {
                        boolean hasWaterTag = block.containsTag(WATER_LIKE_TAG);
                        collider.position.z -= direction.z() / (hasWaterTag ? world.waterDeceleration : 1.0) * time.getDelta();

                        if(hasWaterTag) underwater = true;
                        break;
                    }
                }
            }
        }
    }
    protected void jump(boolean ignoreGround, World world, Time time) {
        if(canJump || ignoreGround) {
            direction.y = underwater ? (direction.y() + jumpPower / world.waterDeceleration * time.getDelta()) : jumpPower;
            canJump = underwater;
        }
    }
}