package org.mynewcraft.engine.graphics.mesh;

import org.lwjgl.opengl.*;
import org.mynewcraft.engine.io.texture.Texture;

public class Mesh {
    public static final int TRIANGLES = GL11.GL_TRIANGLES;
    public static final int TRIANGLE_FAN = GL11.GL_TRIANGLE_FAN;
    public static final int TRIANGLE_STRIP = GL11.GL_TRIANGLE_FAN;
    public static final int LINES = GL11.GL_LINES;

    private final int[] elements;

    private final int RENDER_MODE;
    private final int VAOID;
    private final int VBOID;
    private final int verticesLength;
    private int EBOID;
    private int TBOID;

    private final boolean useEBO;

    private int[] additionalIds;

    public Mesh(int[] elements, MeshBuffer vertices, MeshBuffer texcoords, MeshBuffer[] additionalBuffers, int renderMode, boolean useEBO) {
        this.elements = elements;
        this.useEBO = useEBO;
        this.verticesLength = vertices.data().length / vertices.dimensions();

        RENDER_MODE = renderMode;

        VAOID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAOID);

        if(useEBO) {
            EBOID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBOID);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elements, GL15.GL_STATIC_DRAW);
        }

        VBOID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.data(), GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, vertices.dimensions(), GL11.GL_FLOAT, false, 0, 0);

        if(texcoords != null) {
            TBOID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, TBOID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texcoords.data(), GL15.GL_STATIC_DRAW);

            GL20.glVertexAttribPointer(1, texcoords.dimensions(), GL11.GL_FLOAT, false, 0, 0);
        }
        if(additionalBuffers != null) {
            for(int i = 0; i < additionalBuffers.length; i++) {
                additionalIds = new int[additionalBuffers.length];
                additionalIds[i] = GL15.glGenBuffers();

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, additionalIds[i]);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, additionalBuffers[i].data(), GL15.GL_STATIC_DRAW);

                GL20.glVertexAttribPointer(i + 2, additionalBuffers[i].dimensions(), GL11.GL_FLOAT, false, 0, 0);
            }
        }

        GL30.glBindVertexArray(0);
    }

    public void load() {
        GL30.glBindVertexArray(VAOID);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        if(additionalIds == null) return;
        for(int i = 0; i < additionalIds.length; i++) GL20.glEnableVertexAttribArray(i + 2);
    }
    public void render(Texture texture) {
        if(texture != null) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        }
        if(useEBO) GL11.glDrawElements(RENDER_MODE, elements.length, GL11.GL_UNSIGNED_INT, 0);
        else GL11.glDrawArrays(RENDER_MODE, 0, verticesLength);
    }
    public void render(int texture) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        if(useEBO) GL11.glDrawElements(RENDER_MODE, elements.length, GL11.GL_UNSIGNED_INT, 0);
        else GL11.glDrawArrays(RENDER_MODE, 0, verticesLength);
    }
    public void render(Texture[] textures) {
        for(int i = 0; i < textures.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i].getId());
        }

        if(useEBO) GL11.glDrawElements(RENDER_MODE, elements.length, GL11.GL_UNSIGNED_INT, 0);
        else GL11.glDrawArrays(RENDER_MODE, 0, verticesLength);
    }
    public void render(int[] textures) {
        for(int i = 0; i < textures.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);
        }

        if(useEBO) GL11.glDrawElements(RENDER_MODE, elements.length, GL11.GL_UNSIGNED_INT, 0);
        else GL11.glDrawArrays(RENDER_MODE, 0, verticesLength);
    }
    public void unload() {
        if(additionalIds == null) return;
        for(int i = additionalIds.length - 1; i >= 0; i--) GL20.glEnableVertexAttribArray(i + 2);

        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
    }
    public void clear() {
        unload();

        if(additionalIds == null) return;
        for(int i = additionalIds.length - 1; i >= 0; i--) GL15.glDeleteBuffers(additionalIds[i]);

        GL15.glDeleteBuffers(TBOID);
        GL15.glDeleteBuffers(VBOID);
        GL15.glDeleteBuffers(EBOID);
        GL30.glDeleteVertexArrays(VAOID);
    }
}