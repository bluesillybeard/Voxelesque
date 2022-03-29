package engine.gl33.render;

import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Model;
import engine.gl33.model.GL33Texture;
import engine.multiplatform.Util.Utils;
import engine.multiplatform.gpu.GPUTextEntity;

public class GL33TextEntity extends GL33Entity implements GPUTextEntity {
    //this class is VERY small, but that's because it doesn't add a lot of functionality.
    //in the future, I might add a much more complicated system with proper fonts, modifiers, etc.
    private String text;
    private boolean centerX;
    private boolean centerY;
    public GL33TextEntity(String text, GL33Shader shader, GL33Texture tex, boolean centerX, boolean centerY) {
        super(new GL33Mesh(Utils.generateTextMesh(text, centerX, centerY)), shader, tex);
        this.centerX = centerX;
        this.centerY = centerY;
        this.text = text;
    }

    public void setText(String text, boolean centerX, boolean centerY){
        this.centerX = centerX;
        this.centerY = centerY;
        if(!this.text.equals(text)){
            this.text = text;
            super.model.mesh.delete();
            super.model = new GL33Model(new GL33Mesh(Utils.generateTextMesh(text, centerX, centerY)), super.model.texture);
        }
    }

    public String getText(){
        return text;
    }

    public boolean isCenterX() {
        return centerX;
    }

    public boolean isCenterY() {
        return centerY;
    }
}
