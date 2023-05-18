package org.mynewcraft.world.entity.custom;

import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.entity.AbstractEntity;

public class LivingEntity extends AbstractEntity {
    public double mass;
    public double speed;

    public Vector3d direction;

    public LivingEntity(CubeCollider collider, Vector3d rotation, double mass, double speed) {
        super(collider, rotation);

        this.mass = mass;
        this.speed = speed;

        direction = new Vector3d();
    }

    public void update(Time time) {
        collider.position.add(direction.mul(speed * time.getDelta()));
    }
}