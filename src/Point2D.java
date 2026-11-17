/**
 * Point2D  --  an immutable point in the plane.
 *
 * Based on the Point2D data type from Sedgewick & Wayne, Algorithms 4th ed.,
 * Section 2.1 (Elementary Sorts -> Convex Hull).
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 *
 * Methods you will use in SortByY.java and SortByPolarAngle.java:
 *   a.x(), a.y()               the coordinates of point a
 *   Point2D.ccw(a, b, c)       +1 left turn, -1 right turn, 0 straight line
 *   p.distanceSquaredTo(a)     (distance from p to a) squared
 */
public class Point2D {

    private final double x;
    private final double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() { return x; }
    public double y() { return y; }

    /**
     * Is a -> b -> c a counterclockwise turn?  (from lecture)
     *   +1  counterclockwise (c is to the LEFT of the ray a->b)
     *   -1  clockwise        (c is to the RIGHT of the ray a->b)
     *    0  collinear        (a, b, c are on one straight line)
     */
    public static int ccw(Point2D a, Point2D b, Point2D c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if      (area2 < 0) return -1;   // clockwise
        else if (area2 > 0) return +1;   // counter-clockwise
        else                return  0;   // collinear
    }

    /** Squared distance between this point and that point. */
    public double distanceSquaredTo(Point2D that) {
        double dx = this.x - that.x;
        double dy = this.y - that.y;
        return dx * dx + dy * dy;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point2D)) return false;
        Point2D that = (Point2D) other;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(x) + Double.hashCode(y);
    }

    @Override
    public String toString() {
        return "(" + fmt(x) + ", " + fmt(y) + ")";
    }

    private static String fmt(double v) {
        if (v == Math.rint(v)) return String.valueOf((long) v);
        return String.format("%.1f", v);
    }
}
