package org.mynewcraft.engine.time;

public class Time {
    private final double startTime;
    private final double breakDelta;

    private double lastTime;
    private double time;
    private double delta;

    public Time(double breakDelta) {
        lastTime = System.nanoTime();
        startTime = System.nanoTime();
        delta = 0.0;
        this.breakDelta = breakDelta;
    }

    public static long getMilliseconds() {
        return System.currentTimeMillis();
    }

    public void update() {
        double currentTime = System.nanoTime();

        delta = (currentTime - lastTime) / 1000000000.0;
        lastTime = currentTime;
        time = (System.nanoTime() - startTime) / 1000000000.0;
    }

    public double getDelta() { return delta >= breakDelta ? 0.0 : delta; }
    public double getTime() { return time; }

    public void pause() {
        delta = 0.0f;
    }
}