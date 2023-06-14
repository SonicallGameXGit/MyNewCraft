package org.mynewcraft.world.block.custom;

import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.world.block.AbstractBlock;

public class Block extends AbstractBlock {
    private final Settings settings;

    public Block(Identifier identifier, Settings settings) {
        super(identifier);
        this.settings = settings.cloneSettings();
    }

    public boolean getBreakable() {
        return settings.breakable;
    }
    public boolean getPassable() {
        return settings.passable;
    }
    public boolean getStatic() {
        return !settings.notStatic;
    }
    public boolean getReplaceable() {
        return settings.replaceable;
    }
    public boolean getWaterLike() {
        return settings.waterLike;
    }
    public boolean getRaycastIgnore() {
        return settings.ignoreRaycast;
    }

    public double getTransparency() {
        return settings.transparency;
    }

    public static class Settings {
        private boolean breakable = true;
        private boolean passable = false;
        private boolean notStatic = false;
        private boolean replaceable = false;
        private boolean waterLike = false;
        private boolean ignoreRaycast = false;

        private double transparency = 0.0;

        public Settings() { }
        private Settings(boolean breakable, boolean passable, boolean notStatic, boolean replaceable, boolean waterLike, boolean ignoreRaycast, double transparency) {
            this.breakable = breakable;
            this.passable = passable;
            this.notStatic = notStatic;
            this.replaceable = replaceable;
            this.waterLike = waterLike;
            this.ignoreRaycast = ignoreRaycast;
            this.transparency = transparency;
        }

        protected Settings cloneSettings() {
            return new Settings(breakable, passable, notStatic, replaceable, waterLike, ignoreRaycast, transparency);
        }

        public Settings unbreakable() {
            this.breakable = false;
            return this;
        }
        public Settings passable() {
            this.passable = true;
            return this;
        }
        public Settings notStatic() {
            notStatic = true;
            return this;
        }
        public Settings replaceable() {
            replaceable = true;
            return this;
        }
        public Settings waterLike() {
            waterLike = true;
            return this;
        }
        public Settings ignoreRaycast() {
            ignoreRaycast = true;
            return this;
        }
        public Settings transparent(double value) {
            transparency = MathUtil.clamp(value, 0.0, 1.0);
            return this;
        }
    }
}