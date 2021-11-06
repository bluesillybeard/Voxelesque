package Math;

import org.joml.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

/**
 * The only difference between this class and the original Vector3i class is the ToString method.
 * Everything else is exactly identical.
 */
public class BetterVector3i extends Vector3i {
    /**
     * Create a new {@link Vector3i} of <code>(0, 0, 0)</code>.
     */
    public BetterVector3i() {
    }

    /**
     * Create a new {@link Vector3i} and initialize all three components with
     * the given value.
     *
     * @param d the value of all three components
     */
    public BetterVector3i(int d) {
        super(d);
    }

    /**
     * Create a new {@link Vector3i} with the given component values.
     *
     * @param x the value of x
     * @param y the value of y
     * @param z
     */
    public BetterVector3i(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Create a new {@link Vector3i} with the same values as <code>v</code>.
     *
     * @param v the {@link Vector3ic} to copy the values from
     */
    public BetterVector3i(Vector3ic v) {
        super(v);
    }

    /**
     * Create a new {@link Vector3i} with the first two components from the
     * given <code>v</code> and the given <code>z</code>
     *
     * @param v the {@link Vector2ic} to copy the values from
     * @param z
     */
    public BetterVector3i(Vector2ic v, int z) {
        super(v, z);
    }

    /**
     * Create a new {@link Vector3i} with the given component values and
     * round using the given {@link RoundingMode}.
     *
     * @param x    the value of x
     * @param y    the value of y
     * @param z    the value of z
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(float x, float y, float z, int mode) {
        super(x, y, z, mode);
    }

    /**
     * Create a new {@link Vector3i} with the given component values and
     * round using the given {@link RoundingMode}.
     *
     * @param x    the value of x
     * @param y    the value of y
     * @param z    the value of z
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(double x, double y, double z, int mode) {
        super(x, y, z, mode);
    }

    /**
     * Create a new {@link Vector3i} with the first two components from the
     * given <code>v</code> and the given <code>z</code> and round using the given {@link RoundingMode}.
     *
     * @param v    the {@link Vector2fc} to copy the values from
     * @param z    the z component
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(Vector2fc v, float z, int mode) {
        super(v, z, mode);
    }

    /**
     * Create a new {@link Vector3i} and initialize its components to the rounded value of
     * the given vector.
     *
     * @param v    the {@link Vector3fc} to round and copy the values from
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(Vector3fc v, int mode) {
        super(v, mode);
    }

    /**
     * Create a new {@link Vector3i} with the first two components from the
     * given <code>v</code> and the given <code>z</code> and round using the given {@link RoundingMode}.
     *
     * @param v    the {@link Vector2dc} to copy the values from
     * @param z    the z component
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(Vector2dc v, float z, int mode) {
        super(v, z, mode);
    }

    /**
     * Create a new {@link Vector3i} and initialize its components to the rounded value of
     * the given vector.
     *
     * @param v    the {@link Vector3dc} to round and copy the values from
     * @param mode the {@link RoundingMode} to use
     */
    public BetterVector3i(Vector3dc v, int mode) {
        super(v, mode);
    }

    /**
     * Create a new {@link Vector3i} and initialize its three components from the first
     * three elements of the given array.
     *
     * @param xyz the array containing at least three elements
     */
    public BetterVector3i(int[] xyz) {
        super(xyz);
    }

    /**
     * Create a new {@link Vector3i} and read this vector from the supplied
     * {@link ByteBuffer} at the current buffer
     * {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which the vector is
     * read, use {@link #BetterVector3i(int, ByteBuffer)}, taking the absolute
     * position as parameter.
     *
     * @param buffer
     * @see #BetterVector3i(int, ByteBuffer)
     */
    public BetterVector3i(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * Create a new {@link Vector3i} and read this vector from the supplied
     * {@link ByteBuffer} starting at the specified absolute buffer
     * position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index  the absolute position into the ByteBuffer
     */
    public BetterVector3i(int index, ByteBuffer buffer) {
        super(index, buffer);
    }

    /**
     * Create a new {@link Vector3i} and read this vector from the supplied
     * {@link IntBuffer} at the current buffer
     * {@link IntBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given IntBuffer.
     * <p>
     * In order to specify the offset into the IntBuffer at which the vector is
     * read, use {@link #BetterVector3i(int, IntBuffer)}, taking the absolute position
     * as parameter.
     *
     * @param buffer
     * @see #BetterVector3i(int, IntBuffer)
     */
    public BetterVector3i(IntBuffer buffer) {
        super(buffer);
    }

    /**
     * Create a new {@link Vector3i} and read this vector from the supplied
     * {@link IntBuffer} starting at the specified absolute buffer
     * position/index.
     * <p>
     * This method will not increment the position of the given IntBuffer.
     *
     * @param index  the absolute position into the IntBuffer
     * @param buffer
     */
    public BetterVector3i(int index, IntBuffer buffer) {
        super(index, buffer);
    }

    public String toString() {
        return this.toString(NumberFormat.getIntegerInstance());
    }

}
