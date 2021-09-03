package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NBTString implements NBTElement{
    private String value;
    private String name;

    public NBTString(String name, String value){
        this.name = name;
        this.value = value;
    }
    public NBTString(byte[] serializedData) throws InstantiationException {
        if(serializedData[4] != NBT_ELEMENT_TYPE_STRING)
            throw new InstantiationException("Cannot use data for type " + serializedData[4] + " to create type " + NBT_ELEMENT_TYPE_STRING + ".");
        int size = StaticUtils.getIntFromFourBytes(serializedData[3], serializedData[2], serializedData[1], serializedData[0]);

        StringBuilder builder = new StringBuilder();
        int valueOffset = 5;
        for(int i=5; i<size; i++){
            valueOffset++;
            if(serializedData[i] == 0) break;
            builder.append((char)serializedData[i]);
        }
        this.name = builder.toString();
        builder = new StringBuilder();
        for(int i = valueOffset; i<size; i++){
            builder.append((char)serializedData[i]);
        }
        this.value = builder.toString();

    }

    @Override
    public byte getType() {
        return NBT_ELEMENT_TYPE_STRING;
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
        return value;
    }

    @Override
    public byte[] serialize() {
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        ByteBuffer out = StaticUtils.getNBTSerialData(this, valueBytes);
        if(out.hasArray()) {
            return out.array();
        } else {
            throw new IllegalStateException("ByteBuffer.allocate() returned a non-array buffer! Try using a different JRE.");
        }
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String toString(){
        return name + ":" + value;
    }
}
