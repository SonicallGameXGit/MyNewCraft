package org.mynewcraft.world;

import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.chunk.ChunkMeshBuilder;

import java.util.HashMap;

public class World {
    public double gravity;
    public long SEED;

    public final HashMap<Vector2i, Chunk> CHUNKS = new HashMap<>();
    public final HashMap<Vector2i, Mesh> CHUNK_MESHES = new HashMap<>();

    public World(long seed, double gravity) {
        SEED = seed;
        this.gravity = gravity;

        for(int x = 0; x < 2; x++) {
            for(int z = 0; z < 2; z++) {
                CHUNKS.put(new Vector2i(x, z), new Chunk(new Vector2i(x, z), SEED));
                CHUNK_MESHES.put(new Vector2i(x, z), ChunkMeshBuilder.build(CHUNKS.get(new Vector2i(x, z))));
            }
        }
    }

    public void update() {
        // Update
    }
    public void placeBlock(Vector3i coordinate, AbstractBlock block) {
        Vector2i chunkPos = new Vector2i((int) Math.floor((double) coordinate.x() / 16.0), (int) Math.floor((double) coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        if(chunk != null) chunk.placeBlock(coordinate, block);
    }
    public void removeBlock(Vector3i coordinate) {
        Vector2i chunkPos = new Vector2i((int) Math.floor((double) coordinate.x() / 16.0), (int) Math.floor((double) coordinate.z() / 16.0));
        Chunk chunk = CHUNKS.get(chunkPos);

        if(chunk != null) chunk.removeBlock(coordinate);
    }
    public void clear() {
        for(Mesh chunkMesh : CHUNK_MESHES.values()) chunkMesh.clear();
    }
}