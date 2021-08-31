package game.data.nbt;

public interface NBTElement {
    byte NBT_ELEMENT_TYPE_INT = 0;
    byte NBT_ELEMENT_TYPE_FLOAT = 1;
    byte NBT_ELEMENT_TYPE_STRING = 2;
    byte NBT_ELEMENT_TYPE_FOLDER = 3;

    byte getType();

    String getName();
    void setName(String name);

    Object getAsObject();

    byte[] serialize();
}
