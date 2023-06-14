package org.mynewcraft.world.block;

import org.joml.Vector3d;
import org.mynewcraft.engine.math.physics.CubeCollider;

import java.util.ArrayList;
import java.util.Objects;

public class BlockCollider extends CubeCollider {
    private final ArrayList<String> tags = new ArrayList<>();

    public BlockCollider(Vector3d position, Vector3d scale) {
        super(position, scale);
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
