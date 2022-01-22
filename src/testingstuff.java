
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class testingstuff {
    public static void main(String[] args) throws IOException {

        Map<Object, Object> example = new HashMap<>();
        example.put("0", "0");
        example.put("1", "1");
        example.put("2", "2");
        example.put("3", "3");
        example.put("4", "4");
        Iterator<Map.Entry<Object, Object>> iterator = example.entrySet().iterator();

        example.remove("5");

        if(iterator.hasNext())iterator.next();
    }
}
