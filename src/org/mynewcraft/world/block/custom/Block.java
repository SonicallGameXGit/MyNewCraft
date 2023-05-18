package org.mynewcraft.world.block.custom;

import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.world.block.AbstractBlock;

public class Block extends AbstractBlock {
    private final Settings SETTINGS;

    public Block(Identifier identifier, Settings settings) {
        super(identifier);
        SETTINGS = settings.cloneSettings();
    }

    public boolean getBreakable() {
        return SETTINGS.breakable;
    }

    public static class Settings {
        private boolean breakable = true;

        public Settings() { }
        private Settings(boolean breakable) {
            this.breakable = breakable;
        }

        protected Settings cloneSettings() {
            return new Settings(breakable);
        }

        public Settings unbreakable() {
            this.breakable = false;
            return this;
        }
    }
}