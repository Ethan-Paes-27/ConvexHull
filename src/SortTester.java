import java.util.Random;

/**
 * SortTester  --  RUN THIS FILE to check your work, one part at a time.
 *
 *   1a  SortByY.compareByY
 *   1b  SortByY.selectionSortByY
 *   2a  SortByPolarAngle.compareByPolarAngle
 *   2b  SortByPolarAngle.insertionSortByPolarAngle
 *   3   the whole Graham scan using your methods
 *
 * The tester stops at the first part that fails, so fix them in order.
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 */
public class SortTester {

    public static void main(String[] args) {
        System.out.println("=== Convex Hull Lab Tester ===");
        boolean ok = test1a() && test1b() && test2a() && test2b() && testHull();
        System.out.println();
        if (ok) System.out.println("ALL TESTS PASSED! Now run HullVisualizer and step through the scan.");
        else    System.out.println("Fix the FAIL above, then run the tester again.");
    }

    // ---------------- 1a ----------------
    static boolean test1a() {
        System.out.println("\n1a  compareByY(a, b)");
        Object[][] cases = {
            // a, b, expected sign, reason
            {pt(3, 1), pt(2, 5), -1, "a is lower (1 < 5)"},
            {pt(2, 5), pt(3, 1), +1, "a is higher (5 > 1)"},
            {pt(4, 1), pt(2, 1), +1, "tie on y, a has bigger x"},
            {pt(2, 1), pt(4, 1), -1, "tie on y, a has smaller x"},
            {pt(2, 1), pt(2, 1),  0, "same point"},
            {pt(-5, -3), pt(9, -2), -1, "negative numbers: -3 < -2"},
        };
        for (Object[] c : cases) {
            Point2D a = (Point2D) c[0], b = (Point2D) c[1];
            int expected = (Integer) c[2];
            int got = SortByY.compareByY(a, b);
            if (Integer.signum(got) != expected) {
                System.out.println("   FAIL  compareByY(" + a + ", " + b + ") returned " + got
                        + ", expected " + word(expected) + "  [" + c[3] + "]");
                return false;
            }
        }
        System.out.println("   PASS");
        return true;
    }

    // ---------------- 1b ----------------
    static boolean test1b() {
        System.out.println("\n1b  selectionSortByY(pts)");
        Point2D[] small = { pt(3, 5), pt(1, 2), pt(4, 1), pt(2, 1), pt(5, 4) };
        Point2D[] a = small.clone();
        SortByY.selectionSortByY(a);
        System.out.println("   input   : " + str(small));
        System.out.println("   output  : " + str(a));
        if (!isSortedByY(a) || !samePoints(small, a)) {
            System.out.println("   FAIL  expected: [(2, 1), (4, 1), (1, 2), (5, 4), (3, 5)]");
            return false;
        }
        for (int t = 0; t < 200; t++) {
            Point2D[] r = randomPoints(new Random(t), 2 + t % 25);
            Point2D[] b = r.clone();
            SortByY.selectionSortByY(b);
            if (!isSortedByY(b) || !samePoints(r, b)) {
                System.out.println("   FAIL  on random input " + str(r));
                System.out.println("         your output     " + str(b));
                return false;
            }
        }
        System.out.println("   PASS  (start point p = " + a[0] + ")");
        return true;
    }

    // ---------------- 2a ----------------
    static boolean test2a() {
        System.out.println("\n2a  compareByPolarAngle(p, a, b)   with p = (0, 0)");
        Point2D p = pt(0, 0);
        Object[][] cases = {
            {pt(3, 0), pt(0, 4), -1, "0 degrees comes before 90 degrees (left turn)"},
            {pt(0, 4), pt(3, 0), +1, "90 degrees comes after 0 degrees (right turn)"},
            {pt(-2, 2), pt(1, 1), +1, "135 degrees comes after 45 degrees"},
            {pt(1, 1), pt(2, 2), -1, "same angle, a is closer to p"},
            {pt(4, 4), pt(2, 2), +1, "same angle, a is farther from p"},
            {pt(5, 1), pt(-5, 1), -1, "right side comes before left side"},
            {pt(2, 3), pt(2, 3),  0, "same point"},
        };
        for (Object[] c : cases) {
            Point2D a = (Point2D) c[0], b = (Point2D) c[1];
            int expected = (Integer) c[2];
            int got = SortByPolarAngle.compareByPolarAngle(p, a, b);
            if (Integer.signum(got) != expected) {
                System.out.println("   FAIL  compareByPolarAngle(p, " + a + ", " + b + ") returned " + got
                        + ", expected " + word(expected) + "  [" + c[3] + "]");
                return false;
            }
        }
        System.out.println("   PASS");
        return true;
    }

    // ---------------- 2b ----------------
    static boolean test2b() {
        System.out.println("\n2b  insertionSortByPolarAngle(pts, p)");
        Point2D p = pt(0, 0);
        Point2D[] small = { p, pt(-2, 2), pt(3, 0), pt(0, 4), pt(1, 1), pt(2, 2) };
        Point2D[] a = small.clone();
        SortByPolarAngle.insertionSortByPolarAngle(a, p);
        System.out.println("   input   : " + str(small));
        System.out.println("   output  : " + str(a));
        if (a[0] != p) {
            System.out.println("   FAIL  p was moved out of pts[0]! (start i at 2, keep j > 1)");
            return false;
        }
        if (!isSortedByAngle(a) || !samePoints(small, a)) {
            System.out.println("   FAIL  expected: [(0, 0), (3, 0), (1, 1), (2, 2), (0, 4), (-2, 2)]");
            return false;
        }
        for (int t = 0; t < 200; t++) {
            Point2D[] r = randomPoints(new Random(1000 + t), 3 + t % 25);
            sortByYReference(r);
            Point2D[] b = r.clone();
            SortByPolarAngle.insertionSortByPolarAngle(b, b[0]);
            if (b[0] != r[0] || !isSortedByAngle(b) || !samePoints(r, b)) {
                System.out.println("   FAIL  on random input " + str(r));
                System.out.println("         your output     " + str(b));
                return false;
            }
        }
        System.out.println("   PASS");
        return true;
    }

    // ---------------- 3 ----------------
    static boolean testHull() {
        System.out.println("\n3   Graham scan using your methods");
        Point2D[] square = { pt(5, 5), pt(0, 10), pt(10, 0), pt(3, 7), pt(0, 0), pt(10, 10), pt(6, 2) };
        GrahamScan scan = new GrahamScan(square);
        Point2D[] hull = scan.hull();
        System.out.println("   hull of a square with 3 points inside: " + str(hull));
        String expected = "[(0, 0), (10, 0), (10, 10), (0, 10)]";
        if (!str(hull).equals(expected)) {
            System.out.println("   FAIL  expected: " + expected);
            return false;
        }
        System.out.println("   PASS");
        return true;
    }

    // ---------------- helpers (independent of your code) ----------------
    static Point2D pt(double x, double y) { return new Point2D(x, y); }

    static String word(int sign) {
        return sign < 0 ? "a NEGATIVE number" : sign > 0 ? "a POSITIVE number" : "0";
    }

    static boolean isSortedByY(Point2D[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] == null || GrahamScan.checkY(a[i - 1], a[i]) > 0) return false;
        return true;
    }

    static boolean isSortedByAngle(Point2D[] a) {
        for (int i = 2; i < a.length; i++)
            if (a[i] == null || GrahamScan.checkAngle(a[0], a[i - 1], a[i]) > 0) return false;
        return true;
    }

    static void sortByYReference(Point2D[] a) {
        java.util.Arrays.sort(a, GrahamScan::checkY);
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
