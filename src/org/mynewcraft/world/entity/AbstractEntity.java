package org.mynewcraft.world.entity;

import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;

public class AbstractEntity {
    public CubeCollider collider;
    public Vector3d rotation;

    public AbstractEntity(CubeCollider collider, Vector3d rotation) {
        this.collider = collider;
        this.rotation = rotation;
    }
}