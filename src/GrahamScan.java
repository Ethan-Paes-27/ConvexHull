import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GrahamScan  --  runs YOUR two sorts, then the Graham scan, and records every
 * decision as a "Step" so the visualizer can replay it.
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 *  (But read buildScanSteps() -- it is the Graham scan from lecture!)
 */
public class GrahamScan {

    // Color state of each point
    public static final int UNSEEN   = 0;   // gray : not looked at yet
    public static final int ON_PATH  = 1;   // green: on the fence (so far)
    public static final int REJECTED = 2;   // red  : NOT on the fence

    /** One frame of the animation. */
    public static class Step {
        public String title;
        public String detail;
        public int[] status;                 // status of each sorted point
        public int[] stack;                  // indices into sorted points, bottom -> top
        public int a = -1, b = -1, c = -1;   // the turn a -> b -> c being tested
        public boolean turnIsCCW;
        public boolean showStart;            // highlight p
        public boolean showFan;              // draw rays from p (sort #2 picture)
        public boolean showOrder;            // label points 1, 2, 3, ...
        public boolean done;                 // close the hull
    }

    private final Point2D[] pts;             // points after YOUR sorts
    private final List<Step> steps = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private int[] status;
    private int[] stack;
    private int top;                         // number of items on the stack

    public GrahamScan(Point2D[] input) {
        pts = input.clone();
        int n = pts.length;
        status = new int[n];
        stack = new int[Math.max(n, 1)];

        addStep("Ready", n + " points. Press Step (or Play) to begin.", false, false, false);
        if (n < 3) {
            addStep("Need more points", "Add at least 3 points (click on the board).", false, false, false);
            return;
        }

        // ---------- SORT #1 : find the start point ----------
        ConvexHullSorts.selectionSortByY(pts);
        checkSort1(input);
        Point2D p = pts[0];
        status[0] = ON_PATH;
        addStep("Sort #1: selection sort by y",
                "After Sort #1, pts[0] = p = " + p + ". The lowest point is ALWAYS on the fence, so it turns green.",
                true, false, false);

        // ---------- SORT #2 : order the rest by polar angle ----------
        ConvexHullSorts.insertionSortByPolarAngle(pts, p);
        checkSort2(input);
        addStep("Sort #2: insertion sort by polar angle",
                "Points are numbered 1.." + (n - 1) + " by angle around p, sweeping counterclockwise like a radar.",
                true, true, true);

        buildScanSteps();
    }

    /** The Graham scan itself (from lecture). */
    private void buildScanSteps() {
        int n = pts.length;

        push(0);
        push(1);
        status[1] = ON_PATH;
        addStep("Start the fence",
                "Push p and point 1 onto the stack. Point 1 has the smallest angle, so it is on the fence.",
                true, false, true);

        for (int i = 2; i < n; i++) {
            status[i] = ON_PATH;   // tentatively on the fence
            while (true) {
                if (top < 2) {
                    push(i);
                    addStep("Consider point " + i,
                            "Only p is on the stack, so push point " + i + ".", true, false, true);
                    break;
                }
                int a = stack[top - 2];
                int b = stack[top - 1];
                int turn = Point2D.ccw(pts[a], pts[b], pts[i]);
                if (turn > 0) {
                    push(i);
                    Step s = addStep("Consider point " + i + ": " + name(a) + " -> " + name(b) + " -> " + name(i),
                            "LEFT turn (counterclockwise). Keep " + name(b) + " and push " + name(i) + " onto the fence.",
                            true, false, true);
                    s.a = a; s.b = b; s.c = i; s.turnIsCCW = true;
                    break;
                } else {
                    top--;                      // pop b
                    status[b] = REJECTED;
                    String why = (turn < 0) ? "RIGHT turn (clockwise)" : "No turn (the three points are in a straight line)";
                    Step s = addStep("Consider point " + i + ": " + name(a) + " -> " + name(b) + " -> " + name(i),
                            why + ". Point " + name(b) + " would make a dent in the fence, so pop it. It turns red.",
                            true, false, true);
                    s.a = a; s.b = b; s.c = i; s.turnIsCCW = false;
                }
            }
        }

        Step last = addStep("Done!",
                "The convex hull has " + top + " vertices (green). " + (n - top) + " points are inside or not needed (red).",
                true, false, true);
        last.done = true;
    }

    // ---------------- sort checks (feedback for students) ----------------

    private void checkSort1(Point2D[] original) {
        if (!sameSet(original, pts)) {
            warnings.add("Sort #1 lost or duplicated points -- only use exch() to move points.");
            return;
        }
        for (int i = 1; i < pts.length; i++) {
            if (Point2D.compareByY(pts[i], pts[0]) < 0) {
                warnings.add("Sort #1 (selectionSortByY) is not finished: pts[0] is not the lowest point.");
                return;
            }
        }
        for (int i = 1; i < pts.length; i++) {
            if (Point2D.compareByY(pts[i - 1], pts[i]) > 0) {
                warnings.add("Sort #1 found p, but the array is not fully sorted by y (index " + i + ").");
                return;
            }
        }
    }

    private void checkSort2(Point2D[] original) {
        if (!sameSet(original, pts)) {
            warnings.add("Sort #2 lost or duplicated points -- only use exch() to move points.");
            return;
        }
        Point2D lowest = original[0];
        for (Point2D q : original) if (Point2D.compareByY(q, lowest) < 0) lowest = q;
        boolean sort1Worked = warnings.isEmpty();
        if (sort1Worked && !pts[0].equals(lowest)) {
            warnings.add("Sort #2 moved p out of pts[0]! Start your loops at index 1 or 2.");
            return;
        }
        for (int i = 2; i < pts.length; i++) {
            if (Point2D.compareByPolarAngle(pts[0], pts[i - 1], pts[i]) > 0) {
                warnings.add("Sort #2 (insertionSortByPolarAngle) is not working: points " + (i - 1) + " and " + i + " are out of order.");
                return;
            }
        }
    }

    private static boolean sameSet(Point2D[] a, Point2D[] b) {
        if (a.length != b.length) return false;
        Point2D[] x = a.clone(), y = b.clone();
        for (Point2D q : y) if (q == null) return false;
        Arrays.sort(x, Point2D::compareByY);
        Arrays.sort(y, Point2D::compareByY);
        for (int i = 0; i < x.length; i++) if (!x[i].equals(y[i])) return false;
        return true;
    }

    // ---------------- helpers ----------------

    private void push(int i) { stack[top++] = i; }

    private static String name(int i) { return i == 0 ? "p" : String.valueOf(i); }

    private Step addStep(String title, String detail, boolean showStart, boolean showFan, boolean showOrder) {
        Step s = new Step();
        s.title = title;
        s.detail = detail;
        s.status = status.clone();
        s.stack = Arrays.copyOf(stack, top);
        s.showStart = showStart;
        s.showFan = showFan;
        s.showOrder = showOrder;
        steps.add(s);
        return s;
    }

    public Point2D[] points()      { return pts; }
    public List<Step> steps()      { return steps; }
    public List<String> warnings() { return warnings; }

    /** The hull vertices in counterclockwise order (after the scan). */
    public Point2D[] hull() {
        Step last = steps.get(steps.size() - 1);
        Point2D[] h = new Point2D[last.stack.length];
        for (int i = 0; i < h.length; i++) h[i] = pts[last.stack[i]];
        return h;
    }
}
