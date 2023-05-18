package org.mynewcraft.client.graphics.shader;

import org.joml.Vector3d;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.Window;
import org.mynewcraft.engine.io.shader.Shader;
import org.mynewcraft.engine.math.MathUtil;

public class WorldShader extends Shader {
    private static final int NORMAL_ATTRIBUTE_ID = TEXCOORD_ATTRIBUTE_ID + 1;

    private final Window WINDOW;

    private final String TRANSFORM = "transform";
    private final String VIEW = "view";
    private final String PROJECT = "project";

    public WorldShader(Window window, String gameId) {
        super("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/world.vert", "resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + gameId + "/shaders/world.frag");
        WINDOW = window;
    }

    @Override
    protected void loadVariables() {
        createVariable(TRANSFORM);
        createVariable(VIEW);
        createVariable(PROJECT);
    }

    @Override
    protected void loadAttributes() {
        createAttribute(POSITION_ATTRIBUTE_ID, "position");
        createAttribute(TEXCOORD_ATTRIBUTE_ID, "texcoord");
        createAttribute(NORMAL_ATTRIBUTE_ID, "normal");
    }

    public void transform(Vector3d position, Vector3d rotation, Vector3d scale) {
        setVariable(TRANSFORM, MathUtil.transform(position, rotation, scale));
    }
    public void view(Vector3d position, Vector3d rotation) {
        setVariable(VIEW, MathUtil.view(position, rotation));
    }
    public void project(double fov, double near, double far) {
        setVariable(PROJECT, MathUtil.project(WINDOW, fov, near, far));
    }
}