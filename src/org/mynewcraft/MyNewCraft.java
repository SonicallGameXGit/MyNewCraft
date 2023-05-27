package org.mynewcraft;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.sun.jna.Native;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.mynewcraft.client.graphics.shader.WorldShader;
import org.mynewcraft.client.graphics.util.BlockSelection;
import org.mynewcraft.engine.graphics.OpenGL;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.io.Keyboard;
import org.mynewcraft.engine.io.Mouse;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.texture.Texture;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.Blocks;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.chunk.ChunkMeshBuilder;
import org.mynewcraft.world.entity.custom.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MyNewCraft {
    public static final String GAME_ID = "mynewcraft";
    public static final String RESOURCE_PACK = "MyNewCraft";

    public static final Logger LOGGER = LogManager.getLogger(GAME_ID);

    public static final int VIEW_DISTANCE = 16;

    public static DiscordRPC discordRPC;
    public static DiscordRichPresence discordRichPresence;

    private static final HashMap<Vector2i, Mesh> CHUNK_MESHES = new HashMap<>();

    public static void main(String[] args) throws Exception {
        discordRPC = (DiscordRPC) Native.loadLibrary("libs/DISCORD/discord-rpc", DiscordRPC.class);
        discordRPC.Discord_Initialize("1112019011690037378", new DiscordEventHandlers(), true, null);

        discordRichPresence = new DiscordRichPresence();
        discordRichPresence.largeImageKey = "main-logo";
        discordRichPresence.startTimestamp = Time.getMilliseconds();
        discordRichPresence.details = "Exploring the world in survival mode";
        discordRichPresence.state = null;

        discordRPC.Discord_UpdatePresence(discordRichPresence);

        Window window = new Window(1920.0 * 1.5, 1080.0 * 1.5, "MyNewCraft", true, false, false);
        window.setIcon("resourcepacks/" + RESOURCE_PACK + "/" + GAME_ID + "/icon.png");

        OpenGL.setClearColor(new Vector3d(0.25, 0.55, 1.0));
        OpenGL.cullFace(true);
        OpenGL.outlineWidth(5.0);

        Keyboard keyboard = new Keyboard(window);
        Mouse mouse = new Mouse(window);
        mouse.grab(true);

        Time time = new Time();
        WorldShader worldShader = new WorldShader(window, GAME_ID);

        Texture texture = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
        Blocks.register();

        World world = new World(new Random().nextLong(), 32.0, PlayerEntity.SURVIVAL_GAMEMODE);

        LOGGER.debug("Seed: " + world.SEED);

        PlayerEntity playerEntity = new PlayerEntity(world, new CubeCollider(new Vector3d(0.0, 128.0, 0.0), new Vector3d(0.6, 1.8, 0.6)), new Vector3d(), 1.0, 3.0, 4.0, 9.0);

        BlockSelection selection = new BlockSelection();

        int fps = 0;
        double fpsTimer = 0.0;

        mouse.update();
        while(window.getRunning()) {
            window.update();
            mouse.update();
            time.update();

            fps++;
            if(fpsTimer > 1.0) {
                window.setTitle("MyNewCraft | FPS: " + fps);
                fpsTimer = 0.0;
                fps = 0;
            }

            fpsTimer += time.getDelta();

            if(keyboard.getPress(Keyboard.KEY_F3)) {
                if(keyboard.getClick(Keyboard.KEY_T))
                    texture = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
                if(keyboard.getClick(Keyboard.KEY_R)) {
                    worldShader = new WorldShader(window, GAME_ID);
                    playerEntity.direction.y = 0.0;
                }
                if(keyboard.getClick(Keyboard.KEY_F4)) {
                    playerEntity.setGameMode(playerEntity.getGameMode() + 1);

                    discordRichPresence.details = "Exploring the world in " + playerEntity.getGameModeName(playerEntity.getGameMode()) + " mode";
                    discordRPC.Discord_UpdatePresence(discordRichPresence);
                }
            }
            if(keyboard.getClick(Keyboard.KEY_R)) playerEntity.collider.position.set(new Random().nextDouble(0.0, world.SPAWN_AREA), 128.0, new Random().nextDouble(0.0, world.SPAWN_AREA));
            if(keyboard.getClick(Keyboard.KEY_F11)) window.setFullscreen(!window.getFullscreen());
            if(keyboard.getClick(Keyboard.KEY_ESCAPE)) mouse.grab(!mouse.getGrabbed());

            world.update();
            playerEntity.update(world, selection, keyboard, mouse, time);

            int chunkX = (int) (playerEntity.collider.position.x() / 16.0);
            int chunkZ = (int) (playerEntity.collider.position.z() / 16.0);

            for(int i = chunkX - VIEW_DISTANCE / 2; i < chunkX + VIEW_DISTANCE / 2; i++)
                for(int j = chunkZ - VIEW_DISTANCE / 2; j < chunkZ + VIEW_DISTANCE / 2; j++)
                    if(new Vector2d(i, j).distance(chunkX, chunkZ) <= VIEW_DISTANCE / 2.0 && world.CHUNKS.get(new Vector2i(i, j)) == null && world.CHUNKS_TO_LOAD.get(new Vector2i(i, j)) == null)
                        world.CHUNKS_TO_LOAD.put(new Vector2i(i, j), new Chunk(new Vector2i(i, j), world.SEED));

            for(Vector2i key : world.CHUNKS.keySet()) {
                if(new Vector2d(key).distance(chunkX, chunkZ) > VIEW_DISTANCE / 2.0 && world.CHUNKS_TO_REMOVE.get(key) == null)
                    world.CHUNKS_TO_REMOVE.put(key, world.CHUNKS.get(key));
                if(new Vector2d(key).distance(chunkX, chunkZ) <= VIEW_DISTANCE / 2.0 && world.CHUNKS.get(key) != null && CHUNK_MESHES.get(key) == null)
                    CHUNK_MESHES.put(key, ChunkMeshBuilder.build(world.CHUNKS.get(key)));
            }

            worldShader.load();
            worldShader.project(100.0, 0.05, 1000.0);
            worldShader.view(new Vector3d(playerEntity.collider.position).add(new Vector3d(playerEntity.collider.scale.x() / 2.0, playerEntity.collider.scale.y() - 0.2, playerEntity.collider.scale.z() / 2.0)), playerEntity.rotation);

            for(Vector2i key : new ArrayList<>(CHUNK_MESHES.keySet())) {
                if(new Vector2d(key).distance(chunkX, chunkZ) <= VIEW_DISTANCE / 2.0) {
                    Mesh chunkMesh = CHUNK_MESHES.get(key);

                    chunkMesh.load();

                    worldShader.transform(new Vector3d(key.x() * 16.0, 0.0, key.y() * 16.0), new Vector3d(), new Vector3d(1.0));

                    chunkMesh.render(texture);
                    chunkMesh.unload();
                } else CHUNK_MESHES.remove(key);
            }

            selection.load();

            worldShader.transform(selection.position, new Vector3d(), new Vector3d(1.0));

            selection.render();
            selection.unload();

            worldShader.unload();
        }

        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();

        LOGGER.traceExit();

        world.clear();
        worldShader.clear();
        window.close();

        System.exit(0);
    }
    public static void updateMesh(World world, Vector2i key) {
        if(world.CHUNKS.get(key) != null)
            CHUNK_MESHES.replace(key, ChunkMeshBuilder.build(world.CHUNKS.get(key)));
    }
}