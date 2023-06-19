package org.mynewcraft.engine.io.shader.custom;

import org.joml.Matrix4f;
import org.mynewcraft.engine.io.shader.Shader;

public class Default2dShader extends Shader {
    public Default2dShader(String vertexLocation, String fragmentLocation) {
        super(vertexLocation, fragmentLocation);
    }

    public void project(double left, double right, double top, double bottom) {
        setVariable("project", new Matrix4f().ortho2D((float) left, (float) right, (float) bottom, (float) top));
    }
}
