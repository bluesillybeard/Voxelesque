package engine.multiplatform.Util;

import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    private static final double HALF_PI = Math.PI/2.;
    private static final double DOUBLE_PI = Math.PI*2.;

    public static String loadResource(String fileName) throws Exception {
        return(Files.readString(Path.of(fileName)));
    }

    public static double fastSine(double t){
        //A faster sine calculation based on polynomials and modulus.
        //about twice as fast as using Math.sin()

        //uses a modulus to truncate to a repeating pattern between x=0 and x=2pi
        //then puts that into a 4th degree polynomial to approximate a sine function.
        //ok-ish for graphics and audio, VERY BAD for anything that requires any accuracy at all.
        // may be off by as much as 0.15, most accurate near highest and lowest outputs
        // could use lots more tweaking, this is more of a proof of concept
        double x = Math.abs(t-HALF_PI % DOUBLE_PI);
        return -0.005 * ((x-Math.PI)*(x-Math.PI)*(2*x-4.847*Math.PI)*(2*x+0.847*Math.PI))-1;
    }

}