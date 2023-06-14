package org.mynewcraft.world.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.mynewcraft.MyNewCraft;
import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AbstractBlock {
    protected final double[] TEXCOORD = new double[6];
    protected final boolean[] NATURAL_TEXTURE = new boolean[6];
    protected final Identifier IDENTIFIER;
    protected final JsonObject JSON;

    private static final ArrayList<AbstractBlock> blocks = new ArrayList<>();

    private final int index;

    public AbstractBlock(Identifier identifier) {
        IDENTIFIER = identifier;
        index = blocks.size();

        JsonObject json = null;
        try {
            json = new JsonParser().parse(new BufferedReader(new FileReader("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + identifier.getNamespace() + "/models/block/" + identifier.getId() + ".json"))).getAsJsonObject();
        } catch(Exception exception) {
            exception.printStackTrace();
        }

        JSON = json;

        if(json != null) {
            if(json.get("parent") != null) {
                if(Objects.equals(json.get("parent").getAsString(), "mynewcraft:block/cube_all")) {
                    JsonObject texcoords = json.get("textures").getAsJsonObject();

                    if(texcoords != null) {
                        if(texcoords.get("all") != null) {
                            String textureLocation = texcoords.get("all").getAsJsonObject().get("id").getAsString();
                            String[] textureBeforeAfterNamespace = textureLocation.split(":");

                            double texcoord = 0.0;
                            for(Identifier atlasIdentifier : AtlasGenerator.BLOCKS_ATLAS_OFFSETS.keySet()) {
                                if(atlasIdentifier.equals(new Identifier(textureBeforeAfterNamespace[0], textureBeforeAfterNamespace[1].split("/")[1]))) {
                                    texcoord = AtlasGenerator.BLOCKS_ATLAS_OFFSETS.get(atlasIdentifier);
                                    break;
                                }
                            }
                            Arrays.fill(TEXCOORD, texcoord);

                            for(int i = 0; i < NATURAL_TEXTURE.length; i++) {
                                JsonElement naturalTexture = texcoords.get("all").getAsJsonObject().get("natural");
                                NATURAL_TEXTURE[i] = naturalTexture != null && naturalTexture.getAsBoolean();
                            }
                        }
                        if(texcoords.get("front") != null) loadFace(texcoords, "front", 0);
                        if(texcoords.get("back") != null) loadFace(texcoords, "back", 1);
                        if(texcoords.get("top") != null) loadFace(texcoords, "top", 2);
                        if(texcoords.get("bottom") != null) loadFace(texcoords, "bottom", 3);
                        if(texcoords.get("right") != null) loadFace(texcoords, "right", 4);
                        if(texcoords.get("left") != null) loadFace(texcoords, "left", 5);
                    }
                }
            }
        }

        blocks.add(this);
    }

    public static AbstractBlock getByIndex(int block) {
        return blocks.get(block);
    }

    private void loadFace(JsonObject texcoords, String faceId, int id) {
        String textureLocation = texcoords.get(faceId).getAsJsonObject().get("id").getAsString();
        String[] textureBeforeAfterNamespace = textureLocation.split(":");

        double texcoord = 0.0;
        for(Identifier atlasIdentifier : AtlasGenerator.BLOCKS_ATLAS_OFFSETS.keySet()) {
            if(atlasIdentifier.equals(new Identifier(textureBeforeAfterNamespace[0], textureBeforeAfterNamespace[1].split("/")[1]))) {
                texcoord = AtlasGenerator.BLOCKS_ATLAS_OFFSETS.get(atlasIdentifier);
                break;
            }
        }

        TEXCOORD[id] = texcoord;

        JsonElement naturalTexture = texcoords.get(faceId).getAsJsonObject().get("natural");
        NATURAL_TEXTURE[id] = naturalTexture != null && naturalTexture.getAsBoolean();
    }

    public double[] getTexcoord() {
        return TEXCOORD;
    }

    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public boolean[] getNaturalTexture() {
        return NATURAL_TEXTURE;
    }

    public int getIndex() {
        return index;
    }
}