package org.mynewcraft.client.client;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import org.auburn.fnl.FastNoiseLite;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.texture.Texture;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.chunk.Chunk;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;

public class WorldGenGui {
    private static class Wg {
        private static final ImInt generalNoiseType = new ImInt(Chunk.worldGenNoise.mNoiseType.ordinal());
        private static final ImInt fractalType = new ImInt(Chunk.worldGenNoise.mFractalType.ordinal());
        private static final ImInt cellularDistanceFunction = new ImInt(Chunk.worldGenNoise.mCellularDistanceFunction.ordinal());
        private static final ImInt cellularReturnType = new ImInt(Chunk.worldGenNoise.mCellularReturnType.ordinal());

        private static final int[] fractalOctaves = new int[] { Chunk.worldGenNoise.mOctaves };

        private static final float[] generalFrequency = new float[] { Chunk.worldGenNoise.mFrequency };
        private static final float[] fractalLacunarity = new float[] { Chunk.worldGenNoise.mLacunarity };
        private static final float[] fractalGain = new float[] { Chunk.worldGenNoise.mGain };
        private static final float[] fractalWeightedStrength = new float[] { Chunk.worldGenNoise.mWeightedStrength };
        private static final float[] fractalPingPongStrength = new float[] { Chunk.worldGenNoise.mPingPongStength };
        private static final float[] cellularJitter = new float[] { Chunk.worldGenNoise.mCellularJitterModifier };

        private static Texture worldPreviewId;

        private static boolean previewEnabled = true;
    }
    private static class Cg {
        private static final ImInt generalNoiseType = new ImInt(Chunk.caveGenNoise.mNoiseType.ordinal());
        private static final ImInt rotationType3d = new ImInt(Chunk.caveGenNoise.mRotationType3D.ordinal());
        private static final ImInt fractalType = new ImInt(Chunk.caveGenNoise.mFractalType.ordinal());
        private static final ImInt cellularDistanceFunction = new ImInt(Chunk.caveGenNoise.mCellularDistanceFunction.ordinal());
        private static final ImInt cellularReturnType = new ImInt(Chunk.caveGenNoise.mCellularReturnType.ordinal());
        private static final ImInt domainWarpType = new ImInt(Chunk.caveGenNoise.mDomainWarpType.ordinal());

        private static final int[] fractalOctaves = new int[] { Chunk.caveGenNoise.mOctaves };

        private static final float[] generalFrequency = new float[] { Chunk.caveGenNoise.mFrequency };
        private static final float[] fractalLacunarity = new float[] { Chunk.caveGenNoise.mLacunarity };
        private static final float[] fractalGain = new float[] { Chunk.caveGenNoise.mGain };
        private static final float[] fractalWeightedStrength = new float[] { Chunk.caveGenNoise.mWeightedStrength };
        private static final float[] fractalPingPongStrength = new float[] { Chunk.caveGenNoise.mPingPongStength };
        private static final float[] cellularJitter = new float[] { Chunk.caveGenNoise.mCellularJitterModifier };
        private static final float[] domainWarpAmp = new float[] { Chunk.caveGenNoise.mDomainWarpAmp };

        private static Texture worldPreviewId;

        private static double previewOffset = 0.0;

        private static boolean previewEnabled = true;
    }

    private boolean enabled = false;
    private boolean worldGeneratorMenuOpen = true;
    private boolean caveGeneratorMenuOpen = true;
    private boolean worldChanged = false;

    private final int clipboardTextureId;

