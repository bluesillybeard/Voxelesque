package engine.gl33.render;

import engine.gl33.model.GPUMesh;
import engine.gl33.model.GPUModel;
import engine.gl33.model.GPUTexture;
import engine.multiplatform.Util.Utils;

public class RenderableTextEntity extends RenderableEntity{
    //this class is VERY small, but that's because it doesn't add a lot of functionality.
    //in the future, I might add a much more complicated system with proper fonts, modifiers, etc.
    private String text;
    public RenderableTextEntity(String text, ShaderProgram shader, GPUTexture tex, boolean centerX, boolean centerY) {
        super(new GPUMesh(Utils.generateTextMesh(text, centerX, centerY)), shader, tex);
        this.text = text;
    }

    public void setText(String text, boolean centerX, boolean centerY){
        if(!this.text.equals(text)){
            this.text = text;
            super.setModel(new GPUModel(new GPUMesh(Utils.generateTextMesh(text, centerX, centerY)), super.getModel().texture));
        }
    }


}
