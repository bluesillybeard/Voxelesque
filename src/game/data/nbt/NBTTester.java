package game.data.nbt;

import engine.util.StringOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

        NBTInteger into = new NBTInteger("An-NBT_String", 1024);

        NBTInteger inta = new NBTInteger(into.serialize());
        System.out.println(inta);

    }
}
