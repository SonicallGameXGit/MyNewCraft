package org.mynewcraft.world.entity.custom;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.custom.Block;
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
            for(int x = (int) Math.floor(collider.position.x()) - 1; x <= (int) Math.floor(collider.position.x() + collider.scale.x()) + 1; x++) {
                for(int y = (int) Math.floor(collider.position.y()) - 1; y <= (int) Math.floor(collider.position.y() + collider.scale.y()) + 1; y++) {
                    for(int z = (int) Math.floor(collider.position.z()) - 1; z <= (int) Math.floor(collider.position.z() + collider.scale.z()) + 1; z++) {
                        AbstractBlock block = world.getBlockAt(new Vector3i(x, y, z));
                        if(block != null && new CubeCollider(new Vector3d(x, y, z), new Vector3d(1.0)).checkCollision(collider)) {
                            if(block instanceof Block blockB) {
                                if(!blockB.getPassable()) {
                                    boolean hasWaterTag = blockB.getWaterLike();

                                    collider.position.y -= direction.y() * time.getDelta() * (hasWaterTag ? 0.0 : 1.0);
                                    canJump = direction.y() < 0.0 || hasWaterTag;

                                    direction.y = hasWaterTag ? direction.y() : 0.0;

                                    if(hasWaterTag) underwater = true;
                                    break;
                                }
                            } else {
                                collider.position.y -= direction.y() * time.getDelta();
                                canJump = direction.y() < 0.0 ;

                                direction.y = 0.0;
                                break;
                            }
                        }
                    }
                }
            }
        }

        collider.position.x += direction.x() * time.getDelta();
        if(processCollisions) {
            for(int x = (int) Math.floor(collider.position.x()) - 1; x <= (int) Math.floor(collider.position.x() + collider.scale.x()) + 1; x++) {
                for(int y = (int) Math.floor(collider.position.y()) - 1; y <= (int) Math.floor(collider.position.y() + collider.scale.y()) + 1; y++) {
                    for(int z = (int) Math.floor(collider.position.z()) - 1; z <= (int) Math.floor(collider.position.z() + collider.scale.z()) + 1; z++) {
                        AbstractBlock block = world.getBlockAt(new Vector3i(x, y, z));
                        if(block != null && new CubeCollider(new Vector3d(x, y, z), new Vector3d(1.0)).checkCollision(collider)) {
                            if(block instanceof Block blockB) {
                                if(!blockB.getPassable()) {
                                    boolean hasWaterTag = blockB.getWaterLike();
                                    collider.position.x -= direction.x() / (hasWaterTag ? world.waterDeceleration : 1.0) * time.getDelta();
                                    direction.x = 0.0;

                                    if(hasWaterTag) underwater = true;
                                    break;
                                }
                            } else {
                                collider.position.x -= direction.x() * time.getDelta();
                                direction.x = 0.0;

                                break;
                            }
                        }
                    }
                }
            }
        }

        collider.position.z += direction.z() * time.getDelta();
        if(processCollisions) {
            for(int x = (int) Math.floor(collider.position.x()) - 1; x <= (int) Math.floor(collider.position.x() + collider.scale.x()) + 1; x++) {
                for(int y = (int) Math.floor(collider.position.y()) - 1; y <= (int) Math.floor(collider.position.y() + collider.scale.y()) + 1; y++) {
                    for(int z = (int) Math.floor(collider.position.z()) - 1; z <= (int) Math.floor(collider.position.z() + collider.scale.z()) + 1; z++) {
                        AbstractBlock block = world.getBlockAt(new Vector3i(x, y, z));
                        if(block != null && new CubeCollider(new Vector3d(x, y, z), new Vector3d(1.0)).checkCollision(collider)) {
                            if(block instanceof Block blockB) {
                                if(!blockB.getPassable()) {
                                    boolean hasWaterTag = blockB.getWaterLike();
                                    collider.position.z -= direction.z() / (hasWaterTag ? world.waterDeceleration : 1.0) * time.getDelta();
                                    direction.z = 0.0;

                                    if(hasWaterTag) underwater = true;
                                    break;
                                }
                            } else {
                                collider.position.z -= direction.z() * time.getDelta();
                                direction.z = 0.0;

                                break;
                            }
                        }
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