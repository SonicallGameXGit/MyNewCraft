package org.mynewcraft.world;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.chunk.ChunkMeshBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {
    public double gravity;
    public long SEED;

    public final HashMap<Vector2i, Chunk> CHUNKS = new HashMap<>();
    public final HashMap<Vector2i, Mesh> CHUNK_MESHES = new HashMap<>();

    public final double SPAWN_AREA = 32.0;
    public final int DEFAULT_GAMEMODE;

    public World(long seed, double gravity, int defaultGamemode) {
        SEED = seed;
        DEFAULT_GAMEMODE = defaultGamemode;

        this.gravity = gravity;

        for(int x = 0; x < 2; x++) {
            for(int z = 0; z < 2; z++) {
                CHUNKS.put(new Vector2i(x, z), new Chunk(new Vector2i(x, z), SEED));
                CHUNK_MESHES.put(new Vector2i(x, z), ChunkMeshBuilder.build(CHUNKS.get(new Vector2i(x, z))));
            }
        }
    }

    public void updateMesh(Vector2i offset) {
        if(CHUNKS.get(offset) != null)
            CHUNK_MESHES.replace(offset, ChunkMeshBuilder.build(CHUNKS.get(offset)));
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
        for(Mesh chunkMesh : CHUNK_MESHES.values()) chunkMesh.clear();
    }

    public List<Chunk> getNearChunks(Vector2d position) {
        int chunkX = (int) Math.floor(position.x() / 16.0);
        int chunkZ = (int) Math.floor(position.y() / 16.0);

        List<Chunk> chunks = new ArrayList<>();
        for(int i = chunkX - 1; i <= chunkX + 1; i++)
            for(int j = chunkZ - 1; j <= chunkZ + 1; j++)
                if(CHUNKS.get(new Vector2i(i, j)) != null)
                    chunks.add(CHUNKS.get(new Vector2i(i, j)));

        return chunks;
    }
    public Chunk getChunk(Vector2d position) {
        return CHUNKS.get(new Vector2i((int) Math.floor(position.x() / 16.0), (int) Math.floor(position.y() / 16.0)));
    }
}