package engine.gl33.render;

import engine.gl33.model.GL33Texture;
import engine.multiplatform.gpu.GPUTextBox;

public class GL33TextBox extends GL33TextEntity implements GPUTextBox {
    public GL33TextBox(String text, GL33Shader shader, GL33Texture tex, boolean centerX, boolean centerY) {
        super(text, shader, tex, centerX, centerY);
    }

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (This should absolutely under no circumstances ever happen. Not in all time and space should this value ever be returned by this function)
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }

}
