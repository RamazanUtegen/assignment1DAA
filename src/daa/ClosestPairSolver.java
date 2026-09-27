package daa;

import java.util.Arrays;
import java.util.Comparator;

public final class ClosestPairSolver {
    private static final int BRUTE_FORCE_CUTOFF = 3;
    private static final Comparator<IndexedPoint> BY_X = Comparator
            .comparingDouble((IndexedPoint point) -> point.point.x())
            .thenComparingDouble(point -> point.point.y())
            .thenComparingInt(point -> point.rank);
    private static final Comparator<IndexedPoint> BY_Y = Comparator
            .comparingDouble((IndexedPoint point) -> point.point.y())
            .thenComparingDouble(point -> point.point.x())
            .thenComparingInt(point -> point.rank);

    private ClosestPairSolver() {
    }

    public static ClosestPairResult solve(Point[] points) {
        return solve(points, new AlgorithmMetrics());
    }

    public static ClosestPairResult solve(Point[] points, AlgorithmMetrics metrics) {
        if (points == null) {
            throw new IllegalArgumentException("Points must not be null");
        }
        if (points.length < 2) {
            throw new IllegalArgumentException("At least two points are required");
        }
        metrics.reset();
        IndexedPoint[] byX = new IndexedPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Points must not contain null values");
            }
            byX[i] = new IndexedPoint(points[i], i);
        }
        Arrays.sort(byX, BY_X);
        for (int i = 0; i < byX.length; i++) {
            byX[i].rank = i;
        }
        IndexedPoint[] byY = byX.clone();
        Arrays.sort(byY, BY_Y);
        return solve(byX, byY, 0, points.length, 1, metrics);
    }

    private static ClosestPairResult solve(IndexedPoint[] byX, IndexedPoint[] byY, int low, int high,
                                           int depth, AlgorithmMetrics metrics) {
        metrics.recursiveCall(depth);
        int size = high - low;
        if (size <= BRUTE_FORCE_CUTOFF) {
            return bruteForce(byY, 0, byY.length, metrics);
        }

        int middle = low + size / 2;
        double middleX = byX[middle].point.x();
        IndexedPoint[] leftByY = new IndexedPoint[middle - low];
        IndexedPoint[] rightByY = new IndexedPoint[high - middle];
        int leftCount = 0;
        int rightCount = 0;
        for (int i = 0; i < byY.length; i++) {
            if (byY[i].rank < middle) {
                leftByY[leftCount++] = byY[i];
            } else {
                rightByY[rightCount++] = byY[i];
            }
        }

        ClosestPairResult left = solve(byX, leftByY, low, middle, depth + 1, metrics);
        ClosestPairResult right = solve(byX, rightByY, middle, high, depth + 1, metrics);
        ClosestPairResult best = left.distance() <= right.distance() ? left : right;
        double bestSquared = best.distance() * best.distance();

        IndexedPoint[] strip = new IndexedPoint[size];
        int stripCount = 0;
        for (IndexedPoint point : byY) {
            double dx = point.point.x() - middleX;
            if (dx * dx < bestSquared) {
                strip[stripCount++] = point;
            }
        }
        for (int i = 0; i < stripCount; i++) {
            int limit = Math.min(i + 8, stripCount);
            for (int j = i + 1; j < limit; j++) {
                double dy = strip[j].point.y() - strip[i].point.y();
                if (dy * dy >= bestSquared) {
                    break;
                }
                metrics.comparison();
                double distance = distance(strip[i].point, strip[j].point);
                if (distance < best.distance()) {
                    best = new ClosestPairResult(strip[i].point, strip[j].point, distance);
                    bestSquared = distance * distance;
                }
            }
        }
        return best;
    }

    private static ClosestPairResult bruteForce(IndexedPoint[] points, int low, int high,
                                                 AlgorithmMetrics metrics) {
        ClosestPairResult best = null;
        for (int i = low; i < high; i++) {
            for (int j = i + 1; j < high; j++) {
                metrics.comparison();
                double distance = distance(points[i].point, points[j].point);
                if (best == null || distance < best.distance()) {
                    best = new ClosestPairResult(points[i].point, points[j].point, distance);
                }
            }
        }
        return best;
    }

    private static double distance(Point first, Point second) {
        return Math.hypot(first.x() - second.x(), first.y() - second.y());
    }

    private static final class IndexedPoint {
        private final Point point;
        private int rank;

        private IndexedPoint(Point point, int rank) {
            this.point = point;
            this.rank = rank;
        }
    }
}
