package engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
    public void write(int i) throws IOException {
        this.buffer.append((char)i);
    }

    public String toString(){
        return buffer.toString();
    }
}
