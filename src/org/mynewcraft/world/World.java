package org.mynewcraft.world;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.MyNewCraft;
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

    public final double SPAWN_AREA = 32.0;
    public final long SEED;
    public final int DEFAULT_GAMEMODE;

    public World(long seed, double gravity, int defaultGamemode) {
        SEED = seed;
        DEFAULT_GAMEMODE = defaultGamemode;

        this.gravity = gravity;

        CHUNKS.put(new Vector2i(), new Chunk(new Vector2i(), SEED));
    }

    public void update() {
        if(CHUNKS_TO_LOAD.size() > 0) {
            Chunk chunk = CHUNKS_TO_LOAD.values().stream().toList().get(0);
            CHUNKS.put(chunk.getOffset(), chunk);
            CHUNKS_TO_LOAD.remove(chunk.getOffset());
        }
        if(CHUNKS_TO_REMOVE.size() > 0) {
            Vector2i offset = CHUNKS_TO_REMOVE.keySet().stream().toList().get(0);
            CHUNKS.remove(offset);
            CHUNKS_TO_REMOVE.remove(offset);
        }

        MyNewCraft.LOGGER.debug(CHUNKS.size());
    }
    public void placeBlock(Vector3i coordinate, AbstractBlock block) {
        Vector2i chunkPos = new Vector2i((int) Math.floor((double) coordinate.x() / 16.0), (int) Math.floor((double) coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        Vector3i intBlockPos = new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * 16, 0, chunkPos.y() * 16));

        if(chunk != null && !chunk.getMap().containsKey(intBlockPos)) chunk.placeBlock(intBlockPos, block);
    }
    public void removeBlock(Vector3i coordinate) {
        Vector2i chunkPos = new Vector2i((int) Math.floor((double) coordinate.x() / 16.0), (int) Math.floor((double) coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        if(chunk != null) chunk.removeBlock(new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * 16, 0, chunkPos.y() * 16)));
    }
    public void clear() {
        CHUNKS_TO_LOAD.clear();
        CHUNKS_TO_REMOVE.clear();
        CHUNKS.clear();
    }
    public void loadChunk(Vector2i offset) {
        CHUNKS_TO_LOAD.put(offset, new Chunk(offset, SEED));
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