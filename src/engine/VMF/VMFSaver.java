package engine.VMF;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class VMFSaver {
    /**
     * Saves a VBMF model to a file
     * @param path the file to save - make sure to include the .vbmf0 at the end, as that isn't added automatically.
     * @param vertices the OpenGL vertices
     * @param textureCoordinates the OpenGL texture coordinates
     * @param indices the OpenGL indices
     * @param pathToTexture the path to the texture - see saveUVBMF for no texture
     * @param removableTriangles the removable triangles - A bit-field (8 bits, only 5 of which are used) which tells which triangles are not blocked by which sides.
     *                           bit order [1s, 2s, 4s, 8s, 16s], [top (+y), bottom(-y), z, -x, +x]
     *                           There is one byte for each triangle (every 3 indices) of a mesh. A triangle will only be removed when (~blockedFaces | ~removableTriangles[triangle index]) == 0
     * @param blockedFaces the faces (a bit field with the same bit order as a removableTriangle) that are blocked by this voxel [top (+y), bottom(-y), z, -x, +x]
     * @throws IOException if the texture doesn't exist.
     */
    public static void saveVBMF(String path, float[] vertices, float[] textureCoordinates, int[] indices, String pathToTexture, byte[] removableTriangles, byte blockedFaces) throws IOException {

        byte[] verticesBytes =           new byte[          vertices.length*4+4]; //create new array for bytes to be saved into file
        byte[] textureCoordinatesBytes = new byte[textureCoordinates.length*4+4];
        byte[] indicesBytes =            new byte[           indices.length*4+4];
        byte[] removableTrianglesBytes = new byte[removableTriangles.length+4];

        byte[] fourLengthBytes = getFourBytes(vertices.length/3); //save length into bytes
        verticesBytes[0] = fourLengthBytes[0];
        verticesBytes[1] = fourLengthBytes[1];
        verticesBytes[2] = fourLengthBytes[2];
        verticesBytes[3] = fourLengthBytes[3];


        for(int i=0; i<vertices.length; i++){
            byte[] fourBytes = getFourBytes(vertices[i]); //save data into bytes
            verticesBytes[i*4+4] = fourBytes[0];
            verticesBytes[i*4+5] = fourBytes[1];
            verticesBytes[i*4+6] = fourBytes[2];
            verticesBytes[i*4+7] = fourBytes[3];
        } //repeat for each data type

        fourLengthBytes = getFourBytes(textureCoordinates.length/2);
        textureCoordinatesBytes[0] = fourLengthBytes[0];
        textureCoordinatesBytes[1] = fourLengthBytes[1];
        textureCoordinatesBytes[2] = fourLengthBytes[2];
        textureCoordinatesBytes[3] = fourLengthBytes[3];
        for(int i=0; i<textureCoordinates.length; i++){
            byte[] fourBytes = getFourBytes(textureCoordinates[i]);
            textureCoordinatesBytes[i*4+4] = fourBytes[0];
            textureCoordinatesBytes[i*4+5] = fourBytes[1];
            textureCoordinatesBytes[i*4+6] = fourBytes[2];
            textureCoordinatesBytes[i*4+7] = fourBytes[3];
        }
        fourLengthBytes = getFourBytes(indices.length);
        indicesBytes[0] = fourLengthBytes[0];
        indicesBytes[1] = fourLengthBytes[1];
        indicesBytes[2] = fourLengthBytes[2];
        indicesBytes[3] = fourLengthBytes[3];
        for(int i=0; i<indices.length; i++){
            byte[] fourBytes = getFourBytes(indices[i]);
            indicesBytes[i*4+4] = fourBytes[0];
            indicesBytes[i*4+5] = fourBytes[1];
            indicesBytes[i*4+6] = fourBytes[2];
            indicesBytes[i*4+7] = fourBytes[3];
        }

        fourLengthBytes = getFourBytes(removableTriangles.length);
        removableTrianglesBytes[0] = fourLengthBytes[0];
        removableTrianglesBytes[1] = fourLengthBytes[1];
        removableTrianglesBytes[2] = fourLengthBytes[2];
        removableTrianglesBytes[3] = fourLengthBytes[3];
        System.arraycopy(removableTriangles, 0, removableTrianglesBytes, 4, removableTriangles.length);

        saveZipFile(path, indicesBytes, textureCoordinatesBytes, verticesBytes, removableTrianglesBytes, blockedFaces, pathToTexture);
    }
    public static void saveVEMF(String path, float[] vertices, float[] textureCoordinates, int[] indices, String pathToTexture) throws IOException {

        byte[] verticesBytes = new byte[vertices.length*4+4]; //create new array for bytes to be saved into file
        byte[] fourLengthBytes = getFourBytes(vertices.length/3); //save length into bytes
        verticesBytes[0] = fourLengthBytes[0];
        verticesBytes[1] = fourLengthBytes[1];
        verticesBytes[2] = fourLengthBytes[2];
        verticesBytes[3] = fourLengthBytes[3];


        for(int i=0; i<vertices.length; i++){
            byte[] fourBytes = getFourBytes(vertices[i]); //save data into bytes
            verticesBytes[i*4+4] = fourBytes[0];
            verticesBytes[i*4+5] = fourBytes[1];
            verticesBytes[i*4+6] = fourBytes[2];
            verticesBytes[i*4+7] = fourBytes[3];
        } //repeat for each data type

        byte[] textureCoordinatesBytes = new byte[textureCoordinates.length*4+4];
        fourLengthBytes = getFourBytes(textureCoordinates.length/2);
        textureCoordinatesBytes[0] = fourLengthBytes[0];
        textureCoordinatesBytes[1] = fourLengthBytes[1];
        textureCoordinatesBytes[2] = fourLengthBytes[2];
        textureCoordinatesBytes[3] = fourLengthBytes[3];
        for(int i=0; i<textureCoordinates.length; i++){
            byte[] fourBytes = getFourBytes(textureCoordinates[i]);
            textureCoordinatesBytes[i*4+4] = fourBytes[0];
            textureCoordinatesBytes[i*4+5] = fourBytes[1];
            textureCoordinatesBytes[i*4+6] = fourBytes[2];
            textureCoordinatesBytes[i*4+7] = fourBytes[3];
        }
        byte[] indicesBytes = new byte[indices.length*4+4];
        fourLengthBytes = getFourBytes(indices.length);
        indicesBytes[0] = fourLengthBytes[0];
        indicesBytes[1] = fourLengthBytes[1];
        indicesBytes[2] = fourLengthBytes[2];
        indicesBytes[3] = fourLengthBytes[3];
        for(int i=0; i<indices.length; i++){
            byte[] fourBytes = getFourBytes(indices[i]);
            indicesBytes[i*4+4] = fourBytes[0];
            indicesBytes[i*4+5] = fourBytes[1];
            indicesBytes[i*4+6] = fourBytes[2];
            indicesBytes[i*4+7] = fourBytes[3];
        }

        saveZipFile(path, indicesBytes, textureCoordinatesBytes, verticesBytes, pathToTexture);
    }
    private static void saveZipFile(String path, byte[] i, byte[] t, byte[] v, byte[] r, byte b, String pathToTexture) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(path));

        out.putNextEntry(new ZipEntry("i")); //write indices
        out.write(i);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("t"));//write texture coordinates
        out.write(t);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("tex"));
        out.write(new FileInputStream(pathToTexture).readAllBytes()); //write texture file
        out.closeEntry();

        out.putNextEntry(new ZipEntry("v")); //write vertex coordinates
        out.write(v);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("r")); //write removable triangles
        out.write(r);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("b")); //write blocked faces
        out.write(b);
        out.closeEntry();

        out.close();
    }
    private static void saveZipFile(String path, byte[] i, byte[] t, byte[] v, String pathToTexture) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(path));

        out.putNextEntry(new ZipEntry("i")); //write indices
        out.write(i);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("t"));//write texture coordinates
        out.write(t);
        out.closeEntry();

        out.putNextEntry(new ZipEntry("tex"));
        out.write(new FileInputStream(pathToTexture).readAllBytes()); //write texture file
        out.closeEntry();

        out.putNextEntry(new ZipEntry("v")); //write vertex coordinates
        out.write(v);
        out.closeEntry();

        out.close();
    }
    private static byte[] getFourBytes(float f){
        int floatInt = Float.floatToIntBits(f);
        return getFourBytes(floatInt);
    }
    private static byte[] getFourBytes(int i){
        return new byte[]{(byte)i, (byte)(i>>8), (byte)(i>>16), (byte)(i>>24)};
    }

    public static void main(String[] args) throws IOException {
        saveVBMF("resources/VMFModels/grassBlock.vbmf0",
        new float[]{
                        //(0.57735026919,-0.5),(-0.57735026919, -0.5),(0.0, 0.5)
                        0.57735026919f, 1.0f, -0.5f, //top face positions for side faces (texture coordinates)
                        -0.57735026919f, 1.0f, -0.5f,
                        0.0f, 1.0f, 0.5f,

                        0.57735026919f, 0.0f, -0.5f, //bottom face positions for side faces (texture coordinates)
                        -0.57735026919f, 0.0f, -0.5f,
                        0.0f, 0.0f, 0.5f,

                        0.57735026919f, 1.0f, -0.5f, //top face
                        -0.57735026919f, 1.0f, -0.5f,
                        0.0f, 1.0f, 0.5f,

                        0.57735026919f, 0.0f, -0.5f, //bottom face
                        -0.57735026919f, 0.0f, -0.5f,
                        0.0f, 0.0f, 0.5f,
                }, new float[]{
                        0.0f, 0.3f,
                        1.0f, 0.3f,
                        0.5f, 0.3f,

                        0.0f, 0.7f,
                        1.0f, 0.7f,
                        0.5f, 0.7f,

                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        0.5f, 0.5f,

                        0.0f, 0.6f,
                        1.0f, 0.6f,
                        0.5f, 1.0f,
                }, new int[]{
                        6, 7, 8, //top face
                        9, 10, 11, //bottom face

                        //side faces:
                        0, 1, 3,
                        1, 3, 4,

                        1, 2, 4,
                        2, 4, 5,

                        2, 0, 3,
                        2, 5, 3,
                }, "resources/Textures/grass.png",
                new byte[]{
                        //|16s| 8s| 4s| 2s| 1s| (place value) - Yes, java binary is big endian
                        //|+x | -x|  z| -y| +y| (importance)
                        0b00001, //one for each set of three indices (each triangle)
                        0b00010,
                        0b00100,
                        0b00100,
                        0b01000,
                        0b01000,
                        0b10000,
                        0b10000,
                },
                //|1s| 2s| 4s| 8s| 16s| (place value)
                //|+y| -y|  z| -x|  +x| (importance)
                (byte) 0b11111
        );
    }
}
