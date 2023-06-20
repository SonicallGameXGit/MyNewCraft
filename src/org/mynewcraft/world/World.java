package org.mynewcraft.world;

import org.auburn.fnl.FastNoiseLite;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.chunk.thread.ChunkGenThread;
import org.mynewcraft.world.entity.AbstractEntity;
import org.mynewcraft.world.entity.custom.FallingBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {
    public double gravity;
    public double waterDeceleration;

    public final HashMap<Vector2i, Chunk> CHUNKS_TO_LOAD = new HashMap<>();
    public final HashMap<Vector2i, Chunk> CHUNKS_TO_REMOVE = new HashMap<>();
    public final HashMap<Vector2i, Chunk> CHUNKS = new HashMap<>();

    public final ArrayList<Vector2i> chunkMeshesToUpdate = new ArrayList<>();
    public final ArrayList<AbstractEntity> ENTITIES = new ArrayList<>();

    public static final int chunkScale = 16;

    public final long SEED;
    public final int DEFAULT_GAMEMODE;

    public final ChunkGenThread chunkGenThread = new ChunkGenThread();

    public World(long seed, double gravity, double waterDeceleration, int defaultGamemode) {
        SEED = seed;
        DEFAULT_GAMEMODE = defaultGamemode;

        this.gravity = gravity;
        this.waterDeceleration = waterDeceleration;

        Chunk.worldGenNoise = new FastNoiseLite((int) seed);
        Chunk.worldGenNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        Chunk.worldGenNoise.SetFrequency(0.008f);
        Chunk.worldGenNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        Chunk.worldGenNoise.SetFractalOctaves(7);
        Chunk.worldGenNoise.SetFractalLacunarity(2.0f);
        Chunk.worldGenNoise.SetFractalGain(0.49f);
        Chunk.worldGenNoise.SetFractalWeightedStrength(0.516f);

        Chunk.caveGenNoise = new FastNoiseLite((int) seed);
        Chunk.caveGenNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        Chunk.caveGenNoise.SetFrequency(0.106f);
        Chunk.caveGenNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        Chunk.caveGenNoise.SetFractalOctaves(2);
        Chunk.caveGenNoise.SetFractalLacunarity(0.17f);
        Chunk.caveGenNoise.SetFractalGain(0.299f);
        Chunk.caveGenNoise.SetFractalWeightedStrength(0.0f);
        Chunk.caveGenNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        Chunk.caveGenNoise.SetDomainWarpAmp(0.0f);

        chunkGenThread.start();
    }

    public void update(Time time) {
        if(CHUNKS_TO_LOAD.size() > 0) {
            Chunk chunk = CHUNKS_TO_LOAD.values().stream().toList().get(0);

            chunkGenThread.addChunk(chunk);
            CHUNKS_TO_LOAD.remove(chunk.getOffset());
        }
        if(CHUNKS_TO_REMOVE.size() > 0) {
            Vector2i offset = CHUNKS_TO_REMOVE.keySet().stream().toList().get(0);
            if(!CHUNKS.get(offset).getChanged()) CHUNKS.remove(offset);
            CHUNKS_TO_REMOVE.remove(offset);
        }

        for(Chunk chunk : new ArrayList<>(chunkGenThread.getChunks())) {
            if(chunk != null) {
                if(!CHUNKS.containsKey(chunk.getOffset()))
                    CHUNKS.put(chunk.getOffset(), chunk);
                else CHUNKS.replace(chunk.getOffset(), chunk);

                MyNewCraft.updateMesh(this, chunk.getOffset());
            }
        }
        for(Vector2i offset : chunkMeshesToUpdate)
            MyNewCraft.updateMesh(this, offset);

        chunkMeshesToUpdate.clear();

        for(AbstractEntity entity : new ArrayList<>(ENTITIES))
            if(entity instanceof FallingBlockEntity fallingBlockEntity)
                fallingBlockEntity.update(this, time);

        chunkGenThread.clearGenerated();
    }
    public void removeBlock(Vector3i coordinate) {
        Vector2i chunkPos = new Vector2i((int) Math.floor(coordinate.x() / (double) chunkScale), (int) Math.floor(coordinate.z() / (double) chunkScale));
        Chunk chunk = CHUNKS.get(chunkPos);

        Vector3i intBlockPos = new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * chunkScale, 0, chunkPos.y() * chunkScale));

        ArrayList<Vector2i> chunksToUpdate = new ArrayList<>();
        if(chunk != null) {
            chunk.removeBlock(intBlockPos);
            chunksToUpdate.add(chunkPos);

            if(intBlockPos.x() >= chunkScale - 1 && CHUNKS.get(new Vector2i(chunkPos).add(1, 0)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).add(1, 0)).removeAbstractOutlineBlock(new Vector3i(-1, intBlockPos.y(), intBlockPos.z()));
                chunksToUpdate.add(new Vector2i(chunkPos).add(1, 0));
            }
            if(intBlockPos.x() <= 0 && CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)).removeAbstractOutlineBlock(new Vector3i(chunkScale, intBlockPos.y(), intBlockPos.z()));
                chunksToUpdate.add(new Vector2i(chunkPos).sub(1, 0));
            }
            if(intBlockPos.z() >= chunkScale - 1 && CHUNKS.get(new Vector2i(chunkPos).add(0, 1)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).add(0, 1)).removeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), -1));
                chunksToUpdate.add(new Vector2i(chunkPos).add(0, 1));
            }
            if(intBlockPos.z() <= 0 && CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)).removeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), chunkScale));
                chunksToUpdate.add(new Vector2i(chunkPos).sub(0, 1));
            }
        }

        for(Vector2i offset : chunksToUpdate)
            if(!chunkMeshesToUpdate.contains(offset))
                chunkMeshesToUpdate.add(offset);

        chunksToUpdate.clear();
    }
    public void placeBlock(Vector3i coordinate, AbstractBlock block) {
        Vector2i chunkPos = new Vector2i((int) Math.floor(coordinate.x() / (double) chunkScale), (int) Math.floor(coordinate.z() / (double) chunkScale));
        Chunk chunk = CHUNKS.get(chunkPos);

        Vector3i intBlockPos = new Vector3i(coordinate).sub(new Vector3i(chunkPos.x() * chunkScale, 0, chunkPos.y() * chunkScale));

        ArrayList<Vector2i> chunksToUpdate = new ArrayList<>();
        if(chunk != null && chunk.placeBlock(intBlockPos, block)) {
            chunksToUpdate.add(chunkPos);

            if(intBlockPos.x() >= chunkScale - 1 && CHUNKS.get(new Vector2i(chunkPos).add(1, 0)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).add(1, 0)).placeAbstractOutlineBlock(new Vector3i(-1, intBlockPos.y(), intBlockPos.z()), block);
                chunksToUpdate.add(new Vector2i(chunkPos).add(1, 0));
            }
            if(intBlockPos.x() <= 0 && CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).sub(1, 0)).placeAbstractOutlineBlock(new Vector3i(chunkScale, intBlockPos.y(), intBlockPos.z()), block);
                chunksToUpdate.add(new Vector2i(chunkPos).sub(1, 0));
            }
            if(intBlockPos.z() >= chunkScale - 1 && CHUNKS.get(new Vector2i(chunkPos).add(0, 1)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).add(0, 1)).placeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), -1), block);
                chunksToUpdate.add(new Vector2i(chunkPos).add(0, 1));
            }
            if(intBlockPos.z() <= 0 && CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)) != null) {
                CHUNKS.get(new Vector2i(chunkPos).sub(0, 1)).placeAbstractOutlineBlock(new Vector3i(intBlockPos.x(), intBlockPos.y(), chunkScale), block);
                chunksToUpdate.add(new Vector2i(chunkPos).sub(0, 1));
            }
        }

        for(Vector2i offset : chunksToUpdate)
            if(!chunkMeshesToUpdate.contains(offset))
                chunkMeshesToUpdate.add(offset);

        chunksToUpdate.clear();
    }
    public void clear() {
        CHUNKS_TO_LOAD.clear();
        CHUNKS_TO_REMOVE.clear();
        CHUNKS.clear();

        chunkGenThread.clearAll();

        ENTITIES.clear();
    }
    public void close() {
        chunkGenThread.interrupt();
    }
    public void loadChunk(Vector2i offset) {
        if(!CHUNKS_TO_LOAD.containsKey(offset) && !CHUNKS.containsKey(offset))
            CHUNKS_TO_LOAD.put(offset, new Chunk(offset));
    }
    public void removeChunk(Vector2i offset) {
        if(!CHUNKS_TO_REMOVE.containsKey(offset))
            CHUNKS_TO_REMOVE.put(offset, CHUNKS.get(offset));
    }

    public Chunk[] getNearChunks(Vector2d position) {
        int chunkX = (int) Math.floor(position.x() / chunkScale);
        int chunkZ = (int) Math.floor(position.y() / chunkScale);

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

    public void killEntity(AbstractEntity entity) {
        for(int i = 0; i < ENTITIES.size(); i++) {
            if(ENTITIES.get(i) == entity) {
                ENTITIES.remove(i);
                break;
            }
        }
    }
    public void spawnEntity(AbstractEntity entity) {
        ENTITIES.add(entity);
    }

    public AbstractBlock getBlockAt(Vector3i coordinate) {
        Vector2i chunkOffset = new Vector2i((int) Math.floor(coordinate.x() / (double) chunkScale), (int) Math.floor(coordinate.z() / (double) chunkScale));
        Chunk chunk = getChunk(new Vector2i(chunkOffset));
        if(chunk == null) return null;

        Integer blockId = chunk.getMap().get(new Vector3i(coordinate).sub(chunkOffset.x() * chunkScale, 0, chunkOffset.y() * chunkScale));
        return blockId == null ? null : AbstractBlock.getByIndex(blockId);
    }
}