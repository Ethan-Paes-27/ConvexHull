/**
 * Point2D  --  an immutable point in the plane.
 *
 * Based on the Point2D data type from Sedgewick & Wayne, Algorithms 4th ed.,
 * Section 2.1 (Elementary Sorts -> Convex Hull).
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 *
 * The two compare methods at the bottom are the "rules" your sorts use:
 *   compareByY(a, b)              -> used by Sort #1 (selection sort)
 *   compareByPolarAngle(p, a, b)  -> used by Sort #2 (insertion sort)
 *
 * Both follow the usual Java convention:
 *   negative  ->  a comes BEFORE b
 *   zero      ->  a and b are tied
 *   positive  ->  a comes AFTER b
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

    /**
     * Rule for SORT #1.
     * Compares two points by y-coordinate (smaller y comes first).
     * If the y-coordinates are tied, the point with the smaller x comes first.
     */
    public static int compareByY(Point2D a, Point2D b) {
        if (a.y < b.y) return -1;
        if (a.y > b.y) return +1;
        if (a.x < b.x) return -1;
        if (a.x > b.x) return +1;
        return 0;
    }

    /**
     * Rule for SORT #2.
     * Compares a and b by the polar angle they make with the start point p
     * (angle measured counterclockwise from the positive x-direction).
     *
     * Notice there is no trigonometry!  Because p is the lowest point, every
     * other point is "above" p, so we can use ccw():
     *   if p -> a -> b turns LEFT, then b's angle is bigger, so a comes first.
     * If p, a, b are on one line (same angle), the point closer to p comes first.
     */
    public static int compareByPolarAngle(Point2D p, Point2D a, Point2D b) {
        int turn = ccw(p, a, b);
        if (turn > 0) return -1;   // a has the smaller angle
        if (turn < 0) return +1;   // b has the smaller angle
        return Double.compare(p.distanceSquaredTo(a), p.distanceSquaredTo(b));
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
