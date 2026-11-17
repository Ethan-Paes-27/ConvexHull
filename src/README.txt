CONVEX HULL LAB  --  Sorting Builds the Fence
=============================================

GOAL
  Write the two compare RULES and the two SORTS that make the Graham scan
  work. Then watch your code build the convex hull (the "rubber band"
  around the points).

FILES
  SortByY.java           <-- YOU EDIT (Part 1: compareByY + selection sort)
  SortByPolarAngle.java  <-- YOU EDIT (Part 2: compareByPolarAngle + insertion sort)
  Point2D.java           point class + ccw() + distanceSquaredTo() (given)
  GrahamScan.java        the Graham scan; records each decision (given)
  HullVisualizer.java    RUN THIS to watch the scan (given)
  SortTester.java        RUN THIS to check your work (given)

YOUR TASKS  (do them in order -- each file's comments walk you through it)
  1a. SortByY.compareByY(a, b)
        Lower y comes first. Tie on y? Smaller x comes first.
        Return negative (a first), positive (b first), or 0 (same point).
  1b. SortByY.selectionSortByY(pts)
        Selection sort using compareByY. Afterward pts[0] is p, the start.

  2a. SortByPolarAngle.compareByPolarAngle(p, a, b)
        Use Point2D.ccw(p, a, b):
          left turn  -> a first (-1)
          right turn -> b first (+1)
          straight   -> the point closer to p first
  2b. SortByPolarAngle.insertionSortByPolarAngle(pts, p)
        Insertion sort pts[1..n-1] using compareByPolarAngle.
        Never move p out of pts[0].

HOW TO CHECK YOUR WORK
  - Run SortTester  -> tests 1a, 1b, 2a, 2b, then the whole scan.
                       It stops at the first FAIL and tells you why.
  - Run HullVisualizer
        Step >   one decision at a time      < Back   undo a step
        Play     animate                      Random   new points
        Lecture  points from the slides       Clear    then click to add points
    Colors:  gray = not looked at yet
             green = on the path (fence)
             red = NOT on the path
    A red box at the top of the board names the part that isn't right yet.

WHEN YOU FINISH (answer on paper or in comments)
  a. Part 1 only needs pts[0]. How could you change selection sort so it
     does LESS work and still finds p?
  b. Use Step to find a RIGHT turn. Why can't that middle point be on the fence?
  c. Click to add a point far outside the hull. Which red points might turn
     green? Which green points might turn red? Test it!
  d. Why does compareByPolarAngle only work when p is the LOWEST point?
     (Hint: what happens to "left turn = bigger angle" past 180 degrees?)
  e. Challenge: insertion sort is about N^2/4 compares on random input.
     What sort from class would make the whole Graham scan run in N log N?
