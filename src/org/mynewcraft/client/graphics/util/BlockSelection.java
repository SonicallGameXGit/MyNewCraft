package org.mynewcraft.client.graphics.util;

import org.joml.Vector3d;
import org.mynewcraft.engine.graphics.OpenGL;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.io.texture.Texture;

public class BlockSelection extends Mesh {
    private boolean enabled = false;

    public Vector3d position;

    public BlockSelection() {
        super(null, new float[] {
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        }, new float[108], null, LINES, false);

        position = new Vector3d();
    }

    public void render() {
        if(enabled) {
            OpenGL.cullFace(false);
            OpenGL.outlineOnly(true);

            render((Texture) null);

            OpenGL.outlineOnly(false);
            OpenGL.cullFace(true);
        }
    }
    public void enabled() {
        enabled = true;
    }
    public void disabled() {
        enabled = false;
    }
}