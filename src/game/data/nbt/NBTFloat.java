package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;

public class NBTFloat implements NBTElement{
    private float value;
    private String name;
    @Override
    public byte getType() {
        return NBT_ELEMENT_TYPE_FLOAT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getAsObject() {
        return null;
    }

    @Override
    public byte[] serialize() {
        byte[] valueBytes = StaticUtils.getFourBytes(value);
        ByteBuffer out = StaticUtils.getNBTSerialData(this, valueBytes);
        if(out.hasArray()) {
            return out.array();
        } else {
            throw new IllegalStateException("ByteBuffer.allocate() returned a non-array buffer! Try using a different JRE.");
        }
    }

    public float getValue(){
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
