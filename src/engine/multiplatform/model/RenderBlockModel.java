package engine.multiplatform.model;

/**
 * Makes transferring blocks between the engine and game easier.
 */
public interface RenderBlockModel {
    static RenderBlockModel newModel(CPUMesh mesh, int texture, int shader){
        return new RenderBlockModel() {
            @Override
            public CPUMesh getMesh() {
                return mesh;
            }
            @Override
            public int getTexture() {
                return texture;
            }
            @Override
            public int getShader() {
                return shader;
            }
        };
    }
    CPUMesh getMesh();
    int getTexture();
    int getShader();
}
