package org.mynewcraft.world.block;

import org.mynewcraft.MyNewCraft;
import org.mynewcraft.client.graphics.util.Identifier;
import org.mynewcraft.world.block.custom.Block;

public class Blocks {
    public static final AbstractBlock GRASS_BLOCK = new AbstractBlock(new Identifier(MyNewCraft.GAME_ID, "grass_block"));
    public static final AbstractBlock DIRT = new AbstractBlock(new Identifier(MyNewCraft.GAME_ID, "dirt"));
    public static final AbstractBlock STONE = new AbstractBlock(new Identifier(MyNewCraft.GAME_ID, "stone"));
    public static final AbstractBlock COBBLESTONE = new AbstractBlock(new Identifier(MyNewCraft.GAME_ID, "cobblestone"));
    public static final Block BEDROCK = new Block(new Identifier(MyNewCraft.GAME_ID, "bedrock"), new Block.Settings().unbreakable());

    public static void register() {
        MyNewCraft.LOGGER.debug("Registering blocks!");
    }
}