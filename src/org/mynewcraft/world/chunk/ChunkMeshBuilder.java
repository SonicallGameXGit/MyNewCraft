package org.mynewcraft.world.chunk;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.client.graphics.util.texture.AtlasGenerator;
import org.mynewcraft.world.block.AbstractBlock;
import org.mynewcraft.world.block.BlockCollider;
import org.mynewcraft.world.block.custom.Block;
import org.mynewcraft.world.entity.custom.LivingEntity;
import org.mynewcraft.world.entity.custom.PlayerEntity;

import java.util.*;

public class ChunkMeshBuilder {
    private static final int FACE_FRONT = 0;
    private static final int FACE_BACK = 1;
    private static final int FACE_TOP = 2;
    private static final int FACE_BOTTOM = 3;
    private static final int FACE_RIGHT = 4;
    private static final int FACE_LEFT = 5;

    public static AbstractChunkMesh[] build(Chunk chunk) {
        ArrayList<Vector3d> vertexList = new ArrayList<>();
        ArrayList<Vector2d> texcoordList = new ArrayList<>();
        ArrayList<Vector3d> normalList = new ArrayList<>();
        ArrayList<Float> alphaList = new ArrayList<>();
        ArrayList<Float> aoLevelList = new ArrayList<>();
        ArrayList<Vector3d> tangentList = new ArrayList<>();

        ArrayList<Vector3d> vertexListA = new ArrayList<>();
        ArrayList<Vector2d> texcoordListA = new ArrayList<>();
        ArrayList<Vector3d> normalListA = new ArrayList<>();
        ArrayList<Float> alphaListA = new ArrayList<>();
        ArrayList<Float> aoLevelListA = new ArrayList<>();
        ArrayList<Vector3d> tangentListA = new ArrayList<>();

        HashMap<Vector3i, Integer> blocks = chunk.getMap();
        HashMap<Vector3i, Integer> abstractOutline = chunk.getAbstractOutline();

        chunk.interactiveBlocks.clear();

        for(Vector3i coordinate : chunk.getCoordinates()) {
            if(coordinate != null) {
                int x = coordinate.x();
                int y = coordinate.y();
                int z = coordinate.z();

                Integer blockId = blocks.get(coordinate);
                if(blockId != null) {
                    AbstractBlock block = AbstractBlock.getByIndex(blockId);

                    boolean faceCreated;
                    if(block instanceof Block && (((Block) block).getTransparency() > 0.0 || ((Block) block).getInnerView()))
                        faceCreated = buildFace(chunk, blocks, abstractOutline, vertexListA, texcoordListA, normalListA, alphaListA, aoLevelListA, tangentListA, x, y, z, block);
                    else faceCreated = buildFace(chunk, blocks, abstractOutline, vertexList, texcoordList, normalList, alphaList, aoLevelList, tangentList, x, y, z, block);

                    if(faceCreated) {
                        if(!(block instanceof Block))
                            chunk.interactiveBlocks.add(new BlockCollider(new Vector3d(x, y, z), new Vector3d(1.0)));
                        else {
                            BlockCollider collider = new BlockCollider(new Vector3d(x, y, z), new Vector3d(1.0));
                            if(((Block) block).getPassable())
                                collider.addTag(LivingEntity.PASSABLE_TAG);
                            if(((Block) block).getWaterLike())
                                collider.addTag(LivingEntity.WATER_LIKE_TAG);
                            if(((Block) block).getRaycastIgnore())
                                collider.addTag(PlayerEntity.IGNORE_RAYCAST_TAG);

                            chunk.interactiveBlocks.add(collider);
                        }
                    } if(block instanceof Block blockB) {
                        if(blockB.getWaterLike()) {
                            BlockCollider collider = new BlockCollider(new Vector3d(x, y, z), new Vector3d(1.0)).addTag(LivingEntity.WATER_LIKE_TAG);
                            if(blockB.getRaycastIgnore())
                                collider.addTag(PlayerEntity.IGNORE_RAYCAST_TAG);
                            if(blockB.getPassable())
                                collider.addTag(LivingEntity.PASSABLE_TAG);

                            chunk.interactiveBlocks.add(collider);
                        }
                        if(!blockB.getStatic())
                            chunk.notStaticBlocks.put(new Vector3i(x, y, z), block.getIndex());
                    }
                }
            }
        }

        float[] vertices = getArray3d(vertexList);
        float[] verticesA = getArray3d(vertexListA);
        float[] texcoords = getArray2d(texcoordList);
        float[] texcoordsA = getArray2d(texcoordListA);
        float[] normals = getArray3d(normalList);
        float[] normalsA = getArray3d(normalListA);
        float[] alphas = getArray(alphaList);
        float[] alphasA = getArray(alphaListA);
        float[] aoLevels = getArray(aoLevelList);
        float[] aoLevelsA = new float[aoLevels.length];
        float[] tangents = getArray3d(tangentList);
        float[] tangentsA = getArray3d(tangentListA);

        vertexList.clear();
        vertexListA.clear();
        texcoordList.clear();
        texcoordListA.clear();
        normalList.clear();
        normalListA.clear();
        alphaList.clear();
        alphaListA.clear();
        aoLevelList.clear();
        aoLevelListA.clear();
        tangentList.clear();
        tangentListA.clear();

        chunk.meshGenerated = true;

        return new AbstractChunkMesh[] {
                new AbstractChunkMesh(chunk.getOffset(), vertices, texcoords, normals, alphas, aoLevels, tangents),
                new AbstractChunkMesh(chunk.getOffset(), verticesA, texcoordsA, normalsA, alphasA, aoLevelsA, tangentsA)
        };
    }

