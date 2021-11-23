package engine.multiplatform.Util.threads;

import org.joml.Vector3f;

import java.util.Comparator;

public class DistanceRunnable extends Number implements Runnable{
    public static final Comparator<DistanceRunnable> inOrder = Comparator.comparingInt(DistanceRunnable::intValue);

    private final Runnable run;
    private final Vector3f a;
    private final Vector3f b;

    public DistanceRunnable(Runnable r, Vector3f a, Vector3f b){
        this.run = r;
        this.a = a;
        this.b = b;
    }
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        run.run();
    }

    /**
     * Returns the value of the specified number as an {@code int}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code int}.
     */
    @Override
    public int intValue() {
        return (int)a.distance(b);
    }

    /**
     * Returns the value of the specified number as a {@code long}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        return (long)a.distance(b);
    }

    /**
     * Returns the value of the specified number as a {@code float}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return a.distance(b);
    }

    /**
     * Returns the value of the specified number as a {@code double}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code double}.
     */
    @Override
    public double doubleValue() {
        return a.distance(b);
    }
}
