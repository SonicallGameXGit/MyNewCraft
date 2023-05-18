package org.mynewcraft.engine.math.physics;

import org.joml.Vector3d;

public record RayHitResult(Vector3d hitPoint, Vector3d hitNormal, CubeCollider hitObject) { }