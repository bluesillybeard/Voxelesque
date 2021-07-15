package engine.graph;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    private final int textureID;
    public Texture(String filePath){
        ByteBuffer imgDataBuffer;
        int width, height;
        try {
            BufferedImage img = ImageIO.read(new File(filePath)); //load image
            width = img.getWidth();
            height = img.getHeight();
            int[] imgData = img.getRGB(0, 0, width, height, new int[img.getWidth() * img.getHeight()], 0, img.getWidth());
            //get the RGBA image data
            imgDataBuffer = ByteBuffer.allocateDirect(4 * width * height); //get the ByteBuffer ready for data
            for(int pixel: imgData){ //place data in ByteBuffer
                imgDataBuffer.put((byte)(pixel >> 16 & 255));
                imgDataBuffer.put((byte)(pixel >> 8 & 255)); //bit shifting stuff copied from Java.awt.Color
                imgDataBuffer.put((byte)(pixel & 255));  //because I'm lazy.
                imgDataBuffer.put((byte)(pixel >> 24 & 255));

            }
        } catch (IOException e) {
            e.printStackTrace();
            width = 2;
            height = 2;
            //use the default error texture
            imgDataBuffer = ByteBuffer.allocateDirect(4 * width * height); //get the ByteBuffer ready for data

            imgDataBuffer.put(new byte[]{-1, 0, -1, -1,    0, 0,  0, -1, //magenta, black,
                    0, 0,  0, -1,   -1, 0, -1, -1}); //black, magenta
        }
        imgDataBuffer.flip();

        textureID = glGenTextures();
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
                imgDataBuffer //the actual data
        );
        //I personally think this looks be best - I may change it later if the game's look changes (such as using high-res textures instead)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D); //generate the mipmaps for this image
    }
    public void bind(){
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
}
