package org.mynewcraft.engine.graphics.mesh;

public class Shapes {
    public static final Mesh SQUARE = new Mesh(
            new int[] { 0, 1, 2, 3 },
            new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f },
            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
            null,
            Mesh.TRIANGLE_FAN,
            true
    );
//    public static final Mesh SQUARE_FRONT = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    0.0f, 0.0f, 1.0f,
//                    1.0f, 0.0f, 1.0f,
//                    1.0f, 1.0f, 1.0f,
//                    0.0f, 1.0f, 1.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f }, 3) }
//    );
//    public static final Mesh SQUARE_BACK = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    0.0f, 0.0f, 0.0f,
//                    1.0f, 0.0f, 0.0f,
//                    1.0f, 1.0f, 0.0f,
//                    0.0f, 1.0f, 0.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f }, 3) }
//    );
//    public static final Mesh SQUARE_TOP = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    0.0f, 1.0f, 0.0f,
//                    1.0f, 1.0f, 0.0f,
//                    1.0f, 1.0f, 1.0f,
//                    0.0f, 1.0f, 1.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f }, 3) }
//    );
//    public static final Mesh SQUARE_BOTTOM = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    0.0f, 0.0f, 0.0f,
//                    1.0f, 0.0f, 0.0f,
//                    1.0f, 0.0f, 1.0f,
//                    0.0f, 0.0f, 1.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f }, 3) }
//    );
//    public static final Mesh SQUARE_LEFT = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    0.0f, 0.0f, 0.0f,
//                    0.0f, 1.0f, 0.0f,
//                    0.0f, 1.0f, 1.0f,
//                    0.0f, 0.0f, 1.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f }, 3) }
//    );
//    public static final Mesh SQUARE_RIGHT = new Mesh(
//            new int[] { 0, 1, 3, 2, 3, 1 },
//            new float[] {
//                    1.0f, 0.0f, 0.0f,
//                    1.0f, 1.0f, 0.0f,
//                    1.0f, 1.0f, 1.0f,
//                    1.0f, 0.0f, 1.0f
//            },
//            new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f },
//            new MeshBuffer[] { new MeshBuffer(new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f }, 3) }
//    );
    public static final Mesh CUBE = new Mesh(
            new int[] {
                    0, 1, 3,
                    2, 3, 1,
                    6, 5, 4,
                    6, 4, 7,
                    8, 9, 11,
                    10, 11, 9,
                    14, 13, 12,
                    14, 12, 15,
                    16, 17, 19,
                    18, 19, 17,

                    22, 21, 20,
                    22, 20, 23
            },
            new float[] {
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

                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 0.0f,

                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f
            },
            new float[] {
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f
            },
            new MeshBuffer[] {
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

                            -1.0f, 0.0f, 0.0f,
                            -1.0f, 0.0f, 0.0f,
                            -1.0f, 0.0f, 0.0f,
                            -1.0f, 0.0f, 0.0f,

                            1.0f, 0.0f, 0.0f,
                            1.0f, 0.0f, 0.0f,
                            1.0f, 0.0f, 0.0f,
                            1.0f, 0.0f, 0.0f
                    }, 3)
            },
        Mesh.TRIANGLES,
        true
    );

    public static void load() {
        System.out.println("Registering default shapes");
    }
    public static void clear() {
        SQUARE.clear();
        CUBE.clear();
    }
}