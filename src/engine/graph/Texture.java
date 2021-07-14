package engine.graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    public Texture(String filePath){
        try {
            BufferedImage img = ImageIO.read(new File(filePath)); //load image
            int[] imgData = img.getRGB(0, 0, img.getWidth(), img.getHeight(), new int[img.getWidth() * img.getHeight()], 0, img.getWidth());
            //get the RGBA image data
            ByteBuffer imgDataBuffer = ByteBuffer.allocateDirect(4 * img.getWidth() * img.getHeight()); //get the ByteBuffer ready for data
            for(int pixel: imgData){ //place data in ByteBuffer
                Color ColorPix = new Color(pixel, true);
                imgDataBuffer.put((byte)ColorPix.getRed());
                imgDataBuffer.put((byte)ColorPix.getGreen()); //I would have done the bit shifting and stuff myself,
                imgDataBuffer.put((byte)ColorPix.getBlue()); //but java.awt.Color can conveniently do that for me.
                imgDataBuffer.put((byte)ColorPix.getAlpha());
            }
            imgDataBuffer.flip();

            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexImage2D(
                    GL_TEXTURE_2D, //the type of texture
                    0, //used for mipmaps, this is the base layer.
                    GL_RGBA, //the texture data type to be stored internally
                    img.getWidth(), //width and height
                    img.getHeight(),
                    0, //must be 0
                    GL_RGBA, //the format of the data
                    GL_UNSIGNED_BYTE, //the data type
                    imgDataBuffer //the actual data
            );

            glGenerateMipmap(GL_TEXTURE_2D); //generate the mipmaps for this image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
