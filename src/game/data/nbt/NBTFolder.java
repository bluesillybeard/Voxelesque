package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NBTFolder implements NBTElement{
    private final ArrayList<NBTElement> elements = new ArrayList<>();
    private String name;

    @Override
    public byte getType() {
        return NBT_ELEMENT_TYPE_FOLDER;
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
        return this;
    }

    @Override
    public byte[] serialize() {
        byte[][] valueBytesArray = new byte[elements.size()][];
        int size = 0;
        for(int i=0; i<elements.size(); i++){
            valueBytesArray[i] = elements.get(i).serialize();
            size += valueBytesArray[i].length; //get the size of the serialized data.
        }
        ByteBuffer valueBytesBuffer = ByteBuffer.allocate(size);
        for(byte[] array: valueBytesArray){
            valueBytesBuffer.put(array); //put that data into a ByteBuffer
        }
        ByteBuffer out = StaticUtils.getNBTSerialData(this, valueBytesBuffer);
        if(out.hasArray()) {
            return out.array(); //create the rest of the NBT data
        } else {
            throw new IllegalStateException("ByteBuffer.allocate() returned a non-array buffer! Try using a different JRE.");
        }
    } //TODO: NBT class, serialization(basically done atm), deserialization, and testing of those three features

    //NOTE there are no addElement or removeElement methods - instead, directly modify the list given by this method.
    public List<NBTElement> getElements(){
        return elements;
    }
}
