package engine.VMF;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class VEMFSaver {
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

        System.out.println(Arrays.toString(verticesBytes));

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

        out.putNextEntry(new ZipEntry("y")); //write texture file ending
        out.write(pathToTexture.substring(getLastIndexOf(pathToTexture, '.')).getBytes(StandardCharsets.US_ASCII));
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
    private static int getLastIndexOf(String str, char c){
        for(int i=str.length()-1; i>=0; i--){
            if(str.charAt(i) == c){
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        saveVEMF("src/test2.vemf0",
                new float[]{
                        // V0
                        -0.5f, 0.5f, 0.5f,
                        // V1
                        -0.5f, -0.5f, 0.5f,
                        // V2
                        0.5f, -0.5f, 0.5f,
                        // V3
                        0.5f, 0.5f, 0.5f,
                        // V4
                        -0.5f, 0.5f, -0.5f,
                        // V5
                        0.5f, 0.5f, -0.5f,
                        // V6
                        -0.5f, -0.5f, -0.5f,
                        // V7
                        0.5f, -0.5f, -0.5f,
                        // For text coords in top face
                        // V8: V4 repeated
                        -0.5f, 0.5f, -0.5f,
                        // V9: V5 repeated
                        0.5f, 0.5f, -0.5f,
                        // V10: V0 repeated
                        -0.5f, 0.5f, 0.5f,
                        // V11: V3 repeated
                        0.5f, 0.5f, 0.5f,
                        // For text coords in right face
                        // V12: V3 repeated
                        0.5f, 0.5f, 0.5f,
                        // V13: V2 repeated
                        0.5f, -0.5f, 0.5f,
                        // For text coords in left face
                        // V14: V0 repeated
                        -0.5f, 0.5f, 0.5f,
                        // V15: V1 repeated
                        -0.5f, -0.5f, 0.5f,
                        // For text coords in bottom face
                        // V16: V6 repeated
                        -0.5f, -0.5f, -0.5f,
                        // V17: V7 repeated
                        0.5f, -0.5f, -0.5f,
                        // V18: V1 repeated
                        -0.5f, -0.5f, 0.5f,
                        // V19: V2 repeated
                        0.5f, -0.5f, 0.5f,},
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 0.5f,
                        0.5f, 0.5f,
                        0.5f, 0.0f,
                        0.0f, 0.0f,
                        0.5f, 0.0f,
                        0.0f, 0.5f,
                        0.5f, 0.5f,
                        // For text coords in top face
                        0.0f, 0.5f,
                        0.5f, 0.5f,
                        0.0f, 1.0f,
                        0.5f, 1.0f,
                        // For text coords in right face
                        0.0f, 0.0f,
                        0.0f, 0.5f,
                        // For text coords in left face
                        0.5f, 0.0f,
                        0.5f, 0.5f,
                        // For text coords in bottom face
                        0.5f, 0.0f,
                        1.0f, 0.0f,
                        0.5f, 0.5f,
                        1.0f, 0.5f,},
                new int[]{
                        // Front face
                        0, 1, 3, 3, 1, 2,
                        // Top Face
                        8, 10, 11, 9, 8, 11,
                        // Right face
                        12, 13, 7, 5, 12, 7,
                        // Left face
                        14, 15, 6, 4, 14, 6,
                        // Bottom face
                        16, 18, 19, 17, 16, 19,
                        // Back face
                        4, 6, 7, 5, 4, 7,},
                "/home/bluesillybeard/Pictures/grassblock.png");
    }
}