    public WorldGenGui() {
        ImGuiStyle style = ImGui.getStyle();
        style.setAlpha(1.0f);
        style.setDisabledAlpha(1.0f);
        style.setWindowPadding(12.0f, 12.0f);
        style.setWindowRounding(0.0f);
        style.setWindowBorderSize(0.0f);
        style.setWindowMinSize(20.0f, 20.0f);
        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setWindowMenuButtonPosition(ImGuiDir.None);
        style.setChildRounding(0.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupRounding(0.0f);
        style.setPopupBorderSize(1.0f);
        style.setFramePadding(6.0f, 6.0f);
        style.setFrameRounding(0.0f);
        style.setFrameBorderSize(0.0f);
        style.setItemSpacing(12.0f, 6.0f);
        style.setItemInnerSpacing(6.0f, 3.0f);
        style.setCellPadding(12.0f, 6.0f);
        style.setIndentSpacing(20.0f);
        style.setColumnsMinSpacing(6.0f);
        style.setScrollbarSize(24.0f);
        style.setScrollbarRounding(64.0f);
        style.setGrabMinSize(12.0f);
        style.setGrabRounding(0.0f);
        style.setTabRounding(0.0f);
        style.setTabBorderSize(0.0f);
        style.setTabMinWidthForCloseButton(0.0f);
        style.setColorButtonPosition(ImGuiDir.Right);
        style.setButtonTextAlign(0.5f, 0.5f);
        style.setSelectableTextAlign(0.0f, 0.0f);

        style.setColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.TextDisabled, 0.2745098173618317f, 0.3176470696926117f, 0.4509803950786591f, 1.0f);
        style.setColor(ImGuiCol.WindowBg, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.ChildBg, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.PopupBg, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.Border, 0.1568627506494522f, 0.168627455830574f, 0.1921568661928177f, 1.0f);
        style.setColor(ImGuiCol.BorderShadow, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.FrameBg, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.1568627506494522f, 0.168627455830574f, 0.1921568661928177f, 1.0f);
        style.setColor(ImGuiCol.FrameBgActive, 0.2352941185235977f, 0.2156862765550613f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.TitleBg, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TitleBgActive, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.MenuBarBg, 0.09803921729326248f, 0.105882354080677f, 0.1215686276555061f, 1.0f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.1568627506494522f, 0.168627455830574f, 0.1921568661928177f, 1.0f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.CheckMark, 0.4980392158031464f, 0.5137255191802979f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.SliderGrab, 0.4980392158031464f, 0.5137255191802979f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.5372549295425415f, 0.5529412031173706f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.Button, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.ButtonHovered, 0.196078434586525f, 0.1764705926179886f, 0.5450980663299561f, 1.0f);
        style.setColor(ImGuiCol.ButtonActive, 0.2352941185235977f, 0.2156862765550613f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.Header, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.HeaderHovered, 0.196078434586525f, 0.1764705926179886f, 0.5450980663299561f, 1.0f);
        style.setColor(ImGuiCol.HeaderActive, 0.2352941185235977f, 0.2156862765550613f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.Separator, 0.1568627506494522f, 0.1843137294054031f, 0.250980406999588f, 1.0f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.1568627506494522f, 0.1843137294054031f, 0.250980406999588f, 1.0f);
        style.setColor(ImGuiCol.SeparatorActive, 0.1568627506494522f, 0.1843137294054031f, 0.250980406999588f, 1.0f);
        style.setColor(ImGuiCol.ResizeGrip, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.196078434586525f, 0.1764705926179886f, 0.5450980663299561f, 1.0f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.2352941185235977f, 0.2156862765550613f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.Tab, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TabHovered, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.TabActive, 0.09803921729326248f, 0.105882354080677f, 0.1215686276555061f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocused, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.0784313753247261f, 0.08627451211214066f, 0.1019607856869698f, 1.0f);
        style.setColor(ImGuiCol.PlotLines, 0.5215686559677124f, 0.6000000238418579f, 0.7019608020782471f, 1.0f);
        style.setColor(ImGuiCol.PlotLinesHovered, 0.03921568766236305f, 0.9803921580314636f, 0.9803921580314636f, 1.0f);
        style.setColor(ImGuiCol.PlotHistogram, 1.0f, 0.2901960909366608f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 0.9960784316062927f, 0.4745098054409027f, 0.6980392336845398f, 1.0f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TableBorderStrong, 0.0470588244497776f, 0.05490196123719215f, 0.07058823853731155f, 1.0f);
        style.setColor(ImGuiCol.TableBorderLight, 0.0f, 0.0f, 0.0f, 1.0f);
        style.setColor(ImGuiCol.TableRowBg, 0.1176470592617989f, 0.1333333402872086f, 0.1490196138620377f, 1.0f);
        style.setColor(ImGuiCol.TableRowBgAlt, 0.09803921729326248f, 0.105882354080677f, 0.1215686276555061f, 1.0f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.2352941185235977f, 0.2156862765550613f, 0.5960784554481506f, 1.0f);
        style.setColor(ImGuiCol.DragDropTarget, 0.4980392158031464f, 0.5137255191802979f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.NavHighlight, 0.4980392158031464f, 0.5137255191802979f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.NavWindowingHighlight, 0.4980392158031464f, 0.5137255191802979f, 1.0f, 1.0f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.196078434586525f, 0.1764705926179886f, 0.5450980663299561f, 0.501960813999176f);
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.196078434586525f, 0.1764705926179886f, 0.5450980663299561f, 0.501960813999176f);

