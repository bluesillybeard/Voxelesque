package util.threads;

import org.joml.Vector3i;

import java.util.Comparator;

/**
 * This class is intended to be used in the PriorityThreadPoolExecutor.
 */
public class DistanceRunnable3i extends Number implements Runnable{
    public static final Comparator<DistanceRunnable3i> inOrder = Comparator.comparingInt(DistanceRunnable3i::intValue);

    private final Runnable run;
    public final Vector3i constant;
    public final Vector3i dynamic;


    public DistanceRunnable3i(Runnable r, Vector3i constant, Vector3i dynamic){
        this.run = r;
        this.constant = constant;
        this.dynamic = dynamic;
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
        return (int) constant.distanceSquared(dynamic);
    }

    /**
     * Returns the value of the specified number as a {@code long}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        return constant.distanceSquared(dynamic);
    }

    /**
     * Returns the value of the specified number as a {@code float}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return constant.distanceSquared(dynamic);
    }

    /**
     * Returns the value of the specified number as a {@code double}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code double}.
     */
    @Override
    public double doubleValue() {
        return constant.distanceSquared(dynamic);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof DistanceRunnable3f or) {
            return this.constant.equals(or.constant);
        } else {
            return false;
        }
    }
}
