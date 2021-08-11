package engine.VMF;

import engine.model.Texture;

import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipFile;

public class VEMFLoader {
    private byte[] i, t, v, tex;


    public VEMFLoader loadVEMF(File zipFile) throws IOException {
        ZipFile zipIn = new ZipFile(zipFile);
        i = zipIn.getInputStream(zipIn.getEntry("i")).readAllBytes();
        t = zipIn.getInputStream(zipIn.getEntry("t")).readAllBytes();
        v = zipIn.getInputStream(zipIn.getEntry("v")).readAllBytes();
        tex = zipIn.getInputStream(zipIn.getEntry("tex")).readAllBytes();
        return this; //this is to make chaining methods easier.
    }
    public int[] getIndices(){
        int[] indices = new int[getIntFromFourBytes(i[0], i[1], i[2], i[3])];
        for(int j=0; j<indices.length; j++){
            indices[j] = getIntFromFourBytes(i[j*4+4], i[j*4+5], i[j*4+6], i[j*4+7]);
        }
        return indices;
    }
    public float[] getVertices(){
        float[] vertices = new float[getIntFromFourBytes(v[0], v[1], v[2], v[3])*3];
        for(int i=0; i<vertices.length; i++){
            vertices[i] = getFloatFromFourBytes(v[i*4+4], v[i*4+5], v[i*4+6], v[i*4+7]);
        }
        return vertices;
    }
    public float[] getTextureCoordinates(){
        float[] texCords = new float[getIntFromFourBytes(t[0], t[1], t[2], t[3])*2];
        for(int i=0; i<texCords.length; i++){
            texCords[i] = getFloatFromFourBytes(t[i*4+4], t[i*4+5], t[i*4+6], t[i*4+7]);
        }
        return texCords;
    }
    public Texture getTexture(){
        InputStream inStream = new ByteArrayInputStream(tex);
        return new Texture(inStream);
    }
    public static int getIntFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Byte.toUnsignedInt(b0) + (Byte.toUnsignedInt(b1) * 256) + (Byte.toUnsignedInt(b2) * 65536) + (Byte.toUnsignedInt(b3) * 16777216);
    }
    public static float getFloatFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Float.intBitsToFloat(getIntFromFourBytes(b0, b1, b2, b3));
    }

    public static void main(String[] args) throws IOException {
        VEMFLoader load = new VEMFLoader();
        load.loadVEMF(new File("src/test2.vemf0"));
        System.out.println(Arrays.toString(load.getVertices()));
        System.out.println(Arrays.toString(load.getTextureCoordinates()));
        System.out.println(Arrays.toString(load.getIndices()));
        //System.out.println(load.getTexture());


    }

}
