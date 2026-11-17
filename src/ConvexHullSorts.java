/**
 * ConvexHullSorts  --  *** THIS IS THE ONLY FILE YOU EDIT ***
 *
 * Name: ______________________
 *
 * The Graham scan finds the convex hull in three steps:
 *   1. Choose the point p with the smallest y-coordinate.    <- YOUR SORT #1
 *   2. Sort the other points by polar angle around p.        <- YOUR SORT #2
 *   3. Walk the points in order; throw a point out unless
 *      it makes a counterclockwise (left) turn.              <- already written
 *
 * HOW TO TEST
 *   - Run SortTester.java      -> prints PASS / FAIL for each sort
 *   - Run HullVisualizer.java  -> watch the hull get built step by step
 *
 * Helpers you can use (already written in Point2D.java and below):
 *   Point2D.compareByY(a, b)              negative if a comes before b
 *   Point2D.compareByPolarAngle(p, a, b)  negative if a comes before b
 *   exch(pts, i, j)                       swaps pts[i] and pts[j]
 */
public class ConvexHullSorts {

    /**
     * SORT #1 -- Selection sort by y-coordinate.
     *
     * Goal: after this method runs, pts[0] must be the LOWEST point
     * (smallest y; if tied, smallest x). That point is p, our start point.
     * Sort the whole array from lowest to highest.
     *
     * Selection sort reminder:
     *   for each position i from left to right:
     *     find the index of the smallest item in pts[i .. n-1]
     *     swap it into position i
     *
     * Use Point2D.compareByY(...) to decide which point is "smaller".
     */
    public static void selectionSortByY(Point2D[] pts) {
        // TODO: write selection sort here



    }

    /**
     * SORT #2 -- Insertion sort by polar angle with respect to p.
     *
     * p is already in pts[0] (Sort #1 put it there). LEAVE IT THERE.
     * Sort pts[1] .. pts[n-1] so their polar angles around p go from
     * smallest to largest -- a counterclockwise sweep, like a radar.
     *
     * Insertion sort reminder:
     *   for each i, starting at the first unsorted item:
     *     move pts[i] to the left, swapping with its neighbor,
     *     while it is "smaller" than the neighbor on its left
     *
     * Use Point2D.compareByPolarAngle(p, a, b) to decide which point is "smaller".
     *
     * CAREFUL: pts[0] is p. Your swaps must never move p out of pts[0].
     *          (Hint: pts[1] alone is already "sorted", so where should i start?
     *           And how far left should j be allowed to go?)
     */
    public static void insertionSortByPolarAngle(Point2D[] pts, Point2D p) {
        // TODO: write insertion sort here



    }

    /** Swap pts[i] and pts[j]. (Given -- use it!) */
    private static void exch(Point2D[] pts, int i, int j) {
        Point2D temp = pts[i];
        pts[i] = pts[j];
        pts[j] = temp;
    }
}
