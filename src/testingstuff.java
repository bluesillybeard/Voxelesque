import java.io.IOException;

public class testingstuff {
    public static void main(String[] args) throws IOException {
        for(int i=0; i < 256; i+=16){
            for(int j = 0; j < 16;j++){
                System.out.print((char) (i+j));
            }
            System.out.println();
        }

    }
}
