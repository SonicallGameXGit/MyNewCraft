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
import org.mynewcraft.client.graphics.shader.GuiShader;
import org.mynewcraft.client.graphics.shader.WorldShader;
import org.mynewcraft.client.graphics.util.BlockSelection;
import org.mynewcraft.engine.graphics.OpenGl;
import org.mynewcraft.engine.graphics.ScreenTexture;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.graphics.mesh.MeshBuffer;
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
import org.mynewcraft.world.chunk.AbstractChunkMesh;
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

    public static final ArrayList<Chunk> meshesToUpdate = new ArrayList<>();

    public static final int viewDistance = 40;

    public static boolean paused = false;

    private static Window window;
    private static World world;

    private static WorldShader worldShader;
    private static GuiShader guiShader;

    private static Texture[] textures;

    private static ScreenTexture screenTexture;
    private static Mesh screenMesh;

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

        OpenGl.setClearColor(skyColor);
        OpenGl.cullFace(true);
        OpenGl.outlineWidth(5.0);
        OpenGl.enableDepthTest(true);

        Keyboard keyboard = new Keyboard(window);
        Mouse mouse = new Mouse(window);
        mouse.grab(true);

        Time time = new Time(4.0);

        worldShader = new WorldShader(window);
        guiShader = new GuiShader();

        textures = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
        Texture texture = textures[0];
        Texture normalTexture = textures[1];

        screenTexture = new ScreenTexture(window.getScale().x(), window.getScale().y(), Texture.LINEAR, ScreenTexture.COLOR_AND_DEPTH);
        screenMesh = new Mesh(null, new MeshBuffer(new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f }, 2), new MeshBuffer(new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f }, 2), null, Mesh.TRIANGLES, false);

        Blocks.register();

        world = new World(new Random().nextLong(), 32.0, 12.0, PlayerEntity.SPECTATOR_GAMEMODE);
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
                    normalTexture.clear();
                    textures = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
                    texture = textures[0];
                    normalTexture = textures[1];
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
            if(keyboard.getClick(Keyboard.KEY_F7)) wireframeMode = !wireframeMode;
            if(keyboard.getClick(Keyboard.KEY_F11)) window.setFullscreen(!window.getFullscreen());
            if(keyboard.getClick(Keyboard.KEY_ESCAPE)) {
                paused = !paused;

                mouse.grab(!paused);
                mouse.setPosition(new Vector2d(window.getScale().x() / 2.0, window.getScale().y() / 2.0 - 64.0));
            }
            if(!paused) {
                world.update(time);
                playerEntity.update(world, selection, keyboard, mouse, time);
            } else time.pause();

            for(Chunk chunk : meshesToUpdate) {
                AbstractChunkMesh[] chunkMeshes = ChunkMeshBuilder.build(chunk);
                loadChunkMesh(chunkMeshes[0], chunkMeshes[1]);
            }

            meshesToUpdate.clear();

            int chunkX = (int) Math.floor(playerEntity.collider.position.x() / World.chunkScale);
            int chunkZ = (int) Math.floor(playerEntity.collider.position.z() / World.chunkScale);

            for(int i = 0; i <= viewDistance / 2; i++)
                for(int x = chunkX - i; x < chunkX + i; x++)
                    for(int z = chunkZ - i; z < chunkZ + i; z++)
                        if(new Vector2d(x, z).distance(chunkX, chunkZ) <= i)
                            world.loadChunk(new Vector2i(x, z));
            for(Vector2i key : world.CHUNKS.keySet()) {
                Chunk chunk = world.getChunk(key);

                if(key.distance(chunkX, chunkZ) > viewDistance / 2.0) {
                    world.removeChunk(key);

                    CHUNK_MESHES.remove(key);
                    CHUNK_TRANSPARENT_MESHES.remove(key);
                }
                if(key.distance(chunkX, chunkZ) <= viewDistance / 2.0 && chunk != null && !CHUNK_MESHES.containsKey(key)) {
                    if(!chunk.getGenerated())
                        world.loadChunk(key);
                    else updateMesh(world, key);
                }
            }

            //screenTexture.load(window, new Vector3d());

            worldShader.load();
            worldShader.project(90.0, 0.05, 1000.0);
            worldShader.view(new Vector3d(playerEntity.collider.position).add(new Vector3d(playerEntity.collider.scale.x() / 2.0, playerEntity.collider.scale.y() - 0.2, playerEntity.collider.scale.z() / 2.0)), playerEntity.rotation);
            worldShader.fog(skyColor);
            worldShader.time(time);

            OpenGl.outlineOnly(wireframeMode);

            for(Vector2i key : new ArrayList<>(CHUNK_MESHES.keySet())) {
                if(new Vector2d(key).distance(chunkX, chunkZ) <= viewDistance / 2.0) {
                    Mesh chunkMesh = CHUNK_MESHES.get(key);
                    chunkMesh.load();

                    worldShader.transform(new Vector3d(key.x() * World.chunkScale, 0.0, key.y() * World.chunkScale), new Vector3d(), new Vector3d(1.0));

                    chunkMesh.render(new Texture[] { texture, normalTexture });
                    chunkMesh.unload();
                }
            }
            for(AbstractEntity entity : new ArrayList<>(world.ENTITIES))
                if(entity instanceof FallingBlockEntity fallingBlockEntity)
                    fallingBlockEntity.render(worldShader, new Texture[] { texture, normalTexture });

            OpenGl.cullFace(false);
            for(Vector2i key : new ArrayList<>(CHUNK_TRANSPARENT_MESHES.keySet())) {
                if(new Vector2d(key).distance(chunkX, chunkZ) <= viewDistance / 2.0) {
                    Mesh chunkMesh = CHUNK_TRANSPARENT_MESHES.get(key);
                    chunkMesh.load();

                    worldShader.transform(new Vector3d(key.x() * World.chunkScale, 0.0, key.y() * World.chunkScale), new Vector3d(), new Vector3d(1.0));

                    chunkMesh.render(new Texture[] { texture, normalTexture });
                    chunkMesh.unload();
                } else {
                    CHUNK_MESHES.remove(key);
                    CHUNK_TRANSPARENT_MESHES.remove(key);
                }
            }
            OpenGl.cullFace(true);

            selection.load();

            worldShader.transform(selection.position, new Vector3d(), new Vector3d(1.0));

            selection.render();
            selection.unload();

            worldShader.unload();
