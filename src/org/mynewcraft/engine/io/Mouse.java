package org.mynewcraft.engine.io;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Mouse {
    public static final int
            BUTTON_LEFT   = 0,
            BUTTON_RIGHT  = 1,
            BUTTON_MIDDLE = 2,
            BUTTON_4      = 3,
            BUTTON_5      = 4,
            BUTTON_6      = 5,
            BUTTON_7      = 6,
            BUTTON_8      = 7;

    private final Window window;

    private final Vector2d direction;
    private final Vector2d lastPosition;

    private final Vector2d lastScroll;
    private final Vector2d scroll;
    private final Vector2d scrollDirection;

    private final Map<Integer, Boolean> canClick = new HashMap<>();

    private boolean grabbed;

    public Mouse(Window window) {
        this.window = window;
        lastPosition = new Vector2d();
        direction = new Vector2d();
        lastScroll = new Vector2d();
        scroll = new Vector2d();
        scrollDirection = new Vector2d();
        grabbed = false;

        for(int i = 0; i <= 8; i++) canClick.put(i, false);
    }

    public boolean getPress(int button) {
        return GLFW.glfwGetMouseButton(window.getId(), button) == GLFW.GLFW_PRESS;
    }
    public boolean getClick(int button) {
        boolean clicked = canClick.get(button) && GLFW.glfwGetMouseButton(window.getId(), button) == GLFW.GLFW_PRESS;
        canClick.replace(button, GLFW.glfwGetMouseButton(window.getId(), button) == GLFW.GLFW_RELEASE);

        return clicked;
    }

    public void grab(boolean grabbed) {
        GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, grabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
        this.grabbed = grabbed;
    }
    public void update() {
        Vector2d currentPosition = getPosition();

        direction.set(lastPosition.sub(currentPosition));
        lastPosition.set(currentPosition);
        scroll.set(window.getScrollDirection());
        scrollDirection.set(new Vector2d(scroll).sub(lastScroll));
        lastScroll.set(new Vector2d(scroll));
    }
    public void setPosition(Vector2d position) {
        GLFW.glfwSetCursorPos(window.getId(), position.x(), position.y());
    }

    public Vector2d getPosition() {
        double[] x = new double[1];
        double[] y = new double[1];

        GLFW.glfwGetCursorPos(window.getId(), x, y);

        return new Vector2d(x[0], y[0]);
    }
    public Vector2d getDirection() {
        return direction;
    }
    public Vector2d getScrollDirection() {
        return scrollDirection;
    }

    public boolean getGrabbed() {
        return grabbed;
    }
}