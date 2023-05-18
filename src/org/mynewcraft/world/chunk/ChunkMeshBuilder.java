package org.mynewcraft.world.chunk;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.engine.graphics.mesh.Mesh;
import org.mynewcraft.engine.graphics.mesh.MeshBuffer;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;
import org.mynewcraft.world.block.AbstractBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkMeshBuilder {
    public static Mesh build(Chunk chunk) {
        List<Vector3d> vertexList = new ArrayList<>();
        List<Vector2d> texcoordList = new ArrayList<>();
        List<Vector3d> normalList = new ArrayList<>();

        Map<Vector3i, AbstractBlock> blocks = chunk.getMap();

        for(Vector3i coordinate : chunk.getCoordinates()) {
            int x = coordinate.x();
            int y = coordinate.y();
            int z = coordinate.z();

            AbstractBlock block = blocks.get(coordinate);

            if(!blocks.containsKey(new Vector3i(x, y, z + 1))) {
                vertexList.add(new Vector3d(x, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x, y, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y, z + 1.0));

                double tx = block.getTexcoord()[0];

                if(!block.getNaturalTexture()[0] || !new Random(chunk.getSeed() + x * 293052L + y * 392050L + (z + 1) * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                } else {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                }

                normalList.add(new Vector3d(0.0, 0.0, 1.0));
                normalList.add(new Vector3d(0.0, 0.0, 1.0));
                normalList.add(new Vector3d(0.0, 0.0, 1.0));
                normalList.add(new Vector3d(0.0, 0.0, 1.0));
                normalList.add(new Vector3d(0.0, 0.0, 1.0));
                normalList.add(new Vector3d(0.0, 0.0, 1.0));
            }
            if(!blocks.containsKey(new Vector3i(x, y, z - 1))) {
                vertexList.add(new Vector3d(x, y + 1.0, z));
                vertexList.add(new Vector3d(x + 1.0, y, z));
                vertexList.add(new Vector3d(x, y, z));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z));
                vertexList.add(new Vector3d(x + 1.0, y, z));
                vertexList.add(new Vector3d(x, y + 1.0, z));

                double tx = block.getTexcoord()[1];

                if(!block.getNaturalTexture()[1] || !new Random(chunk.getSeed() + x * 293052L + y * 392050L + (z - 1) * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                } else {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                }

                normalList.add(new Vector3d(0.0, 0.0, -1.0));
                normalList.add(new Vector3d(0.0, 0.0, -1.0));
                normalList.add(new Vector3d(0.0, 0.0, -1.0));
                normalList.add(new Vector3d(0.0, 0.0, -1.0));
                normalList.add(new Vector3d(0.0, 0.0, -1.0));
                normalList.add(new Vector3d(0.0, 0.0, -1.0));
            }
            if(!blocks.containsKey(new Vector3i(x, y + 1, z))) {
                vertexList.add(new Vector3d(x, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z));
                vertexList.add(new Vector3d(x, y + 1.0, z));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z));
                vertexList.add(new Vector3d(x, y + 1.0, z + 1.0));

                double tx = block.getTexcoord()[2];

                if(!block.getNaturalTexture()[2] || !new Random(chunk.getSeed() + x * 293052L + (y + 1) * 392050L + z * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                } else {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                }

                normalList.add(new Vector3d(0.0, 1.0, 0.0));
                normalList.add(new Vector3d(0.0, 1.0, 0.0));
                normalList.add(new Vector3d(0.0, 1.0, 0.0));
                normalList.add(new Vector3d(0.0, 1.0, 0.0));
                normalList.add(new Vector3d(0.0, 1.0, 0.0));
                normalList.add(new Vector3d(0.0, 1.0, 0.0));
            }
            if(!blocks.containsKey(new Vector3i(x, y - 1, z))) {
                vertexList.add(new Vector3d(x, y, z + 1.0));
                vertexList.add(new Vector3d(x, y, z));
                vertexList.add(new Vector3d(x + 1.0, y, z));
                vertexList.add(new Vector3d(x + 1.0, y, z + 1.0));
                vertexList.add(new Vector3d(x, y, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y, z));

                double tx = block.getTexcoord()[3];

                if(!block.getNaturalTexture()[3] || !new Random(chunk.getSeed() + x * 293052L + (y - 1) * 392050L + z * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                } else {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                }

                normalList.add(new Vector3d(0.0, -1.0, 0.0));
                normalList.add(new Vector3d(0.0, -1.0, 0.0));
                normalList.add(new Vector3d(0.0, -1.0, 0.0));
                normalList.add(new Vector3d(0.0, -1.0, 0.0));
                normalList.add(new Vector3d(0.0, -1.0, 0.0));
                normalList.add(new Vector3d(0.0, -1.0, 0.0));
            }
            if(!blocks.containsKey(new Vector3i(x + 1, y, z))) {
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z));
                vertexList.add(new Vector3d(x + 1.0, y, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y, z));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y, z + 1.0));
                vertexList.add(new Vector3d(x + 1.0, y + 1.0, z));

                double tx = block.getTexcoord()[4];

                if(!block.getNaturalTexture()[4] || !new Random(chunk.getSeed() + (x + 1) * 293052L + y * 392050L + z * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                } else {
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                }

                normalList.add(new Vector3d(1.0, 0.0, 0.0));
                normalList.add(new Vector3d(1.0, 0.0, 0.0));
                normalList.add(new Vector3d(1.0, 0.0, 0.0));
                normalList.add(new Vector3d(1.0, 0.0, 0.0));
                normalList.add(new Vector3d(1.0, 0.0, 0.0));
                normalList.add(new Vector3d(1.0, 0.0, 0.0));
            }
            if(!blocks.containsKey(new Vector3i(x - 1, y, z))) {
                vertexList.add(new Vector3d(x, y, z));
                vertexList.add(new Vector3d(x, y, z + 1.0));
                vertexList.add(new Vector3d(x, y + 1.0, z));
                vertexList.add(new Vector3d(x, y + 1.0, z + 1.0));
                vertexList.add(new Vector3d(x, y + 1.0, z));
                vertexList.add(new Vector3d(x, y, z + 1.0));

                double tx = block.getTexcoord()[5];

                if(!block.getNaturalTexture()[5] || !new Random(chunk.getSeed() + (x - 1) * 293052L + y * 392050L + z * 505940L).nextBoolean()) {
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                } else {
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx, 1.0));
                    texcoordList.add(new Vector2d(tx, 0.0));
                    texcoordList.add(new Vector2d(tx + 1.0 / AtlasGenerator.BLOCKS_ATLAS_OFFSETS.size(), 1.0));
                }

                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
                normalList.add(new Vector3d(-1.0, 0.0, 0.0));
            }
        }

        float[] vertices = new float[vertexList.size() * 3];
        for(int i = 0; i < vertexList.size(); i++) {
            vertices[i * 3] = (float) vertexList.get(i).x();
            vertices[i * 3 + 1] = (float) vertexList.get(i).y();
            vertices[i * 3 + 2] = (float) vertexList.get(i).z();
        }
        float[] texcoords = new float[texcoordList.size() * 2];
        for(int i = 0; i < texcoordList.size(); i++) {
            texcoords[i * 2] = (float) texcoordList.get(i).x();
            texcoords[i * 2 + 1] = (float) texcoordList.get(i).y();
        }
        float[] normals = new float[normalList.size() * 3];
        for(int i = 0; i < normalList.size(); i++) {
            normals[i * 3] = (float) normalList.get(i).x();
            normals[i * 3 + 1] = (float) normalList.get(i).y();
            normals[i * 3 + 2] = (float) normalList.get(i).z();
        }

        return new Mesh(null, vertices, texcoords, new MeshBuffer[] { new MeshBuffer(normals, 3) }, Mesh.TRIANGLES, false);
    }
}