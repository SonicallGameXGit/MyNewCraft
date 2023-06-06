package org.mynewcraft.world.chunk;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;

import java.util.*;

public class Chunk {
    private final Vector2i OFFSET;
    private final long SEED;
    private final Map<Vector3i, AbstractBlock> BLOCKS = new HashMap<>();

    public final List<CubeCollider> INTERACTIVE_BLOCKS = new ArrayList<>();

    private boolean changed;

    public Chunk(Vector2i offset, long seed) {
        OFFSET = offset;
        SEED = seed;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int ox = OFFSET.x() * 16 + x;
                int oz = OFFSET.y() * 16 + z;

                double mountainsHeight = Noise.gradientCoherentNoise3D(ox / 64.0, 0, oz / 64.0, (int) seed, NoiseQuality.FAST) * 64.0 + 72.0;
                mountainsHeight += Noise.gradientCoherentNoise3D(ox / 32.0, 0, oz / 32.0, (int) seed, NoiseQuality.FAST) * 16.0;
                mountainsHeight += Noise.gradientCoherentNoise3D(ox / 16.0, 0, oz / 16.0, (int) seed, NoiseQuality.FAST) * 4.0;
                double plainsHeight = Noise.gradientCoherentNoise3D(ox / 72.0, 0, oz / 72.0, (int) seed, NoiseQuality.FAST) * 2.0 + 64.0;
                double height = MathUtil.smooth(mountainsHeight, plainsHeight, Noise.gradientCoherentNoise3D(ox / 256.0, 0, oz / 256.0, (int) seed, NoiseQuality.FAST));

                for(int y = 1; y < height; y++) {
                    AbstractBlock block = Blocks.STONE;

                    if(y >= height - new Random(SEED + ox * 38506L + oz * 101950L).nextDouble() * 5.0 - 1.0)
                        block = Blocks.DIRT;
                    if(y >= height - 1) block = Blocks.GRASS_BLOCK;

                    BLOCKS.put(new Vector3i(x, y, z), block);
                }

                BLOCKS.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
            }
        }

        changed = false;
    }

    public void placeBlock(Vector3i coordinate, AbstractBlock block) {
        if(coordinate.x() >= 0 && coordinate.x() <= getOffset().x() + 16) {
            if(coordinate.y() >= 0) {
                if(coordinate.z() >= 0 && coordinate.z() <= getOffset().y() + 16) {
                    BLOCKS.put(coordinate, block);
                    changed = true;
                }
            }
        }
    }
    public void removeBlock(Vector3i coordinate) {
        BLOCKS.remove(coordinate);
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

    public boolean getChanged() {
        return changed;
    }

    public CubeCollider[] getInteractiveBlocks() {
        return INTERACTIVE_BLOCKS.toArray(new CubeCollider[0]);
    }
}