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
        IndexedPoint[] buffer = new IndexedPoint[points.length];
        return solve(byX, byY, buffer, 0, points.length, 1, metrics);
    }

    private static ClosestPairResult solve(IndexedPoint[] byX, IndexedPoint[] byY,
                                           IndexedPoint[] buffer, int low, int high,
                                           int depth, AlgorithmMetrics metrics) {
        metrics.recursiveCall(depth);
        int size = high - low;
        if (size <= BRUTE_FORCE_CUTOFF) {
            return bruteForce(byY, low, high, metrics);
        }

        int middle = low + size / 2;
        double middleX = byX[middle].point.x();
        int leftCount = 0;
        for (int i = low; i < high; i++) {
            if (byY[i].rank < middle) {
                buffer[low + leftCount++] = byY[i];
            }
        }
        int leftEnd = low + leftCount;
        int rightIndex = leftEnd;
        for (int i = low; i < high; i++) {
            if (byY[i].rank >= middle) {
                buffer[rightIndex++] = byY[i];
            }
        }
        System.arraycopy(buffer, low, byY, low, size);

        ClosestPairResult left = solve(byX, byY, buffer, low, middle, depth + 1, metrics);
        ClosestPairResult right = solve(byX, byY, buffer, middle, high, depth + 1, metrics);
        ClosestPairResult best = left.distance() <= right.distance() ? left : right;
        double bestSquared = best.distance() * best.distance();

        int stripCount = 0;
        for (int i = low; i < high; i++) {
            double dx = byY[i].point.x() - middleX;
            if (dx * dx < bestSquared) {
                buffer[low + stripCount++] = byY[i];
            }
        }
        for (int i = 0; i < stripCount; i++) {
            int limit = Math.min(i + 8, stripCount);
            for (int j = i + 1; j < limit; j++) {
                double dy = buffer[low + j].point.y() - buffer[low + i].point.y();
                if (dy * dy >= bestSquared) {
                    break;
                }
                metrics.comparison();
                double distance = distance(buffer[low + i].point, buffer[low + j].point);
                if (distance < best.distance()) {
                    best = new ClosestPairResult(buffer[low + i].point, buffer[low + j].point, distance);
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
