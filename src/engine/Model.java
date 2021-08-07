package engine;

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
    public Model(VEMFLoader l){
        this.mesh = new Mesh(l.getVertices(), l.getTextureCoordinates(), l.getIndices());
        this.texture = l.getTexture();
    }
    public Model(Mesh mesh, Texture texture){
        this.mesh = mesh;
        this.texture = texture;
    }

    public void cleanUp(){
        mesh.cleanUp();
        texture.cleanUp();
    }

    public void render(){
        texture.bind();
        mesh.render();
        texture.unbind();
    }

    public Mesh getMesh(){return mesh;}
    public Texture getTexture(){return texture;}
}
