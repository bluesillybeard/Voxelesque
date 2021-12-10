package game.world.generation;
//perlin noise generator from https://github.com/warmwaffles/Noise/blob/master/src/prime/PerlinNoise.java
/**
 * <p>A Perlin noise generation utility. Construct the PerlinNoise object with
 * the specified parameters and make a call to the {@link #getHeight(float, float)}
 * method.</p>
 *
 * <p>This class does not make use of the <i>Random</i> class.</p>
 * <p>This is actually a modified version by Bluesillybeard, to accept floats instead of doubles.</p>
 *
 * @author Matthew A. Johnston (WarmWaffles), Bluesillybeard
 *
 */
public class PerlinNoise {
    private int    octaves;
    private float amplitude;
    private float frequency;
    private float persistence;
    private int    seed;

    public PerlinNoise(int seed, float persistence, float frequency, float amplitude, int octaves) {
        set(seed, persistence, frequency, amplitude, octaves);
    }

    /**
     * Pass in the x and y coordinates that you desire
     *
     * @return The height of the (x,y) coordinates multiplied by the
     * amplitude
     */
    public float getHeight(float x, float y) {
        return amplitude * total(x,y);
    }

    // ========================================================================
    //                               GETTERS
    // ========================================================================

    public int getSeed() {
        return seed;
    }

    public int getOctaves() {
        return octaves;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public float getFrequency() {
        return frequency;
    }

    public float getPersistence() {
        return persistence;
    }

    // ========================================================================
    //                               SETTERS
    // ========================================================================

    /**
     * Set all of the properties of the noise generator in one swoop
     *
     * @param seed
     * @param persistence
     *            How persistent it is
     * @param frequency
     *            The frequency level
     * @param amplitude
     *            The amplitude you want to apply
     * @param octaves
     *            The octaves of the frequency
     */
    public final void set(int seed, float persistence, float frequency, float amplitude, int octaves) {
        this.seed        = 2 + seed * seed;
        this.octaves     = octaves;
        this.amplitude   = amplitude;
        this.frequency   = frequency;
        this.persistence = persistence;
    }

    public void setSeed(int seed) {
        this.seed = 2 + seed * seed;
    }

    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setPersistence(float persistence) {
        this.persistence = persistence;
    }

    // ========================================================================
    // PRIVATE
    // ========================================================================

    private float total(float x, float y) {
        float t    = 0;
        float amp  = 1;
        float freq = frequency;

        for(int k = 0; k < octaves; k++) {
            t    += getValue(y * freq + seed, x * freq + seed) * amp;
            amp  *= persistence;
            freq *= 2;
        }

        return t;
    }

    private float getValue(float x, float y) {
        int Xint     = (int) x;
        int Yint     = (int) y;
        float Xfrac = x - Xint;
        float Yfrac = y - Yint;

        // noise values
        float n01 = noise(Xint - 1, Yint - 1);
        float n02 = noise(Xint + 1, Yint - 1);
        float n03 = noise(Xint - 1, Yint + 1);
        float n04 = noise(Xint + 1, Yint + 1);
        float n05 = noise(Xint - 1, Yint);
        float n06 = noise(Xint + 1, Yint);
        float n07 = noise(Xint, Yint - 1);
        float n08 = noise(Xint, Yint + 1);
        float n09 = noise(Xint, Yint);

        float n12 = noise(Xint + 2, Yint - 1);
        float n14 = noise(Xint + 2, Yint + 1);
        float n16 = noise(Xint + 2, Yint);

        float n23 = noise(Xint - 1, Yint + 2);
        float n24 = noise(Xint + 1, Yint + 2);
        float n28 = noise(Xint, Yint + 2);

        float n34 = noise(Xint + 2, Yint + 2);

        // find the noise values of the four corners
        float x0y0 = 0.0625f * (n01 + n02 + n03 + n04) + 0.125f * (n05 + n06 + n07 + n08) + 0.25f * (n09);
        float x1y0 = 0.0625f * (n07 + n12 + n08 + n14) + 0.125f * (n09 + n16 + n02 + n04) + 0.25f * (n06);
        float x0y1 = 0.0625f * (n05 + n06 + n23 + n24) + 0.125f * (n03 + n04 + n09 + n28) + 0.25f * (n08);
        float x1y1 = 0.0625f * (n09 + n16 + n28 + n34) + 0.125f * (n08 + n14 + n06 + n24) + 0.25f * (n04);

        // interpolate between those values according to the x and y fractions
        float v1 = interpolate(x0y0, x1y0, Xfrac); // interpolate in x
        // direction (y)
        float v2 = interpolate(x0y1, x1y1, Xfrac); // interpolate in x
        // direction (y+1)

        return interpolate(v1, v2, Yfrac);
    }

    private float interpolate(float x, float y, float a) {
        float negA = 1.0f - a;
        float negASqr = negA * negA;
        float fac1 = 3.0f * (negASqr) - 2.0f * (negASqr * negA);
        float aSqr = a * a;
        float fac2 = 3.0f * aSqr - 2.0f * (aSqr * a);

        return x * fac1 + y * fac2; // add the weighted factors
    }

    private float noise(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
        return 1.0f - (float) (t) * 0.931322574615478515625e-9f;
    }
}