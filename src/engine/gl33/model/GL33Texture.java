package engine.gl33.model;

import engine.multiplatform.gpu.GPUTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GL33Texture implements GPUTexture {
    private final int textureID;

    public GL33Texture(String filePath, PrintStream print) throws IOException {
        this(new FileInputStream(filePath), print);
    }

    public GL33Texture(InputStream stream, PrintStream print){
        int textureID1;
        try {
            textureID1 = loadTexture(ImageIO.read(stream));
        } catch (IOException e){
            e.printStackTrace(print);
            int width = 2;
            int height = 2;
            //use the default error texture
            ByteBuffer imgDataBuffer = ByteBuffer.allocateDirect(4 * width * height) //get the ByteBuffer ready for data
                    .put(new byte[]{-1, 0, -1, -1,    0, 0,  0, -1, //magenta, black,
                                   0, 0,  0, -1,   -1, 0, -1, -1}); //black, magenta
            textureID1 = loadTexture(imgDataBuffer, width, height);
        }
        this.textureID = textureID1;
    }
    public GL33Texture(BufferedImage img){
        this.textureID = loadTexture(img);
    }
    public void bind(){
        glBindTexture(GL_TEXTURE_2D, this.textureID);
    }
    public void cleanUp(){
        glDeleteTextures(this.textureID);
    }

    private int loadTexture(ByteBuffer rawImgDataBuffer, int width, int height){
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 0);

        glTexImage2D(
                GL_TEXTURE_2D, //the type of texture
                0, //used for mipmaps, this is the base layer.
                GL_RGBA, //the texture data type to be stored internally
                width, //width and height
                height,
                0, //must be 0
                GL_RGBA, //the format of the data
                GL_UNSIGNED_BYTE, //the data type
                rawImgDataBuffer //the actual data
        );
        //I personally think this looks be best - I may change it later if the game's look changes (such as using high-res textures instead)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D); //generate the mipmaps for this image
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureID;
    }

    private int loadTexture(BufferedImage img){
        ByteBuffer imgDataBuffer;
        int width = img.getWidth();
        int height = img.getHeight();
        int[] imgData = img.getRGB(0, 0, width, height, new int[img.getWidth() * img.getHeight()], 0, img.getWidth());
        //get the RGBA image data
        imgDataBuffer = ByteBuffer.allocateDirect(4 * width * height); //get the ByteBuffer ready for data
        for(int pixel: imgData){ //place data in ByteBuffer
            imgDataBuffer.put((byte)(pixel >> 16 & 255));
            imgDataBuffer.put((byte)(pixel >> 8 & 255)); //bit shifting stuff copied from Java.awt.Color
            imgDataBuffer.put((byte)(pixel & 255));
            imgDataBuffer.put((byte)(pixel >> 24 & 255));

        }
        imgDataBuffer.flip();

        return loadTexture(imgDataBuffer, width, height);
    }

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (for when
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }
}
