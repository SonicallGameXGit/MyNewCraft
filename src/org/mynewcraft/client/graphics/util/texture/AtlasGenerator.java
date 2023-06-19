package org.mynewcraft.client.graphics.util.texture;

import org.mynewcraft.MyNewCraft;
import org.mynewcraft.engine.io.texture.Texture;
import org.mynewcraft.client.graphics.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AtlasGenerator {
    public static final Map<Identifier, Double> BLOCKS_ATLAS_OFFSETS = new HashMap<>();

    public static Texture[] generate(Identifier identifier) throws IOException {
        BLOCKS_ATLAS_OFFSETS.clear();

        MyNewCraft.LOGGER.debug("Generating " + identifier.getId() + " atlas...");

        int atlasHeight = 0;
        int normalAtlasHeight = 0;
        int pointer = 0;

        List<File> textures = new ArrayList<>(List.of(Objects.requireNonNull(new File("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + identifier.getNamespace() + "/textures/" + identifier.getId()).listFiles())));
        List<File> allTextures = new ArrayList<>(textures);
        List<BufferedImage> blockImages = new ArrayList<>();
        List<Identifier> blockIdentifiers = new ArrayList<>();


        List<Integer> texturesToRemove = new ArrayList<>();
        for(int i = 0; i < allTextures.size(); i++)
            if(textures.get(i).getName().endsWith("_n.png"))
                texturesToRemove.add(i);
        for(int i = 0; i < texturesToRemove.size(); i++)
            textures.remove(texturesToRemove.get(i) - i);

        texturesToRemove.clear();

        for(File file : allTextures) {
            if(file.isFile() && !file.getName().endsWith("_n.png")) {
                Identifier blockIdentifier = new Identifier(identifier.getNamespace(), file.getName().replace(".png", ""));
                blockIdentifiers.add(blockIdentifier);

                BufferedImage image = ImageIO.read(new File("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + blockIdentifier.getNamespace() + "/textures/" + identifier.getId() + '/' + blockIdentifier.getId() + ".png"));
                blockImages.add(image);

                BLOCKS_ATLAS_OFFSETS.put(blockIdentifier, (double) pointer / textures.size());
                pointer++;

                if(image.getHeight() > atlasHeight) atlasHeight = image.getHeight();
                if(image.getHeight() > normalAtlasHeight) normalAtlasHeight = image.getHeight();
            }
            if(file.isFile() && file.getName().endsWith("_n.png")) {
                int height = ImageIO.read(file).getHeight();
                if(height > normalAtlasHeight) normalAtlasHeight = height;
            }
        }

        BufferedImage atlas = new BufferedImage(atlasHeight * blockImages.size(), atlasHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage normalAtlas = new BufferedImage(blockImages.size() * normalAtlasHeight, normalAtlasHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = atlas.getGraphics();
        for(int i = 0; i < blockImages.size(); i++)
            graphics.drawImage(blockImages.get(i).getScaledInstance(atlasHeight, atlasHeight, Image.SCALE_FAST), i * atlasHeight, 0, null);

        graphics.dispose();

        Graphics normalGraphics = normalAtlas.getGraphics();
        Texture colorTexture = new Texture(atlas, Texture.NEAREST);

        normalGraphics.setColor(new Color(128, 120, 255));
        normalGraphics.fillRect(0, 0, normalAtlasHeight * blockImages.size(), normalAtlasHeight);
        for(int i = 0; i < blockImages.size(); i++) {
            File normalTexture = new File("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + identifier.getNamespace() + "/textures/" + identifier.getId() + '/' + blockIdentifiers.get(i).getId() + "_n.png");
            if(normalTexture.exists())
                normalGraphics.drawImage(ImageIO.read(normalTexture).getScaledInstance(normalAtlasHeight, normalAtlasHeight, Image.SCALE_FAST), i * normalAtlasHeight, 0, null);
        }

        normalGraphics.dispose();

        Texture normalTexture = new Texture(normalAtlas, Texture.NEAREST);

        blockImages.clear();
        textures.clear();
        allTextures.clear();
        blockIdentifiers.clear();

        MyNewCraft.LOGGER.debug(identifier.getId() + " atlas was generated successfully!");

        return new Texture[] { colorTexture, normalTexture };
    }
}