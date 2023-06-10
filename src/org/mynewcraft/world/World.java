package org.mynewcraft.world;

import org.auburn.fnl.FastNoiseLite;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {
    public double gravity;

    public final HashMap<Vector2i, Chunk> CHUNKS_TO_LOAD = new HashMap<>();
    public final HashMap<Vector2i, Chunk> CHUNKS_TO_REMOVE = new HashMap<>();
    public final HashMap<Vector2i, Chunk> CHUNKS = new HashMap<>();

    public final double SPAWN_AREA = 16.0;
    public final long SEED;
    public final int DEFAULT_GAMEMODE;

    public World(long seed, double gravity, int defaultGamemode) {
        SEED = seed;
        DEFAULT_GAMEMODE = defaultGamemode;

        this.gravity = gravity;

        Chunk.worldGenNoise = new FastNoiseLite((int) seed);
        Chunk.worldGenNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        Chunk.worldGenNoise.SetFrequency(0.008f);
        Chunk.worldGenNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        Chunk.worldGenNoise.SetFractalOctaves(7);
        Chunk.worldGenNoise.SetFractalLacunarity(2.0f);
        Chunk.worldGenNoise.SetFractalGain(0.49f);
        Chunk.worldGenNoise.SetFractalWeightedStrength(0.516f);

        Chunk.caveGenNoise = new FastNoiseLite((int) seed);
        Chunk.caveGenNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        Chunk.caveGenNoise.SetFrequency(0.067f);
        Chunk.caveGenNoise.SetFractalType(FastNoiseLite.FractalType.Ridged);
        Chunk.caveGenNoise.SetFractalOctaves(1);
        Chunk.caveGenNoise.SetFractalLacunarity(0.0f);
        Chunk.caveGenNoise.SetFractalGain(0.0f);
        Chunk.caveGenNoise.SetFractalWeightedStrength(0.0f);
        Chunk.caveGenNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        Chunk.caveGenNoise.SetDomainWarpAmp(0.0f);

        Chunk.riverGenNoise = new FastNoiseLite((int) seed);
        Chunk.riverGenNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        Chunk.riverGenNoise.SetFrequency(0.05f);
        Chunk.riverGenNoise.SetFractalType(FastNoiseLite.FractalType.Ridged);
        Chunk.riverGenNoise.SetFractalOctaves(1);
        Chunk.riverGenNoise.SetFractalLacunarity(0.0f);
        Chunk.riverGenNoise.SetFractalGain(0.0f);
        Chunk.riverGenNoise.SetFractalWeightedStrength(0.0f);

        CHUNKS.put(new Vector2i(), new Chunk(new Vector2i()));
    }

    public void update() {
        if(CHUNKS_TO_LOAD.size() > 0) {
            Chunk chunk = CHUNKS_TO_LOAD.values().stream().toList().get(0);
            if(!CHUNKS.containsKey(chunk.getOffset())) CHUNKS.put(chunk.getOffset(), chunk);

            CHUNKS_TO_LOAD.remove(chunk.getOffset());
        }
        if(CHUNKS_TO_REMOVE.size() > 0) {
            Vector2i offset = CHUNKS_TO_REMOVE.keySet().stream().toList().get(0);
            if(!CHUNKS.get(offset).getChanged()) CHUNKS.remove(offset);
            CHUNKS_TO_REMOVE.remove(offset);
        }
    }
    public ArrayList<Vector2i> placeBlock(Vector3i coordinate, AbstractBlock block) {
        Vector2i chunkPos = new Vector2i((int) Math.floor(coordinate.x() / 16.0), (int) Math.floor(coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        Vector3i intBlockPos = new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * 16, 0, chunkPos.y() * 16));

        ArrayList<Vector2i> chunksToUpdate = new ArrayList<>();
        if(chunk.placeBlock(intBlockPos, block)) {
            if(intBlockPos.x() >= 15) {
                CHUNKS.get(new Vector2i(chunkPos).add(1, 0)).placeAbstractOutlineBlock(new Vector3i(-1, intBlockPos.y(), intBlockPos.z()));
                chunksToUpdate.add(new Vector2i(chunkPos).add(1, 0));
            }
            if(intBlockPos.x() <= 0) {
                CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)).placeAbstractOutlineBlock(new Vector3i(16, intBlockPos.y(), intBlockPos.z()));
                chunksToUpdate.add(new Vector2i(chunkPos).sub(1, 0));
            }
            if(intBlockPos.z() >= 15) {
                CHUNKS.get(new Vector2i(chunkPos).add(0, 1)).placeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), -1));
                chunksToUpdate.add(new Vector2i(chunkPos).add(0, 1));
            }
            if(intBlockPos.z() <= 0) {
                CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)).placeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), 16));
                chunksToUpdate.add(new Vector2i(chunkPos).sub(0, 1));
            }
        }

        return chunksToUpdate;
    }
    public ArrayList<Vector2i> removeBlock(Vector3i coordinate) {
        Vector2i chunkPos = new Vector2i((int) Math.floor(coordinate.x() / 16.0), (int) Math.floor(coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        Vector3i intBlockPos = new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * 16, 0, chunkPos.y() * 16));

        ArrayList<Vector2i> chunksToUpdate = new ArrayList<>();
        chunk.removeBlock(intBlockPos);

        if(intBlockPos.x() >= 15) {
            CHUNKS.get(new Vector2i(chunkPos).add(1, 0)).removeAbstractOutlineBlock(new Vector3i(-1, intBlockPos.y(), intBlockPos.z()));
            chunksToUpdate.add(new Vector2i(chunkPos).add(1, 0));
        }
        if(intBlockPos.x() <= 0) {
            CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)).removeAbstractOutlineBlock(new Vector3i(16, intBlockPos.y(), intBlockPos.z()));
            chunksToUpdate.add(new Vector2i(chunkPos).sub(1, 0));
        }
        if(intBlockPos.z() >= 15) {
            CHUNKS.get(new Vector2i(chunkPos).add(0, 1)).removeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), -1));
            chunksToUpdate.add(new Vector2i(chunkPos).add(0, 1));
        }
        if(intBlockPos.z() <= 0) {
            CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)).removeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), 16));
            chunksToUpdate.add(new Vector2i(chunkPos).sub(0, 1));
        }

        return chunksToUpdate;
    }

    public void clear() {
        CHUNKS_TO_LOAD.clear();
        CHUNKS_TO_REMOVE.clear();
        CHUNKS.clear();
    }
    public void loadChunk(Vector2i offset) {
        CHUNKS_TO_LOAD.put(offset, new Chunk(offset));
    }
    public void removeChunk(Vector2i offset) {
        CHUNKS_TO_REMOVE.put(offset, CHUNKS.get(offset));
    }

    public Chunk[] getNearChunks(Vector2d position) {
        int chunkX = (int) Math.floor(position.x() / 16.0);
        int chunkZ = (int) Math.floor(position.y() / 16.0);

        List<Chunk> chunks = new ArrayList<>();
        for(int i = chunkX - 1; i <= chunkX + 1; i++)
            for(int j = chunkZ - 1; j <= chunkZ + 1; j++)
                if(CHUNKS.get(new Vector2i(i, j)) != null)
                    chunks.add(CHUNKS.get(new Vector2i(i, j)));

        return chunks.toArray(new Chunk[0]);
    }

    public Chunk getChunk(Vector2i offset) {
        return CHUNKS.get(offset);
    }
}