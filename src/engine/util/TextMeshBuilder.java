package engine.util;

import engine.model.Mesh;
import engine.model.Model;
import engine.model.Texture;
import engine.render.RenderableEntity;
import engine.render.ShaderProgram;

import java.util.ArrayList;
import java.util.Locale;

public class TextMeshBuilder {
    public static Mesh generateMesh(String text, boolean centerX, boolean centerY){
        //Yes, I am aware that this is a really inefficient way of doing it.
        //But, unless we are rendering insane amounts of text, I think it will be fine.

        char[] iterableText = text.toCharArray();
        ArrayList<ArrayList<Character>> string = new ArrayList<>();
        ArrayList<Character> line = new ArrayList<>();
        int numCharacters = 0;
        for(char character: iterableText){
            if(character == '\n'){
                string.add(line);
                line = new ArrayList<>();
            } else {
                line.add(character);
                numCharacters++;
            }
        }
        float[] positions = new float[numCharacters*12];
        float[] textureCoordinates = new float[numCharacters*8];
        int[] indices = new int[numCharacters*6];
        string.add(line);
        float YStart = centerY ? -string.size()/2f : 0; //the farthest up coordinate of the text.

        int charIndex = 0;
        for(int i=0; i < string.size(); i++){
            float XStart = centerX ? -string.get(i).size()/2f : 0; //the farthest left coordinate of the text.
            for(int j=0; j<string.get(i).size(); j++){
                char character = string.get(i).get(j);
                System.out.print(character);
                int column = character & 15;
                int row = character >> 4 & 15; //get the last 4 bits and first 4 bits

                //UV coordinates
                float UVXPosition = column*0.0625f;
                float UVYPosition = row*0.0625f; //get the actual UV coordinates of the top left corner
                int j8 = 8*charIndex;
                textureCoordinates[j8  ] = UVXPosition;
                textureCoordinates[j8+1] = UVYPosition; //set the top left

                textureCoordinates[j8+2] = UVXPosition+0.0625f;
                textureCoordinates[j8+3] = UVYPosition; //top-right

                textureCoordinates[j8+4] = UVXPosition;
                textureCoordinates[j8+5] = UVYPosition + 0.0625f; //bottom left

                textureCoordinates[j8+6] = UVXPosition + 0.0625f;
                textureCoordinates[j8+7] = UVYPosition + 0.0625f; //bottom right

                //vertex positions
                int j12 = charIndex*12;
                float iXStart = XStart+j;
                float iYStart = YStart-i;
                positions[j12  ] = iXStart; //top left
                positions[j12+1] = iYStart+1;
                positions[j12+2] = 0;

                positions[j12+3] = iXStart+1; //top right
                positions[j12+4] = iYStart+1;
                positions[j12+5] = 0;

                positions[j12+6] = iXStart; //bottom left
                positions[j12+7] = iYStart;
                positions[j12+8] = 0;

                positions[j12+9] = iXStart+1; //bottom right
                positions[j12+10] = iYStart;
                positions[j12+11] = 0;

                //indices 0, 1, 2, 1, 2, 3
                int j6 = charIndex*6;
                indices[j6  ] = charIndex*4;
                indices[j6+1] = 1+charIndex*4;
                indices[j6+2] = 2+charIndex*4;

                indices[j6+3] = 1+charIndex*4;
                indices[j6+4] = 2+charIndex*4;
                indices[j6+5] = 3+charIndex*4;
                charIndex++;
            }
            System.out.println();
        }

        //OLD ALGORITHM - designed for a single line, then haphazardly had new lines added to it.
        /*char[] string = text.toCharArray();

        float XStart = centerX ? -string.length/2f : 0; //the farthest left coordinate of the text.
        int numLines = 0;
        for(char c: string){
            if(c == '\n') numLines++;
        }
        float YStart = centerY ? numLines/2f : 0; //the farthest up coordinate of the text.
        int XPosition = 0;
        int YPosition = 0;
        for(int i=0; i < string.length; i++){
            //get the character and position
            char character = string[i];
            if(character == '\n'){
                XPosition = 0;
                YPosition++;
                continue;
            }
            int column = character & 15;
            int row = character >> 4 & 15; //get the last 4 bits and first 4 bits

            //UV coordinates
            float UVXPosition = column*0.0625f;
            float UVYPosition = row*0.0625f; //get the actual UV coordinates of the top left corner
            int i8 = 8*i;
            textureCoordinates[i8  ] = UVXPosition;
            textureCoordinates[i8+1] = UVYPosition; //set the top left

            textureCoordinates[i8+2] = UVXPosition+0.0625f;
            textureCoordinates[i8+3] = UVYPosition; //top-right

            textureCoordinates[i8+4] = UVXPosition;
            textureCoordinates[i8+5] = UVYPosition + 0.0625f; //bottom left

            textureCoordinates[i8+6] = UVXPosition + 0.0625f;
            textureCoordinates[i8+7] = UVYPosition + 0.0625f; //bottom right

            //vertex positions
            int i12 = i*12;
            float iXStart = XStart+XPosition;
            float iYStart = YStart-YPosition;
            positions[i12  ] = iXStart; //top left
            positions[i12+1] = iYStart+1;
            positions[i12+2] = 0;

            positions[i12+3] = iXStart+1; //top right
            positions[i12+4] = iYStart+1;
            positions[i12+5] = 0;

            positions[i12+6] = iXStart; //bottom left
            positions[i12+7] = iYStart;
            positions[i12+8] = 0;

            positions[i12+9] = iXStart+1; //bottom right
            positions[i12+10] = iYStart;
            positions[i12+11] = 0;

            //indices 0, 1, 2, 1, 2, 3
            int i6 = i*6;
            indices[i6  ] = i*4;
            indices[i6+1] = 1+i*4;
            indices[i6+2] = 2+i*4;

            indices[i6+3] = 1+i*4;
            indices[i6+4] = 2+i*4;
            indices[i6+5] = 3+i*4;

            XPosition++;
        }*/
        Mesh mesh = new Mesh(positions, textureCoordinates, indices);
        System.out.println(mesh);
        return mesh;
    }
    public static Model generateModel(String text, Texture texture, boolean centerX, boolean centerY){
        return new Model(generateMesh(text, centerX, centerY), texture);
    }
    public static RenderableEntity generateEntity(String text, Texture texture, ShaderProgram shader, boolean centerX, boolean centerY){
        return new RenderableEntity(generateModel(text, texture, centerX, centerY), shader);
    }
}
