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

    public static Texture generate(Identifier identifier) throws IOException {
        BLOCKS_ATLAS_OFFSETS.clear();

        MyNewCraft.LOGGER.debug("Generating " + identifier.getId() + " atlas...");

        int atlasWidth = 0;
        int atlasHeight = 0;
        int pointer = 0;

        File[] textures = Objects.requireNonNull(new File("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + identifier.getNamespace() + "/textures/" + identifier.getId()).listFiles());
        List<BufferedImage> blockImages = new ArrayList<>();

        for(File file : textures) {
            if(file.isFile()) {
                Identifier blockIdentifier = new Identifier(identifier.getNamespace(), file.getName().replace(".png", ""));

                MyNewCraft.LOGGER.debug(blockIdentifier);

                BufferedImage image = ImageIO.read(new File("resourcepacks/" + MyNewCraft.RESOURCE_PACK + '/' + blockIdentifier.getNamespace() + "/textures/block/" + blockIdentifier.getId() + ".png"));
                blockImages.add(image);

                BLOCKS_ATLAS_OFFSETS.put(blockIdentifier, (double) pointer / textures.length);

                atlasWidth += image.getHeight();
                pointer++;

                if(image.getHeight() > atlasHeight) atlasHeight = image.getHeight();
            }
        }

        BufferedImage atlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = atlas.getGraphics();

        for(int i = 0; i < blockImages.size(); i++)
            graphics.drawImage(blockImages.get(i), i * atlasHeight, 0, null);

        graphics.dispose();

        MyNewCraft.LOGGER.debug(identifier.getId() + " atlas was generated successfully!");

        return new Texture(atlas, Texture.NEAREST);
    }
}