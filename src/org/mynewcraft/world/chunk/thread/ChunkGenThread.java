package org.mynewcraft.world.chunk.thread;

import org.joml.Vector2i;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkGenThread extends Thread {
    private final HashMap<Vector2i, Chunk> chunks = new HashMap<>();
    private final ArrayList<Chunk> generatedChunks = new ArrayList<>();

    private boolean running = true;

    @Override
    public void run() {
        super.run();

        while(running) {
            if(chunks.size() > 0) {
                ArrayList<Vector2i> offsets = new ArrayList<>(chunks.keySet());

                for(int i = 0; i < offsets.size(); i++) {
                    Chunk chunk = chunks.get(offsets.get(i));
                    if(chunk != null) {
                        chunk.generate();

                        generatedChunks.add(chunk);
                        this.chunks.remove(offsets.get(i));
                    }
                }
            }

            MyNewCraft.LOGGER.isDebugEnabled();
        }
    }
    @Override
    public void interrupt() {
        running = false;

        generatedChunks.clear();
        chunks.clear();

        super.interrupt();
    }

    public synchronized void addChunk(Chunk chunk) {
        if(!chunks.containsKey(chunk.getOffset()))
            chunks.put(chunk.getOffset(), chunk);
    }

    public ArrayList<Chunk> getChunks() {
        return generatedChunks;
    }

    public synchronized void clearGenerated() {
        generatedChunks.clear();
    }

    public synchronized void clearAll() {
        chunks.clear();
        generatedChunks.clear();
    }
}