package org.mynewcraft.client.graphics.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.texture.Texture;

public class CrosshairGui {
    private final Texture crosshairTexture;

    public CrosshairGui() {
        crosshairTexture = new Texture("resourcepacks/" + MyNewCraft.RESOURCE_PACK + "/" + MyNewCraft.GAME_ID + "/textures/gui/widgets.png", Texture.NEAREST);
    }

    public void render(Window window) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("crosshair", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoMove);
        ImGui.setWindowPos((float) window.getScale().x() / 2.0f - 16.0f, (float) window.getScale().y() / 2.0f - 16.0f);
        ImGui.setWindowSize(32.0f, 32.0f);

        ImGui.image(crosshairTexture.getId(), 32.0f, 32.0f);

        ImGui.end();
        ImGui.popStyleVar();
    }
    public void clear() {
        crosshairTexture.clear();
    }
}