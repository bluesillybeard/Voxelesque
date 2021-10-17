package VMF;

import oldEngine.model.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipFile;

public class VMFLoader {
    private byte[] i, t, v, tex, r;
    private byte b;


    public VMFLoader loadVEMF(File zipFile) throws IOException {
        ZipFile zipIn = new ZipFile(zipFile);
        i = zipIn.getInputStream(zipIn.getEntry("i")).readAllBytes();
        t = zipIn.getInputStream(zipIn.getEntry("t")).readAllBytes();
        v = zipIn.getInputStream(zipIn.getEntry("v")).readAllBytes();
        tex = zipIn.getInputStream(zipIn.getEntry("tex")).readAllBytes();
        return this; //this is to make chaining methods easier.
    }

    public VMFLoader loadVBMF(File zipFile) throws IOException {
        ZipFile zipIn = new ZipFile(zipFile);
        i = zipIn.getInputStream(zipIn.getEntry("i")).readAllBytes();
        t = zipIn.getInputStream(zipIn.getEntry("t")).readAllBytes();
        v = zipIn.getInputStream(zipIn.getEntry("v")).readAllBytes();
        tex = zipIn.getInputStream(zipIn.getEntry("tex")).readAllBytes();
        r = zipIn.getInputStream(zipIn.getEntry("r")).readAllBytes();
        b = (byte) zipIn.getInputStream(zipIn.getEntry("b")).read();
        return this; //this is to make chaining methods easier.
    }

    //NOTE: when you get information about mesh, it will delete the source bytes to save memory and performance.
    // Basically, once you get that information you have to reload the loader to call it again.
    public int[] getIndices(){
        int[] indices = new int[getIntFromFourBytes(i[0], i[1], i[2], i[3])];
        for(int j=0; j<indices.length; j++){
            indices[j] = getIntFromFourBytes(i[j*4+4], i[j*4+5], i[j*4+6], i[j*4+7]);
        }
        i = null;
        return indices;
    }
    public float[] getVertices(){
        float[] vertices = new float[getIntFromFourBytes(v[0], v[1], v[2], v[3])*3];
        for(int i=0; i<vertices.length; i++){
            vertices[i] = getFloatFromFourBytes(v[i*4+4], v[i*4+5], v[i*4+6], v[i*4+7]);
        }
        v = null;
        return vertices;
    }
    public float[] getTextureCoordinates(){
        float[] texCords = new float[getIntFromFourBytes(t[0], t[1], t[2], t[3])*2];
        for(int i=0; i<texCords.length; i++){
            texCords[i] = getFloatFromFourBytes(t[i*4+4], t[i*4+5], t[i*4+6], t[i*4+7]);
        }
        t = null;
        return texCords;
    }
    public Texture getTexture(){
        InputStream inStream = new ByteArrayInputStream(tex);
        //can't delete tex because it is used in the InputStream.
        return new Texture(inStream);
    }

    public BufferedImage getImage() throws IOException {
        InputStream inStream = new ByteArrayInputStream(tex);
        //can't delete tex because it is used in the InputStream.
        return ImageIO.read(inStream);
    }

    public byte[] getRemovableTriangles(){
        //Removable triangles are an optimization system that is used during chunk building and only applies to voxels.
        // Each
        byte[] removableTriangles = new byte[getIntFromFourBytes(r[0], r[1], r[2], r[3])];
        System.arraycopy(r, 4, removableTriangles, 0, removableTriangles.length);
         r = null;
        return removableTriangles;
    }

    public byte getBlockedFaces(){
        return b;
    }


    private static int getIntFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Byte.toUnsignedInt(b0) + (Byte.toUnsignedInt(b1) << 8) + (Byte.toUnsignedInt(b2) << 16) + (Byte.toUnsignedInt(b3) << 24);
    }
    private static float getFloatFromFourBytes(byte b0, byte b1, byte b2, byte b3){
        return Float.intBitsToFloat(getIntFromFourBytes(b0, b1, b2, b3));
    }

    public static void main(String[] args) throws IOException {
        VMFLoader load = new VMFLoader();
        load.loadVEMF(new File("src/test2.vemf0"));
        System.out.println(Arrays.toString(load.getVertices()));
        System.out.println(Arrays.toString(load.getTextureCoordinates()));
        System.out.println(Arrays.toString(load.getIndices()));
        //System.out.println(load.getTexture());


    }

}
