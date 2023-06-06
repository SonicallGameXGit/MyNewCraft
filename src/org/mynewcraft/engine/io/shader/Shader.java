package org.mynewcraft.engine.io.shader;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public abstract class Shader {
    private final int programId;
    private final int vertexId;
    private final int fragmentId;

    private final Map<String, Integer> variables = new HashMap<>();

    protected Shader(String vertexLocation, String fragmentLocation) {
        programId = GL20.glCreateProgram();
        vertexId = loadShader(vertexLocation, GL20.GL_VERTEX_SHADER);
        fragmentId = loadShader(fragmentLocation, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programId, vertexId);
        GL20.glAttachShader(programId, fragmentId);
        GL20.glLinkProgram(programId);
        GL20.glValidateProgram(programId);

        if(GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Program error:\n\n" + GL20.glGetProgramInfoLog(programId));
            System.exit(1);
        }

        unload();
    }

    private int loadShader(String location, int type) {
        StringBuffer stringBuffer = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));

            String line;
            while((line = reader.readLine()) != null) stringBuffer.append(line).append('\n');

            reader.close();
        } catch(Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, stringBuffer);
        GL20.glCompileShader(shaderId);

        if(GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            if(type == GL20.GL_VERTEX_SHADER) {
                System.err.println("Vertex error:\n\n" + GL20.glGetShaderInfoLog(shaderId));
                System.exit(1);
            } else if(type == GL20.GL_FRAGMENT_SHADER) {
                System.err.println("Fragment error:\n\n" + GL20.glGetShaderInfoLog(shaderId));
                System.exit(1);
            }
        }

        return shaderId;
    }

    public void load() {
        GL20.glUseProgram(programId);
    }
    public void unload() {
        GL20.glUseProgram(0);
    }
    public void clear() {
        unload();

        GL20.glDetachShader(programId, vertexId);
        GL20.glDeleteShader(vertexId);
        GL20.glDetachShader(programId, fragmentId);
        GL20.glDeleteShader(fragmentId);
        GL20.glDeleteProgram(programId);
    }

    protected void setVariable(String title, boolean value) {
        if(variables.containsKey(title)) GL20.glUniform1i(variables.get(title), value ? 1 : 0);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, int value) {
        if(variables.containsKey(title)) GL20.glUniform1i(variables.get(title), value);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, float value) {
        if(variables.containsKey(title)) GL20.glUniform1f(variables.get(title), value);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, double value) {
        if(variables.containsKey(title)) GL20.glUniform1f(variables.get(title), (float) value);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Vector2d value) {
        if(variables.containsKey(title)) GL20.glUniform2f(variables.get(title), (float) value.x, (float) value.y);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Vector3d value) {
        if(variables.containsKey(title)) GL20.glUniform3f(variables.get(title), (float) value.x, (float) value.y, (float) value.z);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Vector4d value) {
        if(variables.containsKey(title)) GL20.glUniform4f(variables.get(title), (float) value.x, (float) value.y, (float) value.z, (float) value.w);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Matrix2f value) {
        float[] buffer = new float[4];
        value.get(buffer);

        if(variables.containsKey(title)) GL20.glUniformMatrix2fv(variables.get(title), false, buffer);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Matrix3f value) {
        float[] buffer = new float[9];
        value.get(buffer);

        if(variables.containsKey(title)) GL20.glUniformMatrix3fv(variables.get(title), false, buffer);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
    protected void setVariable(String title, Matrix4f value) {
        float[] buffer = new float[16];
        value.get(buffer);

        if(variables.containsKey(title)) GL20.glUniformMatrix4fv(variables.get(title), false, buffer);
        else variables.put(title, GL20.glGetUniformLocation(programId, title));
    }
}