    private static float[] getArray(ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for(int i = 0; i < array.length; i++) array[i] = list.get(i);

        return array;
    }
    private static float[] getArray2d(ArrayList<Vector2d> list) {
        float[] array = new float[list.size() * 2];
        for(int i = 0; i < list.size(); i++) {
            array[i * 2] = (float) list.get(i).x();
            array[i * 2 + 1] = (float) list.get(i).y();
        }

        return array;
    }
    private static float[] getArray3d(ArrayList<Vector3d> list) {
        float[] array = new float[list.size() * 3];
        for(int i = 0; i < list.size(); i++) {
            array[i * 3] = (float) list.get(i).x();
            array[i * 3 + 1] = (float) list.get(i).y();
            array[i * 3 + 2] = (float) list.get(i).z();
        }

        return array;
    }

    private static boolean buildFace(Chunk chunk, HashMap<Vector3i, Integer> blocks, HashMap<Vector3i, Integer> abstractOutline, List<Vector3d> vertexList, List<Vector2d> texcoordList, List<Vector3d> normalList, List<Float> alphaList, List<Float> aoLevelList, List<Vector3d> tangentList, int x, int y, int z, AbstractBlock block) {
        boolean faceCreated = false;

        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x, y, z + 1))) {
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

            buildTransparency(alphaList, block);

            getZAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), true);
            getTangents(tangentList, FACE_FRONT);

            faceCreated = true;
        }
        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x, y, z - 1))) {
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

            buildTransparency(alphaList, block);

            getZAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), false);
            getTangents(tangentList, FACE_BACK);

            faceCreated = true;
        }
        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x, y + 1, z))) {
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

            buildTransparency(alphaList, block);

            getYAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), true);
            getTangents(tangentList, FACE_TOP);

            faceCreated = true;
        }
        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x, y - 1, z))) {
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

            buildTransparency(alphaList, block);

            getYAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), false);
            getTangents(tangentList, FACE_BOTTOM);

            faceCreated = true;
        }
        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x + 1, y, z))) {
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

            buildTransparency(alphaList, block);

            getXAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), true);
            getTangents(tangentList, FACE_RIGHT);

            faceCreated = true;
        }
        if(checkNeighbourBlock(blocks, abstractOutline, new Vector3i(x, y, z), new Vector3i(x - 1, y, z))) {
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

            buildTransparency(alphaList, block);

            getXAoLevel(aoLevelList, chunk, new Vector3i(x, y, z), false);
            getTangents(tangentList, FACE_LEFT);

            faceCreated = true;
        }

        return faceCreated;
    }

    private static void getTangent(List<Vector3d> tangentList, int face) {
        Vector3d tangent = new Vector3d();
        if(face == FACE_FRONT || face == FACE_TOP)
            tangent = new Vector3d(1.0, 0.0, 0.0);
        else if(face == FACE_BACK || face == FACE_BOTTOM)
            tangent = new Vector3d(-1.0, 0.0, 0.0);
        else if(face == FACE_RIGHT) {
            tangent = new Vector3d(0.0, 0.0, 1.0);
        }
        else if(face == FACE_LEFT)
            tangent = new Vector3d(0.0, 0.0, -1.0);
        tangentList.add(tangent);
        tangentList.add(tangent);
        tangentList.add(tangent);
    }
    private static void getTangents(List<Vector3d> tangentList, int face) {
        getTangent(tangentList, face);
        getTangent(tangentList, face);
    }
    private static void getXAoLevel(List<Float> aoLevelList, Chunk chunk, Vector3i blockPos, boolean front) {
        int x = blockPos.x();
        int y = blockPos.y();
        int z = blockPos.z();

        float[] levels = new float[4];

        float aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y - 1, z)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y - 1, z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y, z - 1)))
            aoLevel++;
        levels[0] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y - 1, z)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y - 1, z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y, z + 1)))
            aoLevel++;
        levels[1] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y + 1, z)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y + 1, z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y, z + 1)))
            aoLevel++;
        levels[2] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y + 1, z)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y + 1, z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + (front ? 1 : -1), y, z - 1)))
            aoLevel++;
        levels[3] = aoLevel;

        if(front) {
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[3]);
        } else {
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
        }
    }
    private static void getYAoLevel(List<Float> aoLevelList, Chunk chunk, Vector3i blockPos, boolean front) {
        int x = blockPos.x();
        int y = blockPos.y();
        int z = blockPos.z();

        float[] levels = new float[4];

        float aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x, y + (front ? 1 : -1), z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x - 1, y + (front ? 1 : -1), z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x - 1, y + (front ? 1 : -1), z)))
            aoLevel++;
        levels[0] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x, y + (front ? 1 : -1), z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + 1, y + (front ? 1 : -1), z - 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + 1, y + (front ? 1 : -1), z)))
            aoLevel++;
        levels[1] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x, y + (front ? 1 : -1), z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + 1, y + (front ? 1 : -1), z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x + 1, y + (front ? 1 : -1), z)))
            aoLevel++;
        levels[2] = aoLevel;

        aoLevel = 0.0f;
        if(chunk.containsBlock(new Vector3i(x, y + (front ? 1 : -1), z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x - 1, y + (front ? 1 : -1), z + 1)))
            aoLevel++;
        if(chunk.containsBlock(new Vector3i(x - 1, y + (front ? 1 : -1), z)))
            aoLevel++;
        levels[3] = aoLevel;

        if(front) {
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[3]);
        } else {
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
        }
    }
    private static void getZAoLevel(List<Float> aoLevelList, Chunk chunk, Vector3i blockPos, boolean front) {
        int x = blockPos.x();
        int y = blockPos.y();
        int z = blockPos.z();

        float[] levels = new float[4];

        float aoLevel = 0.0f;
        if(getAoBlockAt(chunk, new Vector3i(x, y - 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x - 1, y - 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x - 1, y, z + (front ? 1 : -1))))
            aoLevel++;
        levels[0] = aoLevel;

        aoLevel = 0.0f;
        if(getAoBlockAt(chunk, new Vector3i(x, y - 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x + 1, y - 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x + 1, y, z + (front ? 1 : -1))))
            aoLevel++;
        levels[1] = aoLevel;

        aoLevel = 0.0f;
        if(getAoBlockAt(chunk, new Vector3i(x, y + 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x + 1, y + 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x + 1, y, z + (front ? 1 : -1))))
            aoLevel++;
        levels[2] = aoLevel;

        aoLevel = 0.0f;
        if(getAoBlockAt(chunk, new Vector3i(x, y + 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x - 1, y + 1, z + (front ? 1 : -1))))
            aoLevel++;
        if(getAoBlockAt(chunk, new Vector3i(x - 1, y, z + (front ? 1 : -1))))
            aoLevel++;
        levels[3] = aoLevel;

        if(front) {
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
        } else {
            aoLevelList.add(levels[3]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[0]);
            aoLevelList.add(levels[2]);
            aoLevelList.add(levels[1]);
            aoLevelList.add(levels[3]);
        }
    }

    private static void buildTransparency(List<Float> alphaList, AbstractBlock block) {
        if(block instanceof Block settedBlock)
            for(int i = 0; i < 6; i++)
                alphaList.add((float) settedBlock.getTransparency());
        else for(int i = 0; i < 6; i++) alphaList.add(0.0f);
    }

    private static boolean checkNeighbourBlock(Map<Vector3i, Integer> blocks, Map<Vector3i, Integer> abstractOutline, Vector3i blockPos, Vector3i neighbourPos) {
        AbstractBlock block = AbstractBlock.getByIndex(blocks.get(blockPos));
        AbstractBlock neighbour = !blocks.containsKey(neighbourPos) ? (!abstractOutline.containsKey(neighbourPos) ? null : AbstractBlock.getByIndex(abstractOutline.get(neighbourPos))) : AbstractBlock.getByIndex(blocks.get(neighbourPos));

        if(neighbour != null) {
            if(neighbour instanceof Block neighbourB) {
                if(block instanceof Block blockB)
                    return (blockB.getTransparency() <= 0.0 && neighbourB.getTransparency() > 0.0) || neighbourB.getInnerView();
                else return neighbourB.getTransparency() > 0.0 || neighbourB.getInnerView();
            } else return false;
        } else return true;
    }
    private static boolean getAoBlockAt(Chunk chunk, Vector3i offset) {
        return chunk.containsBlock(offset) && (!(AbstractBlock.getByIndex(chunk.getBlockAt(offset)) instanceof Block) || ((Block) AbstractBlock.getByIndex(chunk.getBlockAt(offset))).getTransparency() <= 0.0);
    }
}