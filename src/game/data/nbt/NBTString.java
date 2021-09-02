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
