package VectorTools;

public class MyPoint {

    public final double x, y, z;

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public MyPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MyPoint(double val, boolean isX) {
        if (isX) {
            this.x = val;
            this.y = 0;
        } else {
            this.x = 0;
            this.y = val;
        }
        this.z = 0;
    }
}
