package engine.multiplatform.Util;

import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
    StringBuffer buffer;
    /**
     * Create a BufferPrintStream. This is used to get a String from Exception.PrintStackTrace in the LWJGLRenderer class.
     * @param s the StringBuffer that will hold the printed contents.
     */
    public StringOutputStream(StringBuffer s){
        this.buffer = s;
    }

    @Override
    public void write(int i) {
        this.buffer.append((char)i);
    }

    public String toString(){
        return buffer.toString();
    }
}
