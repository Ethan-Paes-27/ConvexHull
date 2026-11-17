CONVEX HULL LAB  --  Sorting Builds the Fence
=============================================

GOAL
  Write the two sorts that make the Graham scan work. Then watch your
  sorts build the convex hull (the "rubber band" around the points).

FILES
  ConvexHullSorts.java   <-- YOU EDIT THIS FILE (2 methods)
  Point2D.java           point class + compare rules + ccw() (given)
  GrahamScan.java        the Graham scan; records each decision (given)
  HullVisualizer.java    RUN THIS to watch the scan (given)
  SortTester.java        RUN THIS to check your sorts (given)

YOUR TASKS
  1. selectionSortByY(Point2D[] pts)
       Selection sort the points from lowest to highest y.
       Rule: Point2D.compareByY(a, b) < 0 means a comes first.
       After it runs, pts[0] is p, the START point of the fence.

  2. insertionSortByPolarAngle(Point2D[] pts, Point2D p)
       Insertion sort pts[1] .. pts[n-1] by angle around p.
       Rule: Point2D.compareByPolarAngle(p, a, b) < 0 means a comes first.
       Do NOT move p out of pts[0].

HOW TO CHECK YOUR WORK
  - Run SortTester  -> PASS / FAIL for each sort
  - Run HullVisualizer
        Step >   one decision at a time      < Back   undo a step
        Play     animate                      Random   new points
        Lecture  points from the slides       Clear    then click to add points
    Colors:  gray = not looked at yet
             green = on the path (fence)
             red = NOT on the path
    A red box at the top of the board means one of your sorts is not right yet.

WHEN YOU FINISH (answer on paper or in comments)
  a. Sort #1 only needs pts[0]. How could you change selection sort so it
     does LESS work and still finds p?
  b. Use Step to find a RIGHT turn. Why can't that middle point be on the fence?
  c. Click to add a point far outside the hull. Which red points might turn
     green? Which green points might turn red? Test it!
  d. Challenge: Insertion sort is about N^2/4 compares on random input.
     What sort from class would make the whole Graham scan run in N log N?
