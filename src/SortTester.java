import java.util.Random;

/**
 * SortTester  --  RUN THIS FILE to check your two sorts.
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 */
public class SortTester {

    public static void main(String[] args) {
        System.out.println("=== Convex Hull Sort Tester ===\n");
        boolean ok1 = testSort1();
        boolean ok2 = ok1 && testSort2();
        boolean ok3 = ok2 && testHull();

        System.out.println();
        if (ok1 && ok2 && ok3) {
            System.out.println("ALL TESTS PASSED! Now run HullVisualizer and step through the scan.");
        } else if (!ok1) {
            System.out.println("Fix Sort #1 first (Sort #2 needs p in pts[0]).");
        } else {
            System.out.println("Keep going -- read the FAIL message above.");
        }
    }

    // -------- Sort #1: selection sort by y --------
    static boolean testSort1() {
        System.out.println("Sort #1  selectionSortByY");
        Point2D[] small = {
            new Point2D(3, 5), new Point2D(1, 2), new Point2D(4, 1), new Point2D(2, 1), new Point2D(5, 4)
        };
        Point2D[] a = small.clone();
        ConvexHullSorts.selectionSortByY(a);
        System.out.println("   input : " + str(small));
        System.out.println("   output: " + str(a));
        if (!isSortedByY(a) || !samePoints(small, a)) {
            System.out.println("   FAIL  expected: [(2, 1), (4, 1), (1, 2), (5, 4), (3, 5)]");
            return false;
        }
        for (int t = 0; t < 200; t++) {
            Point2D[] r = randomPoints(new Random(t), 2 + t % 25);
            Point2D[] b = r.clone();
            ConvexHullSorts.selectionSortByY(b);
            if (!isSortedByY(b) || !samePoints(r, b)) {
                System.out.println("   FAIL  on random input " + str(r));
                return false;
            }
        }
        System.out.println("   PASS  (lowest point p = " + a[0] + ")");
        return true;
    }

    // -------- Sort #2: insertion sort by polar angle --------
    static boolean testSort2() {
        System.out.println("\nSort #2  insertionSortByPolarAngle");
        Point2D p = new Point2D(0, 0);
        Point2D[] small = {
            p, new Point2D(-2, 2), new Point2D(3, 0), new Point2D(0, 4), new Point2D(1, 1), new Point2D(2, 2)
        };
        Point2D[] a = small.clone();
        ConvexHullSorts.insertionSortByPolarAngle(a, p);
        System.out.println("   input : " + str(small));
        System.out.println("   output: " + str(a));
        if (a[0] != p) {
            System.out.println("   FAIL  p was moved out of pts[0]!");
            return false;
        }
        if (!isSortedByAngle(a) || !samePoints(small, a)) {
            System.out.println("   FAIL  expected: [(0, 0), (3, 0), (1, 1), (2, 2), (0, 4), (-2, 2)]");
            return false;
        }
        for (int t = 0; t < 200; t++) {
            Point2D[] r = randomPoints(new Random(1000 + t), 3 + t % 25);
            ConvexHullSorts.selectionSortByY(r);
            Point2D[] b = r.clone();
            ConvexHullSorts.insertionSortByPolarAngle(b, b[0]);
            if (b[0] != r[0] || !isSortedByAngle(b) || !samePoints(r, b)) {
                System.out.println("   FAIL  on random input " + str(r));
                return false;
            }
        }
        System.out.println("   PASS");
        return true;
    }

    // -------- whole Graham scan --------
    static boolean testHull() {
        System.out.println("\nGraham scan using your sorts");
        Point2D[] square = {
            new Point2D(5, 5), new Point2D(0, 10), new Point2D(10, 0), new Point2D(3, 7),
            new Point2D(0, 0), new Point2D(10, 10), new Point2D(6, 2)
        };
        GrahamScan scan = new GrahamScan(square);
        Point2D[] hull = scan.hull();
        System.out.println("   hull of a square with 3 inside points: " + str(hull));
        String expected = "[(0, 0), (10, 0), (10, 10), (0, 10)]";
        if (!str(hull).equals(expected)) {
            System.out.println("   FAIL  expected: " + expected);
            return false;
        }
        System.out.println("   PASS");
        return true;
    }

    // -------- helpers --------
    static boolean isSortedByY(Point2D[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] == null || Point2D.compareByY(a[i - 1], a[i]) > 0) return false;
        return true;
    }

    static boolean isSortedByAngle(Point2D[] a) {
        for (int i = 2; i < a.length; i++)
            if (a[i] == null || Point2D.compareByPolarAngle(a[0], a[i - 1], a[i]) > 0) return false;
        return true;
    }

    static boolean samePoints(Point2D[] x, Point2D[] y) {
        if (x.length != y.length) return false;
        boolean[] used = new boolean[y.length];
        for (Point2D q : x) {
            boolean found = false;
            for (int j = 0; j < y.length; j++) {
                if (!used[j] && q.equals(y[j])) { used[j] = true; found = true; break; }
            }
            if (!found) return false;
        }
        return true;
    }

    static Point2D[] randomPoints(Random rnd, int n) {
        Point2D[] a = new Point2D[n];
        for (int i = 0; i < n; i++) a[i] = new Point2D(rnd.nextInt(20), rnd.nextInt(20));
        return a;
    }

    static String str(Point2D[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(a[i]);
        }
        return sb.append("]").toString();
    }
}
