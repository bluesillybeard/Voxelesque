package engine.gl33.render;

import engine.gl33.model.GL33Texture;
import engine.multiplatform.gpu.GPUTextBox;

public class GL33TextBox extends GL33TextEntity implements GPUTextBox {
    private int pointer; //-1 if not selected
    public void type(String characters){
        if(selected()) return;
        super.setText(super.getText().substring(0, pointer) + characters + super.getText().substring(pointer), super.isCenterX(), super.isCenterY());
    }

    /**
     * Sets the text box to be selected, so it will respond to typing from the type(String) method
     * @param point the index to start the cursor at.
     *              if it is -1, it will start at the end of the string.
     */
    public void select(int point){
        if(point != -1)setPointer(point);
        else setPointer(this.getText().length()-1);
    }

    public void setPointer(int point){
        if(!this.selected())this.pointer = point;
    }

    public void deselect(){
        this.pointer = -1;
    }

    public boolean selected(){
        return pointer != -1;
    }

    public void left(){
        if(pointer > 0)
            this.pointer = pointer - 1;
    }
    public void right(){
        if(pointer < getText().length()-1)
            this.pointer = this.pointer+1;
    }

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
