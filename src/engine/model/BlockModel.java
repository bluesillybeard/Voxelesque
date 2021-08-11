package engine.model;

import engine.VMF.VEMFLoader;

public class BlockModel{
    private final BlockMesh mesh;
    private final Texture texture;

    /**
     * Creates a Model.
     * @param l The VEMFLoader that the data will be taken from. Make sure the VEMFLoader has loaded a VEMF file properly.
     */
    public BlockModel(VEMFLoader l){
        this.mesh = new BlockMesh(l.getVertices(), l.getTextureCoordinates(), l.getIndices());
        this.texture = l.getTexture();
    }
    public BlockModel(BlockMesh mesh, Texture texture){
        this.mesh = mesh;
        this.texture = texture;
    }

    public BlockMesh getMesh(){return this.mesh;}
    public Texture getTexture(){return this.texture;}
}
