package org.mynewcraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.mynewcraft.world.entity.custom.PlayerEntity;

import java.io.IOException;
import java.util.Random;

public class MyNewCraft {
    public static final String GAME_ID = "mynewcraft";
    public static final String RESOURCE_PACK = "MyNewCraft";
    public static final Logger LOGGER = LogManager.getLogger(GAME_ID);

    public static void main(String[] args) throws IOException {
        Window window = new Window(1920.0 * 1.5, 1080.0 * 1.5, "MyNewCraft", true, false, true);

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

        World world = new World(new Random().nextLong(), 0.4);

        LOGGER.debug("Seed: " + world.SEED);

        PlayerEntity playerEntity = new PlayerEntity(new CubeCollider(new Vector3d(0.0, 128.0, 0.0), new Vector3d(0.6, 1.8, 0.6)), new Vector3d(), 0.6, 15.0);

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
                if(keyboard.getClick(Keyboard.KEY_R))
                    worldShader = new WorldShader(window, GAME_ID);
            }
            if(keyboard.getClick(Keyboard.KEY_F11)) window.setFullscreen(!window.getFullscreen());
            if(keyboard.getClick(Keyboard.KEY_ESCAPE)) mouse.grab(!mouse.getGrabbed());

//            double nearestDistance = Double.POSITIVE_INFINITY;
//            RayHitResult nearestHitResult = null;
//            AbstractBlock nearestBlock = null;
//
//            for(Vector3i coordinate : chunk.getCoordinates()) {
//                RayHitResult hitResult = new CubeCollider(new Vector3d(coordinate), new Vector3d(1.0)).processRaycast(camera.position, MathUtil.angleToDirection(new Vector2d(camera.rotation.x(), camera.rotation.y())));
//
//                if(hitResult != null && hitResult.hitPoint().distance(camera.position) < nearestDistance) {
//                    nearestDistance = hitResult.hitPoint().distance(camera.position);
//                    nearestHitResult = hitResult;
//                    nearestBlock = chunk.getMap().get(coordinate);
//                }
//            }
//
//            if(nearestHitResult != null && nearestDistance <= 6.0) {
//                if(mouse.getClick(Mouse.BUTTON_RIGHT)) {
//                    Vector3d blockPos = new Vector3d(nearestHitResult.hitObject().position).add(nearestHitResult.hitNormal());
//                    Vector3i intBlockPos = new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z());
//                    if(!chunk.getMap().containsKey(intBlockPos)) {
//                        chunk.getMap().put(new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z()), Blocks.COBBLESTONE);
//                        chunkMesh = ChunkMeshBuilder.build(chunk);
//                    }
//                }
//                if(mouse.getClick(Mouse.BUTTON_LEFT)) {
//                    if(nearestBlock instanceof Block block && block.getBreakable() || !(nearestBlock instanceof Block)) {
//                        Vector3d blockPos = nearestHitResult.hitObject().position;
//                        chunk.getMap().remove(new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z()));
//                        chunkMesh = ChunkMeshBuilder.build(chunk);
//                    }
//                }
//
//                selection.position = nearestHitResult.hitObject().position;
//                selection.enabled();
//            } else selection.disabled();

            playerEntity.update(world, keyboard, mouse, time);

            worldShader.load();
            worldShader.project(90.0, 0.05, 1000.0);
            worldShader.view(new Vector3d(playerEntity.collider.position).add(new Vector3d(playerEntity.collider.scale.x() / 2.0, playerEntity.collider.scale.y() - 0.2, playerEntity.collider.scale.z() / 2.0)), playerEntity.rotation);

            for(Vector2i key : world.CHUNK_MESHES.keySet()) {
                Mesh chunkMesh = world.CHUNK_MESHES.get(key);

                chunkMesh.load();

                worldShader.transform(new Vector3d(key.x() * 16.0, 0.0, key.y() * 16.0), new Vector3d(), new Vector3d(1.0));

                chunkMesh.render(texture);
                chunkMesh.unload();
            }

            selection.load();

            worldShader.transform(selection.position, new Vector3d(), new Vector3d(1.0));

            selection.render();
            selection.unload();

            worldShader.unload();
        }

        LOGGER.traceExit();

        world.clear();
        worldShader.clear();
        window.close();

        System.exit(0);
    }
}