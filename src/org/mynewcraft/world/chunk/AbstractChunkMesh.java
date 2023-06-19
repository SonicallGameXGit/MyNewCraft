package org.mynewcraft.world.chunk;

import org.joml.Vector2i;

public record AbstractChunkMesh(Vector2i offset, float[] vertices, float[] texcoords, float[] normals, float[] alphas, float[] aoLevels, float[] tangents) { }