        Wg.worldPreviewId = generatePreview(Chunk.worldGenNoise);
        Cg.worldPreviewId = generatePreview3d(null, Chunk.caveGenNoise, 0.0);

        clipboardTextureId = new Texture("resourcepacks/" + MyNewCraft.RESOURCE_PACK + "/" + MyNewCraft.GAME_ID + "/textures/gui/imgui/clipboard.png", Texture.LINEAR).getId();
    }

    public void render(Window window, World world, Time time) {
        worldChanged = false;

        if(enabled) {
            ImGui.begin("WorldGenGui", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove);
            ImGui.setWindowSize(800.0f, (float) window.getScale().y());
            ImGui.setWindowPos(0.0f, 0.0f);

            ImGui.setCursorPosX(((float) window.getScale().x() * 0.2f - ImGui.calcTextSize(" - World Generator - ").x) / 2.0f);

            ImGui.sameLine();

            if(ImGui.arrowButton("wg_menu", worldGeneratorMenuOpen ? ImGuiDir.Down : ImGuiDir.Right))
                worldGeneratorMenuOpen = !worldGeneratorMenuOpen;

            ImGui.sameLine();
            ImGui.text(" - World Generator - ");

            if(worldGeneratorMenuOpen) {
                ImGui.separator();

                if(setNoiseType(Chunk.worldGenNoise, "wg", Wg.generalNoiseType))
                    worldChanged = true;
                if(setFloat("wg_general_frequency", "Frequency ", Wg.generalFrequency, 0.001f, 0.4f)) {
                    worldChanged = true;
                    Chunk.worldGenNoise.SetFrequency(Wg.generalFrequency[0]);
                }

                ImGui.separator();

                if(setFractalType(Chunk.worldGenNoise, "wg", Wg.fractalType))
                    worldChanged = true;
                if(setInt("wg_fractal_octaves", "Fractal Octaves", Wg.fractalOctaves, 1, 10)) {
                    worldChanged = true;
                    Chunk.worldGenNoise.SetFractalOctaves(Wg.fractalOctaves[0]);
                }
                if(setFloat("wg_fractal_lacunarity", "Fractal Lacunarity", Wg.fractalLacunarity, 0.0f, 2.0f)) {
                    worldChanged = true;
                    Chunk.worldGenNoise.SetFractalLacunarity(Wg.fractalLacunarity[0]);
                }
                if(setFloat("wg_fractal_gain", "Fractal Gain", Wg.fractalGain, 0.0f, 1.0f)) {
                    worldChanged = true;
                    Chunk.worldGenNoise.SetFractalGain(Wg.fractalGain[0]);
                }
                if(setFloat("wg_fractal_weighted_strength", "Fractal Weighted Strength", Wg.fractalWeightedStrength, 0.0f, 2.0f)) {
                    worldChanged = true;
                    Chunk.worldGenNoise.SetFractalWeightedStrength(Wg.fractalWeightedStrength[0]);
                }
                if(Wg.fractalType.get() == 3) {
                    if(setFloat("wg_fractal_ping_pong_strength", "Fractal Ping Pong Strength", Wg.fractalPingPongStrength, 0.0f, 2.0f)) {
                        worldChanged = true;
                        Chunk.worldGenNoise.SetFractalPingPongStrength(Wg.fractalPingPongStrength[0]);
                    }
                }

                if(Wg.generalNoiseType.get() == 2) {
                    ImGui.separator();

                    if(setCellularReturnType(Chunk.worldGenNoise, "wg", Wg.cellularReturnType))
                        worldChanged = true;
                    if(setCellularDistanceFunction(Chunk.worldGenNoise, "wg", Wg.cellularDistanceFunction))
                        worldChanged = true;
                    if(setFloat("wg_cellular_jitter", "Cellular Jitter", Wg.cellularJitter, 0.001f, 2.0f)) {
                        worldChanged = true;
                        Chunk.worldGenNoise.SetCellularJitter(Wg.cellularJitter[0]);
                    }
                }
            }

            if(ImGui.arrowButton("cg_menu", caveGeneratorMenuOpen ? ImGuiDir.Down : ImGuiDir.Right))
                caveGeneratorMenuOpen = !caveGeneratorMenuOpen;

            ImGui.sameLine();
            ImGui.text(" - Cave Generator - ");

            if(caveGeneratorMenuOpen) {
                ImGui.separator();

                setNoiseType(Chunk.caveGenNoise, "cg", Cg.generalNoiseType);
                setRotationType3d(Chunk.caveGenNoise, "cg", Cg.rotationType3d);

                if(setFloat("cg_general_frequency", "Frequency ", Cg.generalFrequency, 0.001f, 0.4f))
                    Chunk.caveGenNoise.SetFrequency(Cg.generalFrequency[0]);

                ImGui.separator();

                setFractalType(Chunk.caveGenNoise, "cg", Cg.fractalType);
                if(setInt("cg_fractal_octaves", "Fractal Octaves", Cg.fractalOctaves, 1, 10))
                    Chunk.caveGenNoise.SetFractalOctaves(Cg.fractalOctaves[0]);
                if(setFloat("cg_fractal_lacunarity", "Fractal Lacunarity", Cg.fractalLacunarity, 0.0f, 2.0f))
                    Chunk.caveGenNoise.SetFractalLacunarity(Cg.fractalLacunarity[0]);
                if(setFloat("cg_fractal_gain", "Fractal Gain", Cg.fractalGain, 0.0f, 1.0f))
                    Chunk.caveGenNoise.SetFractalGain(Cg.fractalGain[0]);
                if(setFloat("cg_fractal_weighted_strength", "Fractal Weighted Strength", Cg.fractalWeightedStrength, 0.0f, 2.0f))
                    Chunk.caveGenNoise.SetFractalWeightedStrength(Cg.fractalWeightedStrength[0]);
                if(Cg.fractalType.get() == 3)
                    if(setFloat("cg_fractal_ping_pong_strength", "Ping Pong Strength", Cg.fractalPingPongStrength, 0.0f, 2.0f))
                        Chunk.caveGenNoise.SetFractalPingPongStrength(Cg.fractalPingPongStrength[0]);

                if(Cg.generalNoiseType.get() == 2) {
                    ImGui.separator();

                    setCellularReturnType(Chunk.caveGenNoise, "cg", Cg.cellularReturnType);
                    setCellularDistanceFunction(Chunk.caveGenNoise, "cg", Cg.cellularDistanceFunction);

                    if(setFloat("cg_cellular_jitter", "Cellular Jitter", Cg.cellularJitter, 0.001f, 2.0f))
                        Chunk.caveGenNoise.SetCellularJitter(Cg.cellularJitter[0]);
                }

                setDomainWarpType(Chunk.caveGenNoise, "cg", Cg.domainWarpType);
                if(setFloat("cg_domain_warp_amp", "Domain Warp Amp", Cg.domainWarpAmp, 0.0f, 100.0f))
                    Chunk.caveGenNoise.SetDomainWarpAmp(Cg.domainWarpAmp[0]);
            }

            ImGui.separator();

            if(ImGui.arrowButton("##generate_world", ImGuiDir.Right))
                clearWorld(world);
            ImGui.sameLine();
            ImGui.text("Run");

            if(ImGui.imageButton(clipboardTextureId, ImGui.getFont().getFontSize() * ImGui.getFont().getScale(), ImGui.getFont().getFontSize() * ImGui.getFont().getScale())) {
                clearWorld(world);

                String result = "Chunk.worldGenNoise = new FastNoiseLite((int) seed);\n";
                result += "Chunk.worldGenNoise.SetNoiseType(FastNoiseLite.NoiseType." + Chunk.worldGenNoise.mNoiseType.toString() + ");\n";
                result += "Chunk.worldGenNoise.SetFrequency(" + Chunk.worldGenNoise.mFrequency + "f);\n";
                result += "Chunk.worldGenNoise.SetFractalType(FastNoiseLite.FractalType." + Chunk.worldGenNoise.mFractalType.toString() + ");\n";
                result += "Chunk.worldGenNoise.SetFractalOctaves(" + Chunk.worldGenNoise.mOctaves + ");\n";
                result += "Chunk.worldGenNoise.SetFractalLacunarity(" + Chunk.worldGenNoise.mLacunarity + "f);\n";
                result += "Chunk.worldGenNoise.SetFractalGain(" + Chunk.worldGenNoise.mGain + "f);\n";
                result += "Chunk.worldGenNoise.SetFractalWeightedStrength(" + Chunk.worldGenNoise.mWeightedStrength + "f);\n";
                if(Wg.fractalType.get() == 3)
                    result += "Chunk.worldGenNoise.SetFractalPingPongStrength(" + Chunk.worldGenNoise.mPingPongStength + "f);\n";

                result += "\nChunk.caveGenNoise = new FastNoiseLite((int) seed);\n";

                result += "Chunk.caveGenNoise.SetNoiseType(FastNoiseLite.NoiseType." + Chunk.caveGenNoise.mNoiseType.toString() + ");\n";
                result += "Chunk.caveGenNoise.SetFrequency(" + Chunk.caveGenNoise.mFrequency + "f);\n";
                result += "Chunk.caveGenNoise.SetFractalType(FastNoiseLite.FractalType." + Chunk.caveGenNoise.mFractalType.toString() + ");\n";
                result += "Chunk.caveGenNoise.SetFractalOctaves(" + Chunk.caveGenNoise.mOctaves + ");\n";
                result += "Chunk.caveGenNoise.SetFractalLacunarity(" + Chunk.caveGenNoise.mLacunarity + "f);\n";
                result += "Chunk.caveGenNoise.SetFractalGain(" + Chunk.caveGenNoise.mGain + "f);\n";
                result += "Chunk.caveGenNoise.SetFractalWeightedStrength(" + Chunk.caveGenNoise.mWeightedStrength + "f);\n";
                if(Wg.fractalType.get() == 3)
                    result += "Chunk.caveGenNoise.SetFractalPingPongStrength(" + Chunk.caveGenNoise.mPingPongStength + "f);\n";

                result += "Chunk.caveGenNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType." + Chunk.caveGenNoise.mDomainWarpType.toString() + ");\n";
                result += "Chunk.caveGenNoise.SetDomainWarpAmp(" + Chunk.caveGenNoise.mDomainWarpAmp + "f);";

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(result), null);
            }
            ImGui.sameLine();
            ImGui.text("Copy Preferences");

            ImGui.separator();

            if(worldChanged) {
                Wg.worldPreviewId.clear();
                Wg.worldPreviewId = generatePreview(Chunk.worldGenNoise);
            }
            if(ImGui.arrowButton("enable_height_map_preview", Wg.previewEnabled ? ImGuiDir.Down : ImGuiDir.Right))
                Wg.previewEnabled = !Wg.previewEnabled;

            ImGui.sameLine();
            ImGui.text("Height Map");

            if(Wg.previewEnabled) ImGui.image(Wg.worldPreviewId.getId(), 800.0f, 800.0f);

            Cg.worldPreviewId = generatePreview3d(Cg.worldPreviewId, Chunk.caveGenNoise, Cg.previewOffset);
            if(ImGui.arrowButton("enable_cave_map_preview", Cg.previewEnabled ? ImGuiDir.Down : ImGuiDir.Right))
                Cg.previewEnabled = !Cg.previewEnabled;

            ImGui.sameLine();
            ImGui.text("Cave Map");

            if(Cg.previewEnabled) ImGui.image(Cg.worldPreviewId.getId(), 800.0f, 800.0f);

            Cg.previewOffset += 5.0 * time.getDelta();

            ImGui.end();
        }
    }

    public void clear() {
        Cg.worldPreviewId.clear();
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    private void clearWorld(World world) {
        MyNewCraft.CHUNK_MESHES.clear();

        Chunk.caveGenNoise.SetSeed(Chunk.worldGenNoise.mSeed);
        Chunk.riverGenNoise.SetSeed(Chunk.worldGenNoise.mSeed);

        world.clear();
        MyNewCraft.LOGGER.debug("Seed: " + world.SEED);
    }

    private boolean setNoiseType(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_general_noise_type", "Noise Type");
        if(ImGui.combo("##" + title + "_general_noise_type", value, new String[] { "OpenSimplex2", "OpenSimplex2S", "Cellular", "Perlin", "ValueCubic", "Value" })) {
            switch(value.get()) {
                case 0 -> noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
                case 1 -> noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
                case 2 -> noise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
                case 3 -> noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
                case 4 -> noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
                case 5 -> noise.SetNoiseType(FastNoiseLite.NoiseType.Value);
            }

            return true;
        }

        return false;
    }

    private boolean setRotationType3d(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_rotation_type_3d", "Rotation Type 3d");
        if(ImGui.combo("##" + title + "_rotation_type_3d", value, new String[] { "None", "Improve XY", "Improve XZ" })) {
            switch(value.get()) {
                case 0 -> noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
                case 1 -> noise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXYPlanes);
                case 2 -> noise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
            }

            return true;
        }

        return false;
    }
    private boolean setDomainWarpType(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_domain_warp_type", "Domain Warp Type");
        if(ImGui.combo("##" + title + "_domain_warp_type", value, new String[] { "Open Simplex 2", "Open Simplex 2 Reduced", "Basic Grid" })) {
            switch(value.get()) {
                case 0 -> noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
                case 1 -> noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
                case 2 -> noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            }

            return true;
        }

        return false;
    }
    private boolean setFloat(String title, String label, float[] value, float min, float max) {
        ImGui.labelText("##" + title, label);
        return ImGui.sliderFloat("##" + title, value, min, max);
    }
    private boolean setFractalType(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_fractal_type", "Fractal Type");
        if(ImGui.combo("##" + title + "_fractal_type", value, new String[] { "None", "FBm", "Ridged", "Ping Pong" })) {
            switch(value.get()) {
                case 0 -> noise.SetFractalType(FastNoiseLite.FractalType.None);
                case 1 -> noise.SetFractalType(FastNoiseLite.FractalType.FBm);
                case 2 -> noise.SetFractalType(FastNoiseLite.FractalType.Ridged);
                case 3 -> noise.SetFractalType(FastNoiseLite.FractalType.PingPong);
            }

            return true;
        }

        return false;
    }
    private boolean setInt(String title, String label, int[] octaves, int min, int max) {
        ImGui.labelText("##" + title, label);
        return ImGui.sliderInt("##" + title, octaves, min, max);
    }
    private boolean setCellularReturnType(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_cellular_return_type", "Return Type ");
        if(ImGui.combo("##" + title + "_cellular_return_type", value, new String[] { "Cell Value", "Distance", "Distance 2", "Distance 2 Add", "Distance 2 Sub", "Distance 2 Mul", "Distance 2 Div" })) {
            switch(value.get()) {
                case 0 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
                case 1 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
                case 2 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2);
                case 3 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Add);
                case 4 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Sub);
                case 5 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Mul);
                case 6 -> noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Div);
            }

            return true;
        }

        return false;
    }
    private boolean setCellularDistanceFunction(FastNoiseLite noise, String title, ImInt value) {
        ImGui.labelText("##" + title + "_cellular_distance_function", "Distance Function");
        if(ImGui.combo("##" + title + "_cellular_distance_function", value, new String[] { "Euclidean", "Euclidean Sq", "Manhattan", "Hybrid" })) {
            switch(value.get()) {
                case 0 -> noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
                case 1 -> noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
                case 2 -> noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Manhattan);
                case 3 -> noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Hybrid);
            }

            return true;
        }

        return false;
    }

    private Texture generatePreview(FastNoiseLite noise) {
        BufferedImage texture = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = texture.getGraphics();
        for(float x = 0; x < texture.getWidth(); x += 1) {
            for(float y = 0; y < texture.getHeight(); y += 1) {
                float brightness = noise.GetNoise(x, y) / 2.0f + 0.5f;

                graphics.setColor(new Color(brightness, brightness, brightness));
                graphics.fillRect((int) x, (int) y, (int) x + 1, (int) y + 1);
            }
        }

        graphics.dispose();

        return new Texture(texture, Texture.LINEAR);
    }
    private Texture generatePreview3d(Texture origin, FastNoiseLite noise, double offset) {
        BufferedImage texture = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = texture.getGraphics();
        for(float x = 0; x < texture.getWidth(); x += 1) {
            for(float y = 0; y < texture.getHeight(); y += 1) {
                float brightness = noise.GetNoise(x, (float) offset, y) / 2.0f + 0.5f;

                graphics.setColor(new Color(brightness, brightness, brightness));
                graphics.fillRect((int) x, (int) y, (int) x + 1, (int) y + 1);
            }
        }

        graphics.dispose();

        return origin != null ? origin.update(texture, Texture.LINEAR) : new Texture(texture, Texture.LINEAR);
    }

    public boolean getDisabled() {
        return !enabled;
    }
}