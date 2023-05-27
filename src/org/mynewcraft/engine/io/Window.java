package org.mynewcraft.engine.io;

import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.Sys;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final long window;

    private final int latestX;
    private final int latestY;
    private final int latestWidth;
    private final int latestHeight;

    private final Vector2d scrollDirection;

    private boolean fullscreen;

    public Window(double width, double height, String title, boolean resizable, boolean fullscreen, boolean verticalSynchronization) {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        scrollDirection = new Vector2d();

        if(fullscreen) {
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert videoMode != null;
            window = glfwCreateWindow(videoMode.width(), videoMode.height(), title, glfwGetPrimaryMonitor(), NULL);
        } else window = glfwCreateWindow((int) width, (int) height, title, NULL, NULL);
        if(window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowSizeCallback(window, (window, newWidth, newHeight) -> GL11.glViewport(0, 0, newWidth, newHeight));
        glfwSetScrollCallback(window, (window, directionX, directionY) -> {
            scrollDirection.add(directionX, directionY);
        });

        Vector2d screenSize = getScale();

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        assert videoMode != null;
        int centeredX = (videoMode.width() - (int) screenSize.x) / 2;
        int centeredY = (videoMode.height() - (int) screenSize.y) / 2;

        latestX = centeredX;
        latestY = centeredY;
        latestWidth = (int) width;
        latestHeight = (int) height;

        glfwSetWindowPos(window, centeredX, centeredY);
        glfwMakeContextCurrent(window);

        glfwSwapInterval(verticalSynchronization ? 1 : 0);

        glfwShowWindow(window);

        GL.createCapabilities();

        GL11.glViewport(0, 0, (int) screenSize.x, (int) screenSize.y);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_BACK);

        this.fullscreen = fullscreen;
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void close() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }
    public void setFullscreen(boolean fullscreen) {
        int refreshRate = 0;
        if(fullscreen) {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert videoMode != null;
            refreshRate = videoMode.refreshRate();

            glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), refreshRate);
        } else {
            glfwSetWindowMonitor(window, NULL, latestX, latestY, latestWidth, latestHeight, refreshRate);
        }

        this.fullscreen = fullscreen;
    }
    public void setIcon(String location) {
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        IntBuffer imageWidth = BufferUtils.createIntBuffer(1);
        IntBuffer imageHeight = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(location, imageWidth, imageHeight, comp, 4);

        if(image == null) {
            System.err.println(location + " not found");
            System.exit(1);
        }
        else {
            GLFWImage icon = GLFWImage.malloc();
            GLFWImage.Buffer buffer = GLFWImage.malloc(1);
            icon.set(imageWidth.get(), imageHeight.get(), image);
            buffer.put(0, icon);
            glfwSetWindowIcon(window, buffer);
        }
    }

    public boolean getRunning() {
        return !glfwWindowShouldClose(window);
    }

    public long getId() {
        return window;
    }
    public Vector2d getScale() {
        IntBuffer width;
        IntBuffer height;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            width = stack.mallocInt(1);
            height = stack.mallocInt(1);

            glfwGetWindowSize(window, width, height);
        }

        return new Vector2d(width.get(), height.get());
    }
    public Vector2d getScrollDirection() {
        return scrollDirection;
    }

    public boolean getFullscreen() {
        return fullscreen;
    }
}