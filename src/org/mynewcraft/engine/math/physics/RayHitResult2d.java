package org.mynewcraft.engine.math.physics;

import org.joml.Vector2d;

public record RayHitResult2d(Vector2d hitPoint, Vector2d hitNormal, SquareCollider hitObject) { }