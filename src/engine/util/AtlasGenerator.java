package engine.util;
//The code below is a heavily edited version of https://github.com/lukaszdk/texture-atlas-generator
import engine.model.BlockMesh;
import engine.model.BlockModel;
import engine.model.Texture;
import engine.render.ShaderProgram;

import java.awt.*;
import java.sql.Blob;
import java.util.*;
import java.io.*;
import java.awt.image.*;

public class AtlasGenerator{

    /**
     * places the textures into an atlas,
     * creates copies of the meshes and edits their UV coordinates to use the newly generated atlas,
     * and uploads all the data to the GPU.
     * the same index of texture and mesh are linked together into a model
     *
     * @param textures the textures to use.
     * @param meshes the meshes to use.
     * @return the output array of BlockModels.
     */
    public static BlockModel[] generateBlockModels(BufferedImage[] textures, BlockMesh[] meshes, ShaderProgram shader){
        int totalWidth = 0;
        int totalHeight = 0;
        for(BufferedImage tex: textures){
            totalWidth += tex.getWidth();
            totalHeight += tex.getHeight();
        }
        return Run(totalWidth*2, totalHeight*2, 0, false, textures, meshes, shader);
    }

    public static BlockModel[] Run(int width, int height, int padding, boolean ignoreErrors, BufferedImage[] images, BlockMesh[] meshes, ShaderProgram shader)
    {
        Set<ImageName> imageNameSet = new TreeSet<>(new ImageNameComparator());

        for(int i=0; i<images.length; i++)
        {
            BufferedImage image = images[i];
            if(image.getWidth() > width || image.getHeight() > height)
            {
                if(!ignoreErrors)
                    throw new RuntimeException("image" + i + " (" + image.getWidth() + "x" + image.getHeight() + ") is larger than the atlas (" + width + "x" + height + ")");
                else
                    System.err.println("image" + i + " (" + image.getWidth() + "x" + image.getHeight() + ") is larger than the atlas (" + width + "x" + height + ")");
            }

            imageNameSet.add(new ImageName(image, i));

        }

        Texture atlas = new Texture(width, height);

        for(ImageName imageName : imageNameSet)
        {
            if(!atlas.AddImage(imageName.image, imageName.index, padding))
            {
                if(!ignoreErrors)
                    throw new RuntimeException("unable to add image " + imageName.index + " to the atlas!");
                else
                    System.err.println("unable to add image " + imageName.index + " to the atlas!");
            }
        }
        return atlas.Write(width, height, meshes, shader);
    }


    private static class ImageName
    {
        public BufferedImage image;
        public int index;

        public ImageName(BufferedImage image, int name)
        {
            this.image = image;
            this.index = name;
        }
    }

    private static class ImageNameComparator implements Comparator<ImageName>
    {
        public int compare(ImageName image1, ImageName image2)
        {
            int area1 = image1.image.getWidth() * image1.image.getHeight();
            int area2 = image2.image.getWidth() * image2.image.getHeight();

            if(area1 != area2)
            {
                return area2 - area1;
            }
            else
            {
                return image1.index - image2.index;
            }
        }
    }

    public static class Texture
    {
        private static class Node
        {
            public Rectangle rect;
            public Node[] child;
            public BufferedImage image;

            public Node(int x, int y, int width, int height)
            {
                rect = new Rectangle(x, y, width, height);
                child = new Node[2];
                child[0] = null;
                child[1] = null;
                image = null;
            }

            public boolean IsLeaf()
            {
                return child[0] == null && child[1] == null;
            }

            // Algorithm from http://www.blackpawn.com/texts/lightmaps/
            public Node Insert(BufferedImage image, int padding)
            {
                if(!IsLeaf())
                {
                    Node newNode = child[0].Insert(image, padding);

                    if(newNode != null)
                    {
                        return newNode;
                    }

                    return child[1].Insert(image, padding);
                }
                else
                {
                    if(this.image != null)
                    {
                        return null; // occupied
                    }

                    if(image.getWidth() > rect.width || image.getHeight() > rect.height)
                    {
                        return null; // does not fit
                    }

                    if(image.getWidth() == rect.width && image.getHeight() == rect.height)
                    {
                        this.image = image; // perfect fit
                        return this;
                    }

                    int dw = rect.width - image.getWidth();
                    int dh = rect.height - image.getHeight();

                    if(dw > dh)
                    {
                        child[0] = new Node(rect.x, rect.y, image.getWidth(), rect.height);
                        child[1] = new Node(padding + rect.x + image.getWidth(), rect.y, rect.width - image.getWidth() - padding, rect.height);
                    }
                    else
                    {
                        child[0] = new Node(rect.x, rect.y, rect.width, image.getHeight());
                        child[1] = new Node(rect.x, padding + rect.y + image.getHeight(), rect.width, rect.height - image.getHeight() - padding);
                    }

                    return child[0].Insert(image, padding);
                }
            }
        }

        private final BufferedImage image;
        private final Graphics2D graphics;
        private final Node root;
        private final Map<Integer, Rectangle> rectangleMap;

        public Texture(int width, int height)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            graphics = image.createGraphics();

            root = new Node(0, 0, width, height);
            rectangleMap = new TreeMap<>();
        }

        public boolean AddImage(BufferedImage image, int index, int padding)
        {
            Node node = root.Insert(image, padding);

            if(node == null)
            {
                return false;
            }

            rectangleMap.put(index, node.rect);
            graphics.drawImage(image, null, node.rect.x, node.rect.y);

            return true;
        }

        public BlockModel[] Write(int width, int height, BlockMesh[] meshes, ShaderProgram shader)
        {
            engine.model.Texture glTexture = new engine.model.Texture(this.image);
            BlockModel[] out = new BlockModel[meshes.length];
            for(Map.Entry<Integer, Rectangle> UVMapping : rectangleMap.entrySet())
            {
                Rectangle rect = UVMapping.getValue();
                float rx = (float)rect.x/width;
                float ry = (float)rect.y/height;
                float rw = (float)rect.width/width;
                float rh = (float)rect.height/height;
                int keyVal = UVMapping.getKey();
                for(int i=0; i<meshes[keyVal].UVCoords.length/2; i++){
                    meshes[keyVal].UVCoords[2*i  ] = meshes[keyVal].UVCoords[2*i  ]*rw+rx;
                    meshes[keyVal].UVCoords[2*i+1] = meshes[keyVal].UVCoords[2*i+1]*rh+ry;
                }
                out[keyVal] = new BlockModel(meshes[keyVal], glTexture, shader);
            }
            return out;
        }
    }
}
