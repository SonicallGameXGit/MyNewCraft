package org.mynewcraft.engine.graphics;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.mynewcraft.engine.io.Window;

import java.nio.ByteBuffer;

public class ScreenTexture {
    public static final byte COLOR_ONLY = 0;
    public static final byte DEPTH_ONLY = 1;
    public static final byte COLOR_AND_DEPTH = 2;

    private final double width;
    private final double height;

    private final int frameBufferId;
    private final int renderBufferId;

    private int colorTextureId;
    private int depthTextureId;

    public ScreenTexture(double width, double height, int filter, byte texturesToRender) throws Exception {
        this.width = width;
        this.height = height;

        frameBufferId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);

        renderBufferId = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBufferId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, (int) width, (int) height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBufferId);

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
            throw new Exception("Could not create framebuffer.");
        if(texturesToRender == COLOR_AND_DEPTH) {
            colorTextureId = createTexture(width, height, filter, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE);
            depthTextureId = createTexture(width, height, filter, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_DEPTH_COMPONENT, GL11.GL_FLOAT);
        } else if(texturesToRender == COLOR_ONLY)
            colorTextureId = createTexture(width, height, filter, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE);
        else if(texturesToRender == DEPTH_ONLY)
            depthTextureId = createTexture(width, height, filter, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_DEPTH_COMPONENT, GL11.GL_FLOAT);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void load(Window window, Vector3d clearColor) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);
        GL11.glViewport(0, 0, (int) width, (int) height);

        window.clear();
        OpenGl.setClearColor(clearColor);
    }
    public void unload(Window window, Vector3d clearColor) {
        GL11.glViewport(0, 0, (int) window.getScale().x(), (int) window.getScale().y());
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        window.clear();
        OpenGl.setClearColor(clearColor);
    }
    public void clear() {
        GL30.glDeleteFramebuffers(frameBufferId);

        GL11.glDeleteTextures(colorTextureId);
        GL11.glDeleteTextures(depthTextureId);
    }

    private static int createTexture(double width, double height, int filter, int attachment, int colorMode, int dataType) {
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colorMode, (int) width, (int) height, 0, colorMode, dataType, (ByteBuffer) null);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, textureId, 0);

        return textureId;
    }

    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }

    public int getFramebufferId() {
        return frameBufferId;
    }
    public int getRenderBufferId() {
        return renderBufferId;
    }
    public int getColorTextureId() {
        return colorTextureId;
    }
    public int getDepthTextureId() {
        return depthTextureId;
    }
}
