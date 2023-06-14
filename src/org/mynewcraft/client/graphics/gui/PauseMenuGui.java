package org.mynewcraft.client.graphics.gui;

import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Mouse;
import org.mynewcraft.engine.io.Window;

public class PauseMenuGui {
    public void render(Window window, Mouse mouse) {
        if(MyNewCraft.paused) {
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.0f, 0.0f, 0.0f, 0.6f);

            ImFont font = ImGui.getFont();
            font.setScale(1.0f);
            ImGui.pushFont(font);
            font.setScale(0.5f);

            ImGui.begin("pause_menu", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove);

            ImGui.setWindowPos(0.0f, 0.0f);
            ImGui.setWindowSize((float) window.getScale().x(), (float) window.getScale().y());

            ImGui.setCursorPosY(ImGui.getWindowSizeY() / 2.0f - 134.0f);
            ImGui.setCursorPosX(ImGui.getWindowSizeX() / 2.0f - 256.0f);
            if(ImGui.button("Back to game", 512.0f, 128.0f)) {
                MyNewCraft.paused = false;
                mouse.grab(true);
            }

            ImGui.spacing();

            ImGui.setCursorPosX(ImGui.getWindowSizeX() / 2.0f - 256.0f);
            if(ImGui.button("Quit", 512.0f, 128.0f))
                MyNewCraft.quit();

            ImGui.end();
            ImGui.popStyleColor();
            ImGui.popFont();
        }
    }
}