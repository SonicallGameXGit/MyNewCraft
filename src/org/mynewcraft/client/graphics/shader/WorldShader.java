package org.mynewcraft.client.graphics.shader;

import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.shader.custom.Default3dShader;
import org.mynewcraft.engine.io.Window;

public class WorldShader extends Default3dShader {
    public WorldShader(Window window, String gameId) {
        super("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/world.vert", "resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/world.frag", window);
    }
}