//            screenTexture.unload(window, skyColor);
//
//            guiShader.load();
//
//            screenMesh.load();
//            screenMesh.render(screenTexture.getColorTextureId());
//            screenMesh.unload();
//
//            guiShader.unload();
        }

        quit();
    }

    private static void loadChunkMesh(AbstractChunkMesh chunk, AbstractChunkMesh chunkA) {
        if(chunk != null) {
            Mesh chunkMesh = new Mesh(null, new MeshBuffer(chunk.vertices(), 3), new MeshBuffer(chunk.texcoords(), 2), new MeshBuffer[] { new MeshBuffer(chunk.normals(), 3), new MeshBuffer(chunk.alphas(), 1), new MeshBuffer(chunk.aoLevels(), 1), new MeshBuffer(chunk.tangents(), 3) }, Mesh.TRIANGLES, false);

            if(CHUNK_MESHES.containsKey(chunk.offset()))
                CHUNK_MESHES.replace(chunk.offset(), chunkMesh);
            else CHUNK_MESHES.put(chunk.offset(), chunkMesh);
        }
        if(chunkA != null) {
            Mesh chunkMeshA = new Mesh(null, new MeshBuffer(chunkA.vertices(), 3), new MeshBuffer(chunkA.texcoords(), 2), new MeshBuffer[] { new MeshBuffer(chunkA.normals(), 3), new MeshBuffer(chunkA.alphas(), 1), new MeshBuffer(chunkA.aoLevels(), 1), new MeshBuffer(chunkA.tangents(), 3) }, Mesh.TRIANGLES, false);
            if(CHUNK_TRANSPARENT_MESHES.containsKey(chunkA.offset()))
                CHUNK_TRANSPARENT_MESHES.replace(chunkA.offset(), chunkMeshA);
            else CHUNK_TRANSPARENT_MESHES.put(chunkA.offset(), chunkMeshA);
        }
    }

    public static void updateMesh(World world, Vector2i key) {
        AbstractChunkMesh[] chunkMeshes = ChunkMeshBuilder.build(world.getChunk(key));
        loadChunkMesh(chunkMeshes[0], chunkMeshes[1]);
    }

    public static void quit() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();

        world.clear();
        world.close();

        meshesToUpdate.clear();

        textures[0].clear();
        textures[1].clear();

        screenTexture.clear();

        worldShader.clear();
        guiShader.clear();

        screenMesh.clear();

        window.close();

        LOGGER.traceExit();
        System.exit(0);
    }
}