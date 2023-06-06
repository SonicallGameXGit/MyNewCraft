package org.mynewcraft.world.chunk;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Chunk {
    private final long seed;

    private final Vector2i offset;
    private final HashMap<Vector3i, AbstractBlock> blocks = new HashMap<>();

    public final HashMap<Vector3i, Boolean> abstractOutline = new HashMap<>();

    public final ArrayList<CubeCollider> interactiveBlocks = new ArrayList<>();

    private boolean changed;

    public Chunk(Vector2i offset, long seed) {
        this.offset = offset;
        this.seed = seed;

        for(int x = -1; x < 17; x++) {
            for(int z = -1; z < 17; z++) {
                boolean notOutline = x != -1 && z != -1 && x != 16 && z != 16;

                int ox = offset.x() * 16 + x;
                int oz = offset.y() * 16 + z;

                double mountainsHeight = Noise.gradientCoherentNoise3D(ox / 64.0, 0, oz / 64.0, (int) seed, NoiseQuality.FAST) * 64.0 + 128.0;
                mountainsHeight += Noise.gradientCoherentNoise3D(ox / 32.0, 0, oz / 32.0, (int) seed, NoiseQuality.FAST) * 16.0;
                mountainsHeight += Noise.gradientCoherentNoise3D(ox / 16.0, 0, oz / 16.0, (int) seed, NoiseQuality.FAST) * 4.0;
                double plainsHeight = Noise.gradientCoherentNoise3D(ox / 72.0, 0, oz / 72.0, (int) seed, NoiseQuality.FAST) * 5.0 + 128.0;
                double height = MathUtil.smooth(mountainsHeight, plainsHeight, Noise.gradientCoherentNoise3D(ox / 256.0, 0, oz / 256.0, (int) seed, NoiseQuality.FAST) + 0.4);

                for(int y = 1; y < height; y++) {
                    AbstractBlock block = Blocks.STONE;

                    if(y >= height - new Random(seed + ox * 38506L + oz * 101950L).nextDouble() * 5.0 - 1.0)
                        block = Blocks.DIRT;
                    if(y >= height - 1) block = Blocks.GRASS_BLOCK;

                    if(y >= 100 || Noise.valueCoherentNoise3D(ox / 10.0, y / 10.0, oz / 10.0, (int) seed, NoiseQuality.FAST) <= 0.5) {
                        if(notOutline)
                            blocks.put(new Vector3i(x, y, z), block);
                        else abstractOutline.put(new Vector3i(x, y, z), true);
                    }
                }

                if(notOutline) blocks.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
                else abstractOutline.put(new Vector3i(x, 0, z), true);
            }
        }

        changed = false;
    }

    public void placeBlock(Vector3i coordinate, AbstractBlock block) {
        if(!blocks.containsKey(coordinate)) {
            if(coordinate.x() >= 0 && coordinate.x() <= getOffset().x() + 16) {
                if(coordinate.y() >= 0) {
                    if(coordinate.z() >= 0 && coordinate.z() <= getOffset().y() + 16) {
                        blocks.put(coordinate, block);
                        changed = true;
                    }
                }
            }
        }
    }
    public void removeBlock(Vector3i coordinate) {
        blocks.remove(coordinate);
        changed = true;
    }

    public HashMap<Vector3i, AbstractBlock> getMap() {
        return blocks;
    }

    public ArrayList<Vector3i> getCoordinates() {
        return new ArrayList<>(blocks.keySet());
    }

    public Vector2i getOffset() {
        return offset;
    }

    public long getSeed() {
        return seed;
    }

    public boolean getChanged() {
        return changed;
    }

    public CubeCollider[] getInteractiveBlocks() {
        return interactiveBlocks.toArray(new CubeCollider[0]);
    }

    public HashMap<Vector3i, Boolean> getAbstractOutline() {
        return abstractOutline;
    }

    public void placeAbstractOutlineBlock(Vector3i coordinate) {
        if(!abstractOutline.containsKey(coordinate)) abstractOutline.put(coordinate, true);
    }
    public void removeAbstractOutlineBlock(Vector3i coordinate) {
        abstractOutline.remove(coordinate);
    }
}