package game.data.vector;

import java.util.Objects;

public class IntegerVector3f {
    public int x, y, z;

    public IntegerVector3f(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public IntegerVector3f(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public IntegerVector3f(IntegerVector3f other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IntegerVector3f that = (IntegerVector3f) o;
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
