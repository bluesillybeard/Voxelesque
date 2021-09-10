package engine.model;

import engine.VMF.VEMFLoader;

/**
 * this literally just holds a Mesh and Texture - This is to hold a VEMF model after it has been loaded, so it cam be used in multiple entities.
 */
public class Model {
    private final Mesh mesh;
    private final Texture texture;

    /**
     * Creates a Model.
     * @param l The VEMFLoader that the data will be taken from. Make sure the VEMFLoader has loaded a VEMF file properly.
     */
    public Model(VEMFLoader l, boolean storeOnCPU){
        this.mesh = new Mesh(l.getVertices(), l.getTextureCoordinates(), l.getIndices(), storeOnCPU);
        this.texture = l.getTexture();
    }
    public Model(Mesh mesh, Texture texture){
        this.mesh = mesh;
        this.texture = texture;
    }
    public Model(BlockModel model, boolean storeOnCPU){
        this.mesh = new Mesh(model.getMesh(), storeOnCPU);
        this.texture = model.getTexture();
    }

    public void cleanUp(){
        this.mesh.cleanUp();
        this.texture.cleanUp();
    }

    public void render(){
        this.texture.bind();
        this.mesh.render();
    }

    public Mesh getMesh(){return this.mesh;}
    public Texture getTexture(){return this.texture;}
}
