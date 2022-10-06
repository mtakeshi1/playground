package mtakeshi1.playground.jude;

public record P3(double x, double y, double z) {
    public P3() {
        this(0, 0, 0);
    }

    public P3(double[] v) {
        this(v[0], v[1], v[2]);
    }

    public P3 times(double scalar) {
        return new P3(x * scalar, y * scalar, z * scalar);
    }

    public P3 plus(P3 other) {
        return new P3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public P3 mod1() {
        return new P3(x % 1.0, y % 1.0, z % 1.0);
    }

}
