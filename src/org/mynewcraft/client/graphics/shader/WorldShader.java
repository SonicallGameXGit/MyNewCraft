package org.mynewcraft.client.graphics.shader;

import org.joml.Vector3d;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.shader.custom.Default3dShader;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;

public class WorldShader extends Default3dShader {
    public WorldShader(Window window) {
        super("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + MyNewCraft.GAME_ID + "/shaders/world.vert", "resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + MyNewCraft.GAME_ID + "/shaders/world.frag", window);
    }

    public void fog(Vector3d color) {
        setVariable("viewDistance", 1.0 / (MyNewCraft.viewDistance * World.chunkScale));
        setVariable("skyColor", color);
    }
    public void time(Time time) {
        setVariable("time", time.getTime());
    }
}