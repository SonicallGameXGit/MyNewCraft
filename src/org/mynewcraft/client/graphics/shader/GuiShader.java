package org.mynewcraft.client.graphics.shader;

import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.shader.Shader;

public class GuiShader extends Shader {
    private final Window WINDOW;

    protected GuiShader(Window window, String gameId) {
        super("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/gui.vert", "resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/gui.frag");
        WINDOW = window;
    }

    @Override
    protected void loadVariables() {
        createVariable("project");
        createVariable("transform");
    }

    @Override
    protected void loadAttributes() {
        createAttribute(0, "position");
        createAttribute(1, "texcoord");
    }
}
