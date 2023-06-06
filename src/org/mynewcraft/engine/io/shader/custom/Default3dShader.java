package org.mynewcraft.engine.io.shader.custom;

import org.joml.Vector3d;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.shader.Shader;
import org.mynewcraft.engine.math.MathUtil;

public class Default3dShader extends Shader {
    protected final Window window;

    protected final String transform = "transform";
    protected final String view = "view";
    protected final String project = "project";

    public Default3dShader(String vertexLocation, String fragmentLocation, Window window) {
        super(vertexLocation, fragmentLocation);
        this.window = window;
    }

    public void transform(Vector3d position, Vector3d rotation, Vector3d scale) {
        setVariable(transform, MathUtil.transform(position, rotation, scale));
    }
    public void view(Vector3d position, Vector3d rotation) {
        setVariable(view, MathUtil.view(position, rotation));
    }
    public void project(double fov, double near, double far) {
        setVariable(project, MathUtil.project(window, fov, near, far));
    }
}