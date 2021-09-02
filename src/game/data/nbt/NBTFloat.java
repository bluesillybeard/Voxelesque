package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;

public class NBTFloat implements NBTElement{
    private float value;
    private String name;

    public NBTFloat(String name, float value){
        this.name = name;
        this.value = value;
    }
    public NBTFloat(byte[] serializedData) throws InstantiationException {
        if(serializedData[4] != NBT_ELEMENT_TYPE_FLOAT)
            throw new InstantiationException("Cannot use data for type " + serializedData[4] + " to create type " + NBT_ELEMENT_TYPE_FLOAT + ".");
        int size = StaticUtils.getIntFromFourBytes(serializedData[3], serializedData[2], serializedData[1], serializedData[0]);

        this.name = new String(serializedData, 5, size-10);
        this.value = StaticUtils.getFloatFromFourBytes(serializedData[size-4], serializedData[size-3], serializedData[size-2], serializedData[size-1]);
    }
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

    public String toString(){
        return name + ":" + value;
    }
}
