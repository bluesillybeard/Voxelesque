package game.data.nbt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTester {
    public static void main(String[] args) throws IOException, InstantiationException {
        /*StringBuffer out = new StringBuffer();
        System.setOut(new PrintStream(new StringOutputStream(out)));
        NBTFolder folder = new NBTFolder("A folder");

        List<NBTElement> folderElements = folder.getElements();
        folderElements.add(new NBTString("String1", "TheValueOfString1"));
        folderElements.add(new NBTInteger("anInt", 18));
        folderElements.add(new NBTFloat("1.5", 1.5f));

        System.out.println((Arrays.toString(folder.serialize())));
        System.out.println(new String(folder.serialize(), StandardCharsets.US_ASCII));
        byte[] arr = folder.serialize();
        for(byte b: arr){
            System.out.print((char)b);
        }

        FileOutputStream fileOut = new FileOutputStream("/home/bluesillybeard/YEEET.txt");
        fileOut.write(out.toString().getBytes(StandardCharsets.US_ASCII));
        fileOut.close();*/

        NBTFolder into = new NBTFolder("A_Folder");
        List<NBTElement> intoElements = into.getElements();
        intoElements.add(new NBTInteger("int0", 9));
        intoElements.add(new NBTString("string0", "value0"));
        intoElements.add(new NBTFloat("float0", 8.5f));
        System.out.println(into);
        byte[] serialized = into.serialize();
        System.out.println(Arrays.toString(serialized));
        NBTFolder inta = new NBTFolder(into.serialize());
        System.out.println(inta);

    }
}
