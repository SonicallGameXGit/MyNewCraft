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
import org.mynewcraft.client.graphics.gui.CrosshairGui;
import org.mynewcraft.client.graphics.gui.PauseMenuGui;
import org.mynewcraft.client.graphics.gui.WorldGenGui;
import org.mynewcraft.client.graphics.shader.WorldShader;
import org.mynewcraft.client.graphics.util.BlockSelection;
import org.mynewcraft.engine.graphics.OpenGl;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.io.Font;
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
import org.mynewcraft.world.entity.AbstractEntity;
import org.mynewcraft.world.entity.custom.FallingBlockEntity;
import org.mynewcraft.world.entity.custom.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MyNewCraft {
    public static final String GAME_ID = "mynewcraft";
    public static final String RESOURCE_PACK = "MyNewCraft";

    public static final Logger LOGGER = LogManager.getLogger(GAME_ID);

    public static DiscordRPC discordRPC;
    public static DiscordRichPresence discordRichPresence;

    public static final HashMap<Vector2i, Mesh> CHUNK_MESHES = new HashMap<>();
    public static final HashMap<Vector2i, Mesh> CHUNK_TRANSPARENT_MESHES = new HashMap<>();

    public static final int viewDistance = 12;

    public static boolean paused = false;

    private static CrosshairGui crosshairGui;

    private static Window window;
    private static World world;
    private static WorldShader worldShader;

    private static final Vector3d skyColor = new Vector3d(0.35, 0.5, 0.9);

    public static void main(String[] args) throws Exception {
        discordRPC = (DiscordRPC) Native.loadLibrary("libs/DISCORD/discord-rpc", DiscordRPC.class);
        discordRPC.Discord_Initialize("1112019011690037378", new DiscordEventHandlers(), true, null);

        discordRichPresence = new DiscordRichPresence();
        discordRichPresence.largeImageKey = "main-logo";
        discordRichPresence.startTimestamp = Time.getMilliseconds();
        discordRichPresence.details = "Exploring the world in survival mode";
        discordRichPresence.state = null;

        discordRPC.Discord_UpdatePresence(discordRichPresence);

        window = new Window(1920.0 * 1.5, 1080.0 * 1.5, "MyNewCraft", true, false, false);
        window.setIcon("resourcepacks/" + RESOURCE_PACK + "/" + GAME_ID + "/icon.png");
        window.initImGui(new Font("resourcepacks/" + RESOURCE_PACK + "/" + GAME_ID + "/fonts/default.ttf", 60.0f), false);

        OpenGl.setClearColor(skyColor);
        OpenGl.cullFace(true);
        OpenGl.outlineWidth(5.0);
        OpenGl.enableDepthTest(true);

        Keyboard keyboard = new Keyboard(window);
        Mouse mouse = new Mouse(window);
        mouse.grab(true);

        Time time = new Time();
        worldShader = new WorldShader(window);

        Texture texture = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
        Blocks.register();

        world = new World(new Random().nextLong(), 32.0, 12.0, PlayerEntity.SURVIVAL_GAMEMODE);
        WorldGenGui worldGenGui = new WorldGenGui();

        crosshairGui = new CrosshairGui();
        PauseMenuGui pauseMenuGui = new PauseMenuGui();

        LOGGER.debug("Seed: " + world.SEED);

        PlayerEntity playerEntity = new PlayerEntity(world, new CubeCollider(new Vector3d(0.0, 320.0, 0.0), new Vector3d(0.6, 1.8, 0.6)), new Vector3d(), 1.0, 4.317, 24.0, 4.0, 0.15, 9.0);

        BlockSelection selection = new BlockSelection();

        int fps = 0;
        double fpsTimer = 0.0;

        boolean wireframeMode = false;

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
                if(keyboard.getClick(Keyboard.KEY_T)) {
                    texture.clear();
                    texture = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
                }
                if(keyboard.getClick(Keyboard.KEY_R))
                    worldShader = new WorldShader(window);
                if(keyboard.getClick(Keyboard.KEY_F4)) {
                    playerEntity.setGameMode(playerEntity.getGameMode() + 1);

                    discordRichPresence.details = "Exploring the world in " + playerEntity.getGameModeName(playerEntity.getGameMode()) + " mode";
                    discordRPC.Discord_UpdatePresence(discordRichPresence);
                }
            }
            if(keyboard.getClick(Keyboard.KEY_R)) {
                playerEntity.collider.position.set(new Random().nextDouble(0.0, World.chunkScale), 320.0, new Random().nextDouble(0.0, World.chunkScale));
                playerEntity.direction.y = 0.0;
            }
            if(keyboard.getClick(Keyboard.KEY_F9)) {
                worldGenGui.enable(worldGenGui.getDisabled());
                mouse.grab(!paused && worldGenGui.getDisabled());
            }
            if(keyboard.getClick(Keyboard.KEY_F7)) wireframeMode = !wireframeMode;
            if(keyboard.getClick(Keyboard.KEY_F11)) window.setFullscreen(!window.getFullscreen());
            if(keyboard.getClick(Keyboard.KEY_ESCAPE)) {
                if(worldGenGui.getDisabled()) {
                    paused = !paused;

                    mouse.grab(!paused);
                    mouse.setPosition(new Vector2d(window.getScale().x() / 2.0, window.getScale().y() / 2.0 - 64.0));
                } else worldGenGui.enable(false);
            }
            if(!paused) {
                world.update(time);
                playerEntity.update(world, selection, keyboard, mouse, time);
            } else time.pause();

            int chunkX = (int) (playerEntity.collider.position.x() / World.chunkScale);
            int chunkZ = (int) (playerEntity.collider.position.z() / World.chunkScale);

            for(int i = chunkX - (int) (getTransformedViewDistance() / 2.0); i < chunkX + (int) (getTransformedViewDistance() / 2.0); i++)
                for(int j = chunkZ - (int) (getTransformedViewDistance() / 2.0); j < chunkZ + (int) (getTransformedViewDistance() / 2.0); j++)
                    if(new Vector2d(i, j).distance(chunkX, chunkZ) <= getTransformedViewDistance() / 2.0 && world.getChunk(new Vector2i(i, j)) == null && world.CHUNKS_TO_LOAD.get(new Vector2i(i, j)) == null)
                        world.loadChunk(new Vector2i(i, j));
            for(Vector2i key : world.CHUNKS.keySet()) {
                if(key.distance(chunkX, chunkZ) > getTransformedViewDistance() / 2.0 && world.CHUNKS_TO_REMOVE.get(key) == null)
                    world.removeChunk(key);
                if(key.distance(chunkX, chunkZ) <= getTransformedViewDistance() / 2.0 && world.getChunk(key) != null && CHUNK_MESHES.get(key) == null) {
                    Mesh[] meshes = ChunkMeshBuilder.build(world.getChunk(key));

                    CHUNK_MESHES.put(key, meshes[0]);
                    CHUNK_TRANSPARENT_MESHES.put(key, meshes[1]);
                }
            }

            worldShader.load();
            worldShader.project(90.0, 0.05, 1000.0);
            worldShader.view(new Vector3d(playerEntity.collider.position).add(new Vector3d(playerEntity.collider.scale.x() / 2.0, playerEntity.collider.scale.y() - 0.2, playerEntity.collider.scale.z() / 2.0)), playerEntity.rotation);
            worldShader.fog(skyColor);
            worldShader.time(time);

            OpenGl.outlineOnly(wireframeMode);

            for(Vector2i key : new ArrayList<>(CHUNK_MESHES.keySet())) {
                if(new Vector2d(key).distance(chunkX, chunkZ) <= getTransformedViewDistance() / 2.0) {
                    Mesh chunkMesh = CHUNK_MESHES.get(key);
                    chunkMesh.load();

                    worldShader.transform(new Vector3d(key.x() * World.chunkScale, 0.0, key.y() * World.chunkScale), new Vector3d(), new Vector3d(1.0));

                    chunkMesh.render(texture);
                    chunkMesh.unload();
                } else {
                    CHUNK_MESHES.remove(key);
                    CHUNK_TRANSPARENT_MESHES.remove(key);
                }
            }
            for(AbstractEntity entity : new ArrayList<>(world.ENTITIES))
                if(entity instanceof FallingBlockEntity fallingBlockEntity)
                    fallingBlockEntity.render(worldShader, texture);

            OpenGl.cullFace(false);
            for(Vector2i key : new ArrayList<>(CHUNK_MESHES.keySet())) {
                if(new Vector2d(key).distance(chunkX, chunkZ) <= getTransformedViewDistance() / 2.0) {
                    Mesh chunkMesh = CHUNK_TRANSPARENT_MESHES.get(key);
                    chunkMesh.load();

                    worldShader.transform(new Vector3d(key.x() * World.chunkScale, 0.0, key.y() * World.chunkScale), new Vector3d(), new Vector3d(1.0));

                    chunkMesh.render(texture);
                    chunkMesh.unload();
                } else {
                    CHUNK_MESHES.remove(key);
                    CHUNK_TRANSPARENT_MESHES.remove(key);
                }
            }
            OpenGl.cullFace(true);

            if(keyboard.getClick(Keyboard.KEY_G))
                world.spawnEntity(new FallingBlockEntity(new CubeCollider(new Vector3d(Math.floor(playerEntity.collider.position.x()), Math.floor(playerEntity.collider.position.y()), Math.floor(playerEntity.collider.position.z())), new Vector3d(1.0)), Blocks.SAND, 1.0));

            selection.load();

            worldShader.transform(selection.position, new Vector3d(), new Vector3d(1.0));

            selection.render();
            selection.unload();

            worldShader.unload();

//            window.imGuiBegin();
//            worldGenGui.render(window, world, time);
//            crosshairGui.render(window);
//            pauseMenuGui.render(window, mouse);
//
//            window.imGuiEnd();
//            worldGenGui.clear();
        }

        quit();
    }
    public static void updateMesh(World world, Vector2i key) {
        Chunk chunk = world.getChunk(key);
        if(chunk != null) {
            Mesh[] meshes = ChunkMeshBuilder.build(chunk);
            CHUNK_MESHES.replace(key, meshes[0]);
            CHUNK_TRANSPARENT_MESHES.replace(key, meshes[1]);
        }
    }

    public static void quit() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();

        LOGGER.traceExit();

        crosshairGui.clear();
        world.clear();
        world.close();

        worldShader.clear();
        window.close();

        System.exit(0);
    }

    private static double getTransformedViewDistance() {
        return viewDistance / (World.chunkScale / 16.0);
    }
}