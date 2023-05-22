package org.mynewcraft.client.graphics;

import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;

public class Camera {
    public Vector3d position;
    public Vector3d rotation;

    public Camera(Vector3d position, Vector3d rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void updateInBB(CubeCollider origin, Vector3d offset) {
        position = new Vector3d(origin.position.x() + origin.scale.x() * offset.x(), origin.position.y() + origin.scale.y() * offset.y(), origin.position.z() + origin.scale.z() * offset.z());
    }
}