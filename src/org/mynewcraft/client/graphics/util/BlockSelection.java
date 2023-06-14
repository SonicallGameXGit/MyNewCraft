package org.mynewcraft.client.graphics.util;

import org.joml.Vector3d;
import org.mynewcraft.engine.graphics.OpenGl;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.graphics.mesh.MeshBuffer;
import org.mynewcraft.engine.io.texture.Texture;

public class BlockSelection extends Mesh {
    private boolean enabled = false;

    public Vector3d position;

    public BlockSelection() {
        super(null, new MeshBuffer(new float[] {
                -0.001f, -0.001f, 1.001f,
                1.001f, -0.001f, 1.001f,
                1.001f, -0.001f, 1.001f,
                1.001f, 1.001f, 1.001f,
                1.001f, 1.001f, 1.001f,
                -0.001f, 1.001f, 1.001f,

                -0.001f, -0.001f, -0.001f,
                1.001f, -0.001f, -0.001f,
                1.001f, -0.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                -0.001f, 1.001f, -0.001f,

                -0.001f, 1.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, 1.001f, 1.001f,
                1.001f, 1.001f, 1.001f,
                -0.001f, 1.001f, 1.001f,

                -0.001f, -0.001f, -0.001f,
                1.001f, -0.001f, -0.001f,
                1.001f, -0.001f, -0.001f,
                1.001f, -0.001f, 1.001f,
                1.001f, -0.001f, 1.001f,
                -0.001f, -0.001f, 1.001f,

                1.001f, -0.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, 1.001f, 1.001f,
                1.001f, 1.001f, 1.001f,
                1.001f, -0.001f, 1.001f,

                -0.001f, -0.001f, -0.001f,
                -0.001f, 1.001f, -0.001f,
                -0.001f, 1.001f, -0.001f,
                -0.001f, 1.001f, 1.001f,
                -0.001f, 1.001f, 1.001f,
                -0.001f, -0.001f, 1.001f
        }, 3), new MeshBuffer(new float[108], 2), null, LINES, false);

        position = new Vector3d();
    }

    public void render() {
        if(enabled) {
            OpenGl.outlineOnly(true);
            render((Texture) null);
            OpenGl.outlineOnly(false);
        }
    }
    public void enabled() {
        enabled = true;
    }
    public void disabled() {
        enabled = false;
    }
}