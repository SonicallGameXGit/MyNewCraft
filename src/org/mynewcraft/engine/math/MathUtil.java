package org.mynewcraft.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.mynewcraft.engine.io.Window;

public class MathUtil {
    public static Matrix4f project(Window window, double fieldOfView, double near, double far) {
        Matrix4f matrix;

        Vector2d windowResolution = window.getScale();

        double aspect = windowResolution.x() / windowResolution.y();
        double scaleY = 1.0 / Math.tan(Math.toRadians(fieldOfView / 2.0));
        double scaleX = scaleY / aspect;
        double frustum = far - near;

        matrix = new Matrix4f();
        matrix.m00((float) scaleX);
        matrix.m11((float) scaleY);
        matrix.m22((float) -((far + near) / frustum));
        matrix.m23(-1.0f);
        matrix.m32((float) -((2.0 * near * far) / frustum));
        matrix.m33(0.0f);

        return matrix;
    }

    public static Matrix4f view(Vector3d position, Vector3d rotation) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rotation.x()), new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.y()), new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.z()), new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.translate(new Vector3f((float) -position.x(), (float) -position.y(), (float) -position.z()));

        return matrix;
    }

    public static Matrix4f transform(Vector3d position, Vector3d rotation, Vector3d scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(new Vector3f((float) position.x(), (float) position.y(), (float) position.z()));
        matrix.rotate((float) Math.toRadians(rotation.x()), new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.y()), new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.z()), new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.scale(new Vector3f((float) scale.x(), (float) scale.y(), (float) scale.z()));

        return matrix;
    }

    public static Matrix4f transform(Vector3d position, Vector3d rotation, Vector3d scale, Vector3d anchor) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(new Vector3f((float) position.x(), (float) position.y(), (float) position.z()));
        matrix.translate(new Vector3f((float) anchor.x(), (float) anchor.y(), (float) anchor.z()));
        matrix.scale(new Vector3f((float) scale.x(), (float) scale.y(), (float) scale.z()));
        matrix.translate(new Vector3f((float) -anchor.x(), (float) -anchor.y(), (float) -anchor.z()));
        matrix.translate(new Vector3f((float) anchor.x(), (float) anchor.y(), (float) anchor.z()).div((float) scale.x(), (float) scale.y(), (float) scale.z()));
        matrix.rotate((float) Math.toRadians(rotation.x()), new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.y()), new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate((float) Math.toRadians(rotation.z()), new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.translate(new Vector3f((float) -anchor.x(), (float) -anchor.y(), (float) -anchor.z()).div((float) scale.x(), (float) scale.y(), (float) scale.z()));

        return matrix;
    }

    public static Vector3d clamp(Vector3d origin, Vector3d minimum, Vector3d maximum) {
        Vector3d result = new Vector3d(origin);
        result.min(result.max(minimum), maximum);

        return result;
    }

    public static Vector2d clamp(Vector2d origin, Vector2d minimum, Vector2d maximum) {
        Vector2d result = new Vector2d(origin);
        result.min(result.max(minimum), maximum);

        return result;
    }

    public static double clamp(double origin, double minimum, double maximum) {
        return Math.min(Math.max(origin, minimum), maximum);
    }

    public static Vector3d smooth(Vector3d origin, Vector3d destination, Vector3d sharpness) {
        return new Vector3d(origin).add(new Vector3d(destination).sub(origin).mul(clamp(sharpness, new Vector3d(), new Vector3d(1.0))));
    }

    public static Vector2d smooth(Vector2d origin, Vector2d destination, Vector2d sharpness) {
        return new Vector2d(origin).add(new Vector2d(destination).sub(origin).mul(clamp(sharpness, new Vector2d(), new Vector2d(1.0))));
    }

    public static double smooth(double origin, double destination, double sharpness) {
        return origin + (destination - origin) * clamp(sharpness, 0.0, 1.0);
    }

    public static Vector3d angleToDirection(Vector2d angle) {
        return new Vector3d(
                Math.sin(Math.toRadians(angle.y())) * Math.cos(Math.toRadians(angle.x())),
                -Math.sin(Math.toRadians(angle.x())),
                -Math.cos(Math.toRadians(angle.y())) * Math.cos(Math.toRadians(angle.x()))
        );
    }
    public static Vector2d angleToDirection(double angle) {
        return new Vector2d(
                Math.sin(Math.toRadians(angle)),
                -Math.cos(Math.toRadians(angle))
        );
    }
}