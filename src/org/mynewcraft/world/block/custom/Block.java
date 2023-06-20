package org.mynewcraft.world.block.custom;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.entity.custom.FallingBlockEntity;
import org.mynewcraft.world.entity.custom.PlayerEntity;

public class Block extends AbstractBlock {
    private final Settings settings;

    public Block(Identifier identifier, Settings settings) {
        super(identifier);
        this.settings = settings.cloneSettings();
    }

    @Override
    public void onPlace(World world, PlayerEntity player, Vector3i position) {
        super.onPlace(world, player, position);

        if(settings.notStatic) {
            AbstractBlock blockAbove = world.getBlockAt(new Vector3i(position.x(), position.y() - 1, position.z()));
            if(blockAbove == null || (blockAbove instanceof Block && (((Block) blockAbove).getPassable()))) {
                world.removeBlock(position);
                world.spawnEntity(new FallingBlockEntity(new CubeCollider(new Vector3d(position), new Vector3d(1.0)), this, 1.0));
            }
        }
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
    public boolean getInnerView() {
        return settings.innerView;
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
        private boolean innerView = false;

        private double transparency = 0.0;

        public Settings() { }
        private Settings(boolean breakable, boolean passable, boolean notStatic, boolean replaceable, boolean waterLike, boolean ignoreRaycast, boolean innerView, double transparency) {
            this.breakable = breakable;
            this.passable = passable;
            this.notStatic = notStatic;
            this.replaceable = replaceable;
            this.waterLike = waterLike;
            this.ignoreRaycast = ignoreRaycast;
            this.innerView = innerView;
            this.transparency = transparency;
        }

        protected Settings cloneSettings() {
            return new Settings(breakable, passable, notStatic, replaceable, waterLike, ignoreRaycast, innerView, transparency);
        }

        /**
         * Makes block unbreakable for player, but you can break it directly from code!
         */
        public Settings unbreakable() {
            this.breakable = false;
            return this;
        }

        /**
         * Disables collider for entity physics, but not for raycast
         */
        public Settings passable() {
            this.passable = true;
            return this;
        }

        /**
         * Enables falling feature to make block fall when no block below found
         */
        public Settings notStatic() {
            notStatic = true;
            return this;
        }

        /**
         * Enables replacing feature, also enables {@link #ignoreRaycast() ignoreRaycast() } to make it work
         */
        public Settings replaceable() {
            replaceable = true;
            ignoreRaycast = true;
            return this;
        }

        /**
         * Makes block water-like, it means that entities can swim in it!
         */
        public Settings waterLike() {
            waterLike = true;
            return this;
        }

        /**
         * Makes block ignoring raycast, it means that player can't remove this block or place other on it
         */
        public Settings ignoreRaycast() {
            ignoreRaycast = true;
            return this;
        }

        /**
         * Makes block transparent
         * @param value transparency from 0.0 to 1.0
         */
        public Settings transparent(double value) {
            transparency = MathUtil.clamp(value, 0.0, 1.0);
            return this;
        }

        /**
         * Enables inner faces rendering
         */
        public Settings innerView() {
            innerView = true;
            transparency = 0.001;
            return this;
        }
    }
}