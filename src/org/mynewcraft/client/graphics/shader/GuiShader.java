package org.mynewcraft.client.graphics.shader;

import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.shader.custom.Default2dShader;

public class GuiShader extends Default2dShader {
    public GuiShader() {
        super("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + MyNewCraft.GAME_ID + "/shaders/gui.vert", "resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + MyNewCraft.GAME_ID + "/shaders/gui.frag");
    }
}
