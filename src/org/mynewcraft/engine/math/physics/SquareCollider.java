package org.mynewcraft.engine.math.physics;

import org.joml.Vector2d;

public class SquareCollider {
    public Vector2d position;
    public Vector2d scale;

    public SquareCollider(Vector2d position, Vector2d scale) {
        this.position = position;
        this.scale = scale;
    }

    public boolean checkCollision(SquareCollider b) {
        if(position.x() + scale.x() >= b.position.x() && position.x() <= b.position.x() + b.scale.x()) {
            return position.y() + scale.y() >= b.position.y() && position.y() <= b.position.y() + b.scale.y();
        }

        return false;
    }

    public RayHitResult2d processRaycast(Vector2d rayOrigin, Vector2d rayDirection) {
        double t1 = (position.x() - rayOrigin.x()) / rayDirection.x();
        double t2 = (position.x() + scale.x() - rayOrigin.x()) / rayDirection.x();
        double t3 = (position.y() - rayOrigin.y()) / rayDirection.y();
        double t4 = (position.y() + scale.y() - rayOrigin.y()) / rayDirection.y();
        double tMin = Math.max(Math.min(t1, t2), Math.min(t3, t4));
        double tMax = Math.min(Math.max(t1, t2), Math.max(t3, t4));

        Vector2d hitPoint;

        if(tMax < 0.0 || tMin > tMax) return null;
        if(tMin < 0.0) hitPoint = new Vector2d(rayOrigin).add(new Vector2d(rayDirection).mul(tMax));
        else hitPoint = new Vector2d(rayOrigin).add(new Vector2d(rayDirection).mul(tMin));

        Vector2d blockCenter = new Vector2d(
                position.x() + scale.x() / 2.0,
                position.y() + scale.y() / 2.0
        );
        Vector2d nearestNormal = new Vector2d();

        double nearestNormalDistance = 1000.0;
        if(new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(0.0, 1.0)) < nearestNormalDistance) {
            nearestNormal = new Vector2d(0.0, 1.0);
            nearestNormalDistance = new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(0.0, 1.0));
        }
        if(new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(0.0, -1.0)) < nearestNormalDistance) {
            nearestNormal = new Vector2d(0.0, -1.0);
            nearestNormalDistance = new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(0.0, -1.0));
        }
        if(new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(1.0, 0.0)) < nearestNormalDistance) {
            nearestNormal = new Vector2d(1.0, 0.0);
            nearestNormalDistance = new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(1.0, 0.0));
        }
        if(new Vector2d(hitPoint).sub(blockCenter).distance(new Vector2d(-1.0, 0.0)) < nearestNormalDistance)
            nearestNormal = new Vector2d(-1.0, 0.0);

        return new RayHitResult2d(hitPoint, nearestNormal, this);
    }
}