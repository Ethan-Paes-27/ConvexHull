import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HullVisualizer  --  RUN THIS FILE to watch the Graham scan.
 *
 *  *** YOU DO NOT NEED TO CHANGE THIS FILE. ***
 *
 * Controls
 *   Step / Back   move one decision forward or backward
 *   Play          animate all the steps
 *   Restart       go back to the first step
 *   Random        new random points
 *   Lecture       the 13 points from the lecture slides
 *   Clear         remove all points (then click to add your own)
 *   Click         add a point where you click
 *
 * Colors
 *   gray   not looked at yet
 *   green  on the path (the fence)
 *   red    NOT on the path
 */
public class HullVisualizer extends JFrame {

    static final double WORLD_W = 1000, WORLD_H = 700;

    private final HullPanel board = new HullPanel();
    private final JLabel stepLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JTextArea detailArea = new JTextArea();
    private final JTextArea stackArea = new JTextArea();
    private final JButton playButton = new JButton("Play");
    private final Timer timer;
    private final Random random = new Random();

    public HullVisualizer() {
        super("Convex Hull - Graham Scan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- side panel -----
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        side.setPreferredSize(new Dimension(320, 600));

        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        stepLabel.setForeground(Color.GRAY);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        styleText(detailArea, 15);
        styleText(stackArea, 14);
        stackArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        side.add(left(stepLabel));
        side.add(Box.createVerticalStrut(4));
        side.add(left(titleLabel));
        side.add(Box.createVerticalStrut(8));
        side.add(left(detailArea));
        side.add(Box.createVerticalStrut(16));
        JLabel stackTitle = new JLabel("Stack (bottom -> top)");
        stackTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        side.add(left(stackTitle));
        side.add(left(stackArea));
        side.add(Box.createVerticalGlue());
        side.add(left(new LegendPanel()));

        // ----- buttons -----
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton back = new JButton("< Back");
        JButton step = new JButton("Step >");
        JButton restart = new JButton("Restart");
        JButton rand = new JButton("Random");
        JButton lecture = new JButton("Lecture");
        JButton clear = new JButton("Clear");
        for (JButton b : new JButton[]{back, step, playButton, restart, rand, lecture, clear}) {
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusable(false);
            controls.add(b);
        }
        JSlider speed = new JSlider(100, 1500, 700);
        speed.setInverted(true);
        speed.setPreferredSize(new Dimension(120, 30));
        controls.add(new JLabel("  speed"));
        controls.add(speed);

        timer = new Timer(700, e -> {
            if (!board.stepForward()) stopPlaying();
            refresh();
        });
        speed.addChangeListener(e -> timer.setDelay(speed.getValue()));

        step.addActionListener(e -> { stopPlaying(); board.stepForward(); refresh(); });
        back.addActionListener(e -> { stopPlaying(); board.stepBack(); refresh(); });
        restart.addActionListener(e -> { stopPlaying(); board.restart(); refresh(); });
        playButton.addActionListener(e -> {
            if (timer.isRunning()) stopPlaying();
            else {
                if (board.atEnd()) board.restart();
                timer.start();
                playButton.setText("Pause");
            }
            refresh();
        });
        rand.addActionListener(e -> { stopPlaying(); board.setPoints(randomPoints(14)); refresh(); });
        lecture.addActionListener(e -> { stopPlaying(); board.setPoints(lecturePoints()); refresh(); });
        clear.addActionListener(e -> { stopPlaying(); board.setPoints(new ArrayList<>()); refresh(); });
        board.onChange = this::refresh;

        add(board, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);
        add(controls, BorderLayout.SOUTH);

        board.setPoints(lecturePoints());
        refresh();
        pack();
        setLocationRelativeTo(null);
    }

    private void stopPlaying() {
        timer.stop();
        playButton.setText("Play");
    }

    private void refresh() {
        GrahamScan.Step s = board.currentStep();
        stepLabel.setText("Step " + (board.stepIndex() + 1) + " of " + board.stepCount());
        titleLabel.setText("<html><body style='width:230px'>" + s.title + "</body></html>");
        detailArea.setText(s.detail);
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < s.stack.length; k++) {
            int idx = s.stack[k];
            sb.append(idx == 0 ? "p" : String.valueOf(idx));
            sb.append("  ").append(board.scan().points()[idx]);
            if (k == s.stack.length - 1) sb.append("   <- top");
            sb.append('\n');
        }
        if (s.stack.length == 0) sb.append("(empty)");
        stackArea.setText(sb.toString());
        board.repaint();
    }

    // ---------------- point sets ----------------

    List<Point2D> randomPoints(int n) {
        List<Point2D> list = new ArrayList<>();
        while (list.size() < n) {
            Point2D q = new Point2D(80 + random.nextInt(840), 70 + random.nextInt(560));
            if (!list.contains(q)) list.add(q);
        }
        return list;
    }

    /** The 13 points from the "Graham scan demo" lecture slide. */
    static List<Point2D> lecturePoints() {
        int[][] xy = {
            {771, 736}, {771, 370}, {659, 394}, {698, 587}, {567, 393}, {459, 299}, {567, 550},
            {497, 548}, {351, 493}, {289, 551}, {379, 608}, {602, 700}, {389, 690}
        };
        List<Point2D> list = new ArrayList<>();
        // shuffle-ish order so the sorts have real work to do
        int[] order = {6, 0, 9, 3, 12, 1, 8, 4, 11, 2, 10, 5, 7};
        for (int i : order) {
            double x = (xy[i][0] - 289) * 1.45 + 150;
            double y = (736 - xy[i][1]) * 1.35 + 60;
            list.add(new Point2D(Math.round(x), Math.round(y)));
        }
        return list;
    }

    // ---------------- small Swing helpers ----------------

    private static void styleText(JTextArea t, int size) {
        t.setFont(new Font("SansSerif", Font.PLAIN, size));
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setEditable(false);
        t.setOpaque(false);
        t.setFocusable(false);
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (c instanceof JTextArea) c.setMaximumSize(new Dimension(300, 400));
        return c;
    }

    /** Color key shown in the side panel. */
    static class LegendPanel extends JPanel {
        LegendPanel() {
            setPreferredSize(new Dimension(296, 120));
            setMaximumSize(new Dimension(296, 120));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            Object[][] rows = {
                {HullPanel.GRAY, "not looked at yet"},
                {HullPanel.GREEN, "on the path (fence)"},
                {HullPanel.RED, "NOT on the path"},
                {HullPanel.GOLD, "point being considered"}
            };
            for (int i = 0; i < rows.length; i++) {
                int y = 14 + i * 26;
                g.setColor((Color) rows[i][0]);
                if (i == 3) {
                    g.setStroke(new BasicStroke(3f));
                    g.drawOval(4, y - 10, 18, 18);
                } else {
                    g.fillOval(6, y - 8, 14, 14);
                }
                g.setColor(Color.DARK_GRAY);
                g.drawString((String) rows[i][1], 32, y + 4);
            }
        }
    }

    // =====================================================================
    //  The drawing board
    // =====================================================================
    static class HullPanel extends JPanel {
        static final Color GRAY  = new Color(150, 150, 150);
        static final Color GREEN = new Color(30, 160, 70);
        static final Color RED   = new Color(215, 45, 45);
        static final Color GOLD  = new Color(235, 170, 0);
        static final Color FAN   = new Color(200, 200, 210);
        static final Color BG    = new Color(250, 250, 247);

        private List<Point2D> points = new ArrayList<>();
        private GrahamScan scan;
        private int stepIndex;
        Runnable onChange = () -> { };

        HullPanel() {
            setPreferredSize(new Dimension(900, 640));
            setBackground(BG);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Point2D q = toWorld(e.getX(), e.getY());
                    if (q.x() < 0 || q.x() > WORLD_W || q.y() < 0 || q.y() > WORLD_H) return;
                    if (points.contains(q)) return;
                    List<Point2D> copy = new ArrayList<>(points);
                    copy.add(q);
                    setPoints(copy);
                    onChange.run();
                }
            });
        }

        void setPoints(List<Point2D> pts) {
            points = new ArrayList<>(pts);
            scan = new GrahamScan(points.toArray(new Point2D[0]));
            stepIndex = 0;
        }

        GrahamScan scan()              { return scan; }
        GrahamScan.Step currentStep()  { return scan.steps().get(stepIndex); }
        int stepIndex()                { return stepIndex; }
        int stepCount()                { return scan.steps().size(); }
        boolean atEnd()                { return stepIndex == stepCount() - 1; }
        void restart()                 { stepIndex = 0; }
        void goToStep(int i)           { stepIndex = Math.max(0, Math.min(i, stepCount() - 1)); }
        void stepBack()                { if (stepIndex > 0) stepIndex--; }
        boolean stepForward() {
            if (atEnd()) return false;
            stepIndex++;
            return true;
        }

        // ----- world <-> screen (world y points UP, screen y points DOWN) -----
        private double scale() {
            return Math.min((getWidth() - 40) / WORLD_W, (getHeight() - 40) / WORLD_H);
        }
        private double offX() { return (getWidth() - WORLD_W * scale()) / 2; }
        private double offY() { return (getHeight() - WORLD_H * scale()) / 2; }
        double sx(Point2D q) { return offX() + q.x() * scale(); }
        double sy(Point2D q) { return offY() + (WORLD_H - q.y()) * scale(); }
        Point2D toWorld(int px, int py) {
            double x = (px - offX()) / scale();
            double y = WORLD_H - (py - offY()) / scale();
            return new Point2D(Math.round(x), Math.round(y));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // board outline
            g.setColor(Color.WHITE);
            g.fillRect((int) offX(), (int) offY(), (int) (WORLD_W * scale()), (int) (WORLD_H * scale()));
            g.setColor(new Color(225, 225, 225));
            g.drawRect((int) offX(), (int) offY(), (int) (WORLD_W * scale()), (int) (WORLD_H * scale()));

            GrahamScan.Step s = currentStep();
            Point2D[] pts = (stepIndex == 0) ? points.toArray(new Point2D[0]) : scan.points();
            int n = pts.length;

            if (n == 0) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("SansSerif", Font.PLAIN, 20));
                g.drawString("Click anywhere to add points", (int) (offX() + 320 * scale()), (int) (offY() + 350 * scale()));
            }

            // fan of rays from p (Sort #2 picture)
            if (s.showFan && n > 0) {
                g.setColor(FAN);
                g.setStroke(new BasicStroke(1.2f));
                for (int i = 1; i < n; i++) g.draw(line(pts[0], pts[i]));
            }

            // fence so far: edges along the stack
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(GREEN);
            for (int k = 1; k < s.stack.length; k++) {
                g.draw(line(pts[s.stack[k - 1]], pts[s.stack[k]]));
            }
            if (s.done && s.stack.length > 1) {
                g.draw(line(pts[s.stack[s.stack.length - 1]], pts[s.stack[0]]));
            }

            // the turn being tested: a -> b -> c
            if (s.c >= 0) {
                Color turnColor = s.turnIsCCW ? GREEN : RED;
                float[] dash = {10f, 8f};
                g.setColor(turnColor);
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                g.draw(line(pts[s.a], pts[s.b]));
                g.draw(line(pts[s.b], pts[s.c]));
                drawTurnBadge(g, pts[s.b], s.turnIsCCW);
            }

            // points
            for (int i = 0; i < n; i++) {
                Color c = GRAY;
                if (stepIndex > 0) {
                    if (s.status[i] == GrahamScan.ON_PATH)  c = GREEN;
                    if (s.status[i] == GrahamScan.REJECTED) c = RED;
                    if (s.done && s.status[i] != GrahamScan.ON_PATH) c = RED;
                }
                double r = 7;
                g.setColor(c);
                g.fill(new Ellipse2D.Double(sx(pts[i]) - r, sy(pts[i]) - r, 2 * r, 2 * r));
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Ellipse2D.Double(sx(pts[i]) - r, sy(pts[i]) - r, 2 * r, 2 * r));
            }

            // highlight the candidate point c
            if (s.c >= 0) {
                g.setColor(GOLD);
                g.setStroke(new BasicStroke(3f));
                double r = 14;
                g.draw(new Ellipse2D.Double(sx(pts[s.c]) - r, sy(pts[s.c]) - r, 2 * r, 2 * r));
            }

            // labels
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            if (s.showOrder) {
                for (int i = 1; i < n; i++) label(g, pts[i], String.valueOf(i), Color.DARK_GRAY);
            }
            if (s.showStart && n > 0) {
                g.setColor(GOLD.darker());
                g.setStroke(new BasicStroke(2.5f));
                double r = 13;
                g.draw(new Ellipse2D.Double(sx(pts[0]) - r, sy(pts[0]) - r, 2 * r, 2 * r));
                label(g, pts[0], "p", new Color(160, 100, 0));
            }

            // warnings from the sort checks
            List<String> warn = scan.warnings();
            if (stepIndex > 0 && !warn.isEmpty()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 15));
                int y = 28;
                for (String w : warn) {
                    int width = g.getFontMetrics().stringWidth(w) + 24;
                    g.setColor(new Color(255, 235, 235));
                    g.fillRoundRect(14, y - 19, width, 28, 10, 10);
                    g.setColor(RED);
                    g.drawRoundRect(14, y - 19, width, 28, 10, 10);
                    g.drawString(w, 26, y);
                    y += 36;
                }
            }
        }

        private void drawTurnBadge(Graphics2D g, Point2D b, boolean ccw) {
            String text = ccw ? "LEFT turn: keep" : "RIGHT turn: pop";
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            int w = g.getFontMetrics().stringWidth(text) + 14;
            int x = (int) sx(b) + 16, y = (int) sy(b) - 36;
            g.setColor(ccw ? new Color(225, 245, 230) : new Color(255, 228, 228));
            g.fillRoundRect(x, y, w, 22, 8, 8);
            g.setColor(ccw ? GREEN : RED);
            g.drawString(text, x + 7, y + 16);
        }

        private void label(Graphics2D g, Point2D q, String text, Color color) {
            g.setColor(color);
            g.drawString(text, (int) sx(q) + 10, (int) sy(q) + 22);
        }

        private Line2D line(Point2D a, Point2D b) {
            return new Line2D.Double(sx(a), sy(a), sx(b), sy(b));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HullVisualizer().setVisible(true));
    }
}
