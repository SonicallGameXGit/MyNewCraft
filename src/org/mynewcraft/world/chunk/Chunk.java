package org.mynewcraft.world.chunk;

import org.auburn.fnl.FastNoiseLite;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.BlockCollider;
import org.mynewcraft.world.block.Blocks;
import org.mynewcraft.world.block.custom.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Chunk {
    private final Vector2i offset;
    private final HashMap<Vector3i, Integer> blocks = new HashMap<>();
    public final HashMap<Vector3i, Integer> abstractOutline = new HashMap<>();
    public final HashMap<Vector3i, Integer> notStaticBlocks = new HashMap<>();

    public final ArrayList<BlockCollider> interactiveBlocks = new ArrayList<>();

    private final long seed;

    private boolean changed;
    private boolean generated;

    public boolean meshGenerated;

    public static FastNoiseLite worldGenNoise;
    public static FastNoiseLite caveGenNoise;

    private static final int SEA_LEVEL = 72;

    public Chunk(Vector2i offset) {
        this.offset = offset;

        seed = worldGenNoise.mSeed;
        changed = false;
        meshGenerated = false;
        generated = false;
    }

    public boolean placeBlock(Vector3i coordinate, AbstractBlock block) {
        Integer blockFound = blocks.get(coordinate);

        if(blockFound == null) {
            if(coordinate.y() >= 0) {
                blocks.put(coordinate, block.getIndex());
                changed = true;

                return true;
            }
        } else if(AbstractBlock.getByIndex(blockFound) instanceof Block blockB) {
            if(!blockB.getStatic()) {
                if(blockB.getReplaceable())
                    notStaticBlocks.replace(coordinate, block.getIndex());
                else notStaticBlocks.put(coordinate, block.getIndex());
            } else {
                if(blockB.getReplaceable())
                    blocks.replace(coordinate, block.getIndex());
                else blocks.put(coordinate, block.getIndex());
            }

            return true;
        }

        return false;
    }
    public void removeBlock(Vector3i coordinate) {
        blocks.remove(coordinate);
        notStaticBlocks.remove(coordinate);
        changed = true;
    }

    public HashMap<Vector3i, Integer> getMap() {
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

    public BlockCollider[] getInteractiveBlocks() {
        return interactiveBlocks.toArray(new BlockCollider[0]);
    }

    public HashMap<Vector3i, Integer> getAbstractOutline() {
        return abstractOutline;
    }
    public HashMap<Vector3i, Integer> getNotStaticBlocks() {
        return notStaticBlocks;
    }

    public void placeAbstractOutlineBlock(Vector3i coordinate, AbstractBlock block) {
        if(!abstractOutline.containsKey(coordinate)) abstractOutline.put(coordinate, block.getIndex());
    }

    public void removeAbstractOutlineBlock(Vector3i coordinate) {
        abstractOutline.remove(coordinate);
    }

    public long getSeed() {
        return seed;
    }

    public void generate() {
        for(int x = -1; x < World.chunkScale + 1; x++) {
            for(int z = -1; z < World.chunkScale + 1; z++) {
                int ox = offset.x() * World.chunkScale + x;
                int oz = offset.y() * World.chunkScale + z;
                int height = (int) ((worldGenNoise.GetNoise(ox / 2.5f, oz / 2.5f) + 1.0) * 128.0);

                boolean notOutline = x != -1 && z != -1 && x != World.chunkScale && z != World.chunkScale;

                for(int i = 1; i < height; i++) {
                    if(caveGenNoise.GetNoise(ox, i, oz) <= 0.1 || caveGenNoise.GetNoise(ox / 7.0f, i / 7.0f, oz / 7.0f) >= -0.3) {
                        AbstractBlock block = Blocks.STONE;
                        if(i >= height - new Random(seed + ox * 392034L + oz * 3929340L).nextDouble(0.0, 6.0))
                            block = Blocks.DIRT;
                        if(i == height - 1)
                            block = height <= SEA_LEVEL + 5.0 + (caveGenNoise.GetNoise(ox, oz) + 1.0) * 6.0 ? (caveGenNoise.GetNoise(ox + 3920.0f, oz - 323.3f) <= 0.3 ? Blocks.SAND : Blocks.GRAVEL) : Blocks.GRASS_BLOCK;
                        if(height <= SEA_LEVEL) {
                            for(int j = height; j <= SEA_LEVEL; j++)
                                if(notOutline) blocks.put(new Vector3i(x, j, z), Blocks.WATER.getIndex());
                                else abstractOutline.put(new Vector3i(x, j, z), Blocks.WATER.getIndex());
                        }

                        if(notOutline) blocks.put(new Vector3i(x, i, z), block.getIndex());
                        else abstractOutline.put(new Vector3i(x, i, z), block.getIndex());
                    }
                }

                if(notOutline) blocks.put(new Vector3i(x, 0, z), Blocks.BEDROCK.getIndex());
                else abstractOutline.put(new Vector3i(x, 0, z), Blocks.BEDROCK.getIndex());
            }
        }

        generated = true;
    }

    public boolean getMeshGenerated() {
        return meshGenerated;
    }
    public boolean getGenerated() {
        return generated;
    }
    public boolean containsBlock(Vector3i coordinate) {
        return blocks.containsKey(coordinate) || abstractOutline.containsKey(coordinate);
    }
}