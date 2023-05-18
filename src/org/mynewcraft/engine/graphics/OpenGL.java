package org.mynewcraft.engine.graphics;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

public class OpenGL {
    public static void setClearColor(Vector3d backgroundColor) {
        GL11.glClearColor((float) backgroundColor.x, (float) backgroundColor.y, (float) backgroundColor.z, 1.0f);
    }
    public static void cullFace(boolean cull) {
        if(cull) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
    }
    public static void outlineOnly(boolean outline) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, outline ? GL11.GL_LINE : GL11.GL_FILL);
    }
    public static void outlineWidth(double width) {
        GL11.glLineWidth((float) width);
    }
    public static void enableDepthTest(boolean enable) {
        if(enable) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
}