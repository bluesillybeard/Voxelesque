package game.data.nbt;


public class NBT {
    private NBTElement element;
    private static boolean ready;
    public NBT(NBTElement element){
        this.element = element;
    }
    public NBT(byte[] element) throws InstantiationException {
        byte type = element[0];
    }//TODO: NBT class, deserialization (String and Folder not done yet), and testing of those features
}
