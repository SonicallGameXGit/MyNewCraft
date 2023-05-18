package org.mynewcraft.world.chunk;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;

import java.util.*;

public class Chunk {
    private final Vector2i OFFSET;
    private final long SEED;
    private final Map<Vector3i, AbstractBlock> BLOCKS = new HashMap<>();

    public Chunk(Vector2i offset, int seed) {
        OFFSET = offset;
        SEED = seed;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                double height = Noise.valueCoherentNoise3D(x / 64.0, 0, z / 64.0, seed, NoiseQuality.FAST) * 64.0 + 64.0;

                for(int y = 1; y < height; y++) {
                    AbstractBlock block = Blocks.STONE;

                    if(y >= height - new Random(seed + x * 38506L + y * 101950L).nextDouble() * 5.0 - 1.0)
                        block = Blocks.DIRT;
                    if(y >= height - 1) block = Blocks.GRASS_BLOCK;

                    BLOCKS.put(new Vector3i(x, y, z), block);
                }

                BLOCKS.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
            }
        }
    }

    public Map<Vector3i, AbstractBlock> getMap() {
        return BLOCKS;
    }

    public List<Vector3i> getCoordinates() {
        return new ArrayList<>(BLOCKS.keySet());
    }

    public Vector2i getOffset() {
        return OFFSET;
    }

    public long getSeed() {
        return SEED;
    }
}