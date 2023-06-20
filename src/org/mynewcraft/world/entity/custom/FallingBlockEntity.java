package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.client.graphics.shader.WorldShader;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.graphics.mesh.MeshBuffer;
import org.mynewcraft.engine.io.texture.Texture;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.BlockCollider;
import org.mynewcraft.world.block.custom.Block;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.entity.AbstractEntity;

public class FallingBlockEntity extends AbstractEntity {
    private final double mass;

    private final AbstractBlock block;
    public final Vector2d direction;

    private final Mesh mesh;

    public FallingBlockEntity(CubeCollider collider, AbstractBlock block, double mass) {
        super(new CubeCollider(new Vector3d(collider.position.x() + 0.25, collider.position.y() + 0.25, collider.position.z() + 0.25), new Vector3d(collider.scale.x() - 0.5, collider.scale.y() - 0.5, collider.scale.z() - 0.5)), new Vector3d());
        this.mass = mass;

        this.block = block;
        direction = new Vector2d();

        float alpha = block instanceof Block ? (float) ((Block) block).getTransparency() : 0.0f;

        mesh = new Mesh(new int[] {
                0, 1, 3,
                2, 3, 1,
                6, 5, 4,
                6, 4, 7,
                8, 9, 11,
                10, 11, 9,
                14, 13, 12,
                14, 12, 15,
                18, 17, 16,
                18, 16, 19,
                20, 21, 23,
                22, 23, 21
        }, new MeshBuffer(new float[] {
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,

                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f
        }, 3), new MeshBuffer(new float[] {
                (float) block.getTexcoord()[0], 1.0f,
                (float) block.getTexcoord()[0] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[0] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[0], 0.0f,

                (float) block.getTexcoord()[1], 1.0f,
                (float) block.getTexcoord()[1] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[1] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[1], 0.0f,

                (float) block.getTexcoord()[2], 1.0f,
                (float) block.getTexcoord()[2] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[2] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[2], 0.0f,

                (float) block.getTexcoord()[3], 1.0f,
                (float) block.getTexcoord()[3] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[3] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[3], 0.0f,

                (float) block.getTexcoord()[4], 1.0f,
                (float) block.getTexcoord()[4] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[4] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[4], 0.0f,

                (float) block.getTexcoord()[5], 1.0f,
                (float) block.getTexcoord()[5] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0f,
                (float) block.getTexcoord()[5] + 1.0f / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0f,
                (float) block.getTexcoord()[5], 0.0f
        }, 2), new MeshBuffer[] {
                new MeshBuffer(new float[] {
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f
                }, 3),
                new MeshBuffer(new float[] {
                        alpha, alpha, alpha, alpha,
                        alpha, alpha, alpha, alpha,
                        alpha, alpha, alpha, alpha,
                        alpha, alpha, alpha, alpha,
                        alpha, alpha, alpha, alpha,
                        alpha, alpha, alpha, alpha
                }, 1),
                new MeshBuffer(new float[] {
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                }, 1),
                new MeshBuffer(new float[] {
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f
                }, 3)
        }, Mesh.TRIANGLES, true);
    }

    public void update(World world, Time time) {
        Vector3i finalBlockPos = null;

        direction.y -= world.gravity * mass * time.getDelta();
        collider.position.y += direction.y() * time.getDelta();

        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
            for(BlockCollider block : chunk.getInteractiveBlocks()) {
                Vector3d blockPos = new Vector3d(block.position).add(chunk.getOffset().x() * World.chunkScale, 0.0, chunk.getOffset().y() * World.chunkScale);
                if(!block.containsTag(LivingEntity.PASSABLE_TAG) && !block.containsTag(LivingEntity.WATER_LIKE_TAG) && new CubeCollider(blockPos, block.scale).checkCollision(collider)) {
                    collider.position.y -= direction.y() * time.getDelta();

                    finalBlockPos = new Vector3i((int) Math.floor(blockPos.x()), (int) Math.floor(blockPos.y() + block.scale.y()), (int) Math.floor(blockPos.z()));
                    direction.y = 0.0;

                    break;
                }
            }
        }

        if(finalBlockPos != null) {
            world.killEntity(this);
            world.placeBlock(finalBlockPos, block);
        }
        if(collider.position.y() <= 0.0) world.killEntity(this);
    }
    public void render(WorldShader worldShader, Texture[] blockAtlas) {
        worldShader.transform(new Vector3d(collider.position.x() - 0.25, collider.position.y() - 0.25, collider.position.z() - 0.25), new Vector3d(), new Vector3d(collider.scale.x() + 0.5, collider.scale.y() + 0.5, collider.scale.z() + 0.5));

        mesh.load();
        mesh.render(blockAtlas);
        mesh.unload();
    }
}
