package engine.model;

import engine.render.ShaderProgram;

public class BlockModel{
    private final BlockMesh mesh;
    private final Texture texture;
    private final ShaderProgram shader;
    public BlockModel(BlockMesh mesh, Texture texture, ShaderProgram shader){
        this.mesh = mesh;
        this.texture = texture;
        this.shader = shader;
    }

    public BlockMesh getMesh(){return this.mesh;}
    public Texture getTexture(){return this.texture;}
    public ShaderProgram getShader(){return this.shader;}
}
