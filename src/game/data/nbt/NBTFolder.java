package game.data.nbt;

import game.misc.StaticUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class NBTFolder implements NBTElement{
    private final ArrayList<NBTElement> elements;
    private String name;

    public NBTFolder(String name){
        this.elements = new ArrayList<>();
        this.name = name;
    }

    public NBTFolder(byte[] serializedData) throws InstantiationException {
        if(serializedData[4] != NBT_ELEMENT_TYPE_FOLDER)
            throw new InstantiationException("Cannot use data for type " + serializedData[4] + " to create type " + NBT_ELEMENT_TYPE_FOLDER + ".");
        int size = StaticUtils.getIntFromFourBytes(serializedData[3], serializedData[2], serializedData[1], serializedData[0]);
        //get the name of the folder
        StringBuilder buffer = new StringBuilder();
        for(int i=5; i<size && serializedData[i] != 0; i++){
            buffer.append((char)serializedData[i]);
        }
        this.name = buffer.toString();

        //deserialize elements
        elements = new ArrayList<>();
        int index = 6+name.length(); //1 byte of type, 4 bytes of size, x bytes of the name, then one more byte for the null terminator
        while(index < size) {
            int elementSize = StaticUtils.getIntFromFourBytes(serializedData[index+3], serializedData[index+2], serializedData[index+1], serializedData[index]);
            int elementType = serializedData[index+4];
            byte[] elementData = Arrays.copyOfRange(serializedData, index, index+size);
            elements.add(switch(elementType){
                case NBT_ELEMENT_TYPE_INT -> new NBTInteger(elementData);
                case NBT_ELEMENT_TYPE_FLOAT -> new NBTFloat(elementData);
                case NBT_ELEMENT_TYPE_STRING -> new NBTString(elementData);
                case NBT_ELEMENT_TYPE_FOLDER -> new NBTFolder(elementData);
                default -> throw new InstantiationException("found invalid NBT type " + elementType + " at index " + index);
            });
            index+=elementSize;
        }
    }

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
        ByteBuffer out = StaticUtils.getNBTSerialHeader(this, valueBytesBuffer);
        if(out.hasArray()) {
            return out.array(); //create the rest of the NBT data
        } else {
            throw new IllegalStateException("ByteBuffer.allocate() returned a non-array buffer! Try using a different JRE.");
        }
    }

    //NOTE there are no addElement or removeElement methods - instead, directly modify the list given by this method.
    public List<NBTElement> getElements(){
        return elements;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder().append("[");
        for(NBTElement element: elements){
            builder.append(", ").append(element.toString());
        }
        return builder.append("]").toString();
    }

}
