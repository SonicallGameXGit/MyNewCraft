package org.mynewcraft.world.chunk;

import org.auburn.fnl.FastNoiseLite;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Chunk {
    private final Vector2i offset;
    private final HashMap<Vector3i, AbstractBlock> blocks = new HashMap<>();
    public final HashMap<Vector3i, Boolean> abstractOutline = new HashMap<>();

    public final ArrayList<CubeCollider> interactiveBlocks = new ArrayList<>();

    private final long seed;
    private boolean changed;

    public static FastNoiseLite worldGenNoise;
    public static FastNoiseLite caveGenNoise;
    public static FastNoiseLite riverGenNoise;

    public Chunk(Vector2i offset) {
        this.offset = offset;

        seed = worldGenNoise.mSeed;

        for(int x = -1; x < 17; x++) {
            for(int z = -1; z < 17; z++) {
                int ox = offset.x() * 16 + x;
                int oz = offset.y() * 16 + z;
                int height = (int) ((worldGenNoise.GetNoise(ox / 2.5f, oz / 2.5f) + 1.0) * 128.0);

                boolean notOutline = x != -1 && z != -1 && x != 16 && z != 16;

                for(int i = 1; i < height; i++) {
                    double inRiver = riverGenNoise.GetNoise(ox, oz);

                    if((caveGenNoise.GetNoise(ox, i, oz) <= 0.1 || caveGenNoise.GetNoise(ox / 7.0f, i / 7.0f, oz / 7.0f) >= -0.2) && inRiver >= (i - height) / 64.0) {
                        AbstractBlock block = Blocks.STONE;
                        if(i >= height - new Random(worldGenNoise.mSeed + ox * 34829L + oz * 339204L).nextInt(0, 7))
                            block = Blocks.DIRT;
                        if(i == height - 1)
                            block = Blocks.GRASS_BLOCK;
                        if(inRiver >= (height / (i - 3.0)) - new Random(worldGenNoise.mSeed + ox * 394930L + oz * 94830L).nextDouble() * 0.1 + 0.3)
                            block = Blocks.SAND;

                        if(notOutline) blocks.put(new Vector3i(x, i, z), block);
                        else abstractOutline.put(new Vector3i(x, i, z), true);
                    }
                }

                if(notOutline) blocks.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
                else abstractOutline.put(new Vector3i(x, 0, z), true);
            }
        }

        changed = false;
    }

    public boolean placeBlock(Vector3i coordinate, AbstractBlock block) {
        if(!blocks.containsKey(coordinate)) {
            if(coordinate.y() >= 0) {
                blocks.put(coordinate, block);
                changed = true;

                return true;
            }
        }

        return false;
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

    public long getSeed() {
        return seed;
    }
}