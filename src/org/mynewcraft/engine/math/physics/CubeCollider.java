package org.mynewcraft.engine.math.physics;

import org.joml.Vector3d;

public class CubeCollider {
    public Vector3d position;
    public Vector3d scale;

    public CubeCollider(Vector3d position, Vector3d scale) {
        this.position = new Vector3d(position);
        this.scale = new Vector3d(scale);
    }

    public boolean checkCollision(CubeCollider b) {
        if(position.x() <= b.position.x() + b.scale.x() && position.x() + scale.x() >= b.position.x())
            if(position.y() <= b.position.y() + b.scale.y() && position.y() + scale.y() >= b.position.y())
                return position.z() <= b.position.z() + b.scale.z() && position.z() + scale.z() >= b.position.z();

        return false;
    }

    public RayHitResult processRaycast(Vector3d rayOrigin, Vector3d rayDirection) {
        double t1 = (position.x() - rayOrigin.x()) / rayDirection.x();
        double t2 = (position.x() + scale.x() - rayOrigin.x()) / rayDirection.x();
        double t3 = (position.y() - rayOrigin.y()) / rayDirection.y();
        double t4 = (position.y() + scale.y() - rayOrigin.y()) / rayDirection.y();
        double t5 = (position.z() - rayOrigin.z()) / rayDirection.z();
        double t6 = (position.z() + scale.z() - rayOrigin.z()) / rayDirection.z();
        double tMin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tMax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        Vector3d hitPoint;

        if(tMax < 0.0 || tMin > tMax) return null;
        if(tMin < 0.0) hitPoint = new Vector3d(rayOrigin).add(new Vector3d(rayDirection).mul(tMax));
        else hitPoint = new Vector3d(rayOrigin).add(new Vector3d(rayDirection).mul(tMin));

        Vector3d blockCenter = new Vector3d(
                position.x() + scale.x() / 2.0,
                position.y() + scale.y() / 2.0,
                position.z() + scale.z() / 2.0
        );
        Vector3d nearestNormal = new Vector3d();

        double nearestNormalDistance = 1000.0;
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 0.0, 1.0)) < nearestNormalDistance) {
            nearestNormal = new Vector3d(0.0, 0.0, 1.0);
            nearestNormalDistance = new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 0.0, 1.0));
        }
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 0.0, -1.0)) < nearestNormalDistance) {
            nearestNormal = new Vector3d(0.0, 0.0, -1.0);
            nearestNormalDistance = new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 0.0, -1.0));
        }
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 1.0, 0.0)) < nearestNormalDistance) {
            nearestNormal = new Vector3d(0.0, 1.0, 0.0);
            nearestNormalDistance = new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, 1.0, 0.0));
        }
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, -1.0, 0.0)) < nearestNormalDistance) {
            nearestNormal = new Vector3d(0.0, -1.0, 0.0);
            nearestNormalDistance = new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(0.0, -1.0, 0.0));
        }
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(1.0, 0.0, 0.0)) < nearestNormalDistance) {
            nearestNormal = new Vector3d(1.0, 0.0, 0.0);
            nearestNormalDistance = new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(1.0, 0.0, 0.0));
        }
        if(new Vector3d(hitPoint).sub(blockCenter).distance(new Vector3d(-1.0, 0.0, 0.0)) < nearestNormalDistance)
            nearestNormal = new Vector3d(-1.0, 0.0, 0.0);

        return new RayHitResult(hitPoint, nearestNormal, this);
    }
}