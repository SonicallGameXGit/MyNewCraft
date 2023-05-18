package org.mynewcraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.client.graphics.Camera;
import org.mynewcraft.client.graphics.shader.WorldShader;
import org.mynewcraft.client.graphics.util.BlockSelection;
import org.mynewcraft.engine.graphics.OpenGL;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.io.Keyboard;
import org.mynewcraft.engine.io.Mouse;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.texture.Texture;
import org.mynewcraft.engine.math.MathUtil;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.math.physics.RayHitResult;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.Blocks;
import org.mynewcraft.world.block.custom.Block;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.chunk.ChunkMeshBuilder;

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

        Chunk chunk = new Chunk(new Vector2i(), new Random().nextInt());
        Mesh chunkMesh = ChunkMeshBuilder.build(chunk);

        LOGGER.debug(chunk.getSeed());

        Camera camera = new Camera(new Vector3d(0.0, 128.0, 0.0), new Vector3d());

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

            Vector3d direction = new Vector3d();

            if(keyboard.getPress(Keyboard.KEY_W))
                direction.add(MathUtil.angleToDirection(new Vector2d(0.0, camera.rotation.y())));
            if(keyboard.getPress(Keyboard.KEY_S))
                direction.add(MathUtil.angleToDirection(new Vector2d(0.0, camera.rotation.y() + 180.0)));
            if(keyboard.getPress(Keyboard.KEY_D))
                direction.add(MathUtil.angleToDirection(new Vector2d(0.0, camera.rotation.y() + 90.0)));
            if(keyboard.getPress(Keyboard.KEY_A))
                direction.add(MathUtil.angleToDirection(new Vector2d(0.0, camera.rotation.y() - 90.0)));
            if(keyboard.getPress(Keyboard.KEY_LEFT_SHIFT))
                direction.sub(new Vector3d(0.0, 1.0, 0.0));
            if(keyboard.getPress(Keyboard.KEY_SPACE))
                direction.add(new Vector3d(0.0, 1.0, 0.0));
            if(keyboard.getPress(Keyboard.KEY_F3)) {
                if(keyboard.getClick(Keyboard.KEY_T))
                    texture = AtlasGenerator.generate(new Identifier(GAME_ID, "block"));
                if(keyboard.getClick(Keyboard.KEY_R))
                    worldShader = new WorldShader(window, GAME_ID);
            }
            if(keyboard.getClick(Keyboard.KEY_F11)) window.setFullscreen(!window.getFullscreen());
            if(keyboard.getClick(Keyboard.KEY_ESCAPE)) mouse.grab(!mouse.getGrabbed());
            if(direction.length() > 1.0) direction.normalize();

            double nearestDistance = Double.POSITIVE_INFINITY;
            RayHitResult nearestHitResult = null;
            AbstractBlock nearestBlock = null;

            for(Vector3i coordinate : chunk.getCoordinates()) {
                RayHitResult hitResult = new CubeCollider(new Vector3d(coordinate), new Vector3d(1.0)).processRaycast(camera.position, MathUtil.angleToDirection(new Vector2d(camera.rotation.x(), camera.rotation.y())));

                if(hitResult != null && hitResult.hitPoint().distance(camera.position) < nearestDistance) {
                    nearestDistance = hitResult.hitPoint().distance(camera.position);
                    nearestHitResult = hitResult;
                    nearestBlock = chunk.getMap().get(coordinate);
                }
            }

            if(nearestHitResult != null && nearestDistance <= 6.0) {
                if(mouse.getClick(Mouse.BUTTON_RIGHT)) {
                    Vector3d blockPos = new Vector3d(nearestHitResult.hitObject().position).add(nearestHitResult.hitNormal());
                    Vector3i intBlockPos = new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z());
                    if(!chunk.getMap().containsKey(intBlockPos)) {
                        chunk.getMap().put(new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z()), Blocks.COBBLESTONE);
                        chunkMesh = ChunkMeshBuilder.build(chunk);
                    }
                }
                if(mouse.getClick(Mouse.BUTTON_LEFT)) {
                    if(nearestBlock instanceof Block block && block.getBreakable() || !(nearestBlock instanceof Block)) {
                        Vector3d blockPos = nearestHitResult.hitObject().position;
                        chunk.getMap().remove(new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z()));
                        chunkMesh = ChunkMeshBuilder.build(chunk);
                    }
                }

                selection.position = nearestHitResult.hitObject().position;
                selection.enabled();
            } else selection.disabled();

            camera.position.add(direction.mul(25.0 * time.getDelta()));
            camera.rotation.add(new Vector3d(new Vector2d(mouse.getDirection().y(), mouse.getDirection().x()).mul(!mouse.getGrabbed() ? 0.0 : -0.05), 0.0));

            worldShader.load();
            worldShader.project(90.0, 0.05, 1000.0);
            worldShader.view(camera.position, camera.rotation);

            chunkMesh.load();

            worldShader.transform(new Vector3d(chunk.getOffset().x(), 0.0, chunk.getOffset().y()), new Vector3d(), new Vector3d(1.0));

            chunkMesh.render(texture);
            chunkMesh.unload();

            selection.load();

            worldShader.transform(selection.position, new Vector3d(), new Vector3d(1.0));

            selection.render();
            selection.unload();

            worldShader.unload();
        }

        LOGGER.traceExit();

        chunkMesh.clear();
        worldShader.clear();
        window.close();

        System.exit(0);
    }
}