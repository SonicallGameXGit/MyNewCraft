package org.mynewcraft.world.block;

import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.world.block.custom.Block;
import org.mynewcraft.world.entity.custom.LivingEntity;
import org.mynewcraft.world.entity.custom.PlayerEntity;

import java.util.ArrayList;
import java.util.Objects;

public class BlockCollider extends CubeCollider {
    private final ArrayList<String> tags = new ArrayList<>();

    public BlockCollider(Vector3d position, Vector3d scale) {
        super(position, scale);
    }

    public static BlockCollider of(AbstractBlock block) {
        BlockCollider collider = new BlockCollider(new Vector3d(), new Vector3d(1.0));

        if(block instanceof Block blockB) {
            if(blockB.getPassable())
                collider.addTag(LivingEntity.PASSABLE_TAG);
            if(blockB.getWaterLike())
                collider.addTag(LivingEntity.WATER_LIKE_TAG);
            if(blockB.getRaycastIgnore())
                collider.addTag(PlayerEntity.IGNORE_RAYCAST_TAG);
        }

        return collider;
    }

    public BlockCollider addTag(String string) {
        tags.add(string);
        return this;
    }

    public String[] getTags() {
        return tags.toArray(new String[0]);
    }

    public boolean containsTag(String tag) {
        for(String currentTag : tags)
            if(Objects.equals(currentTag, tag))
                return true;

        return false;
    }
}
