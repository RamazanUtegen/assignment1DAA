package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

class ClosestPairSolverTest {
    @Test
    void matchesBruteForceForRandomSmallDatasets() {
        Random random = new Random(9921);
        for (int trial = 0; trial < 120; trial++) {
            Point[] points = new Point[2 + random.nextInt(98)];
            for (int i = 0; i < points.length; i++) {
                points[i] = new Point(random.nextInt(200) - 100, random.nextInt(200) - 100);
            }
            assertEquals(bruteForce(points), ClosestPairSolver.solve(points).distance(), 1e-9);
        }
    }

    @Test
    void matchesBruteForceAtTheTwoThousandPointLimit() {
        Random random = new Random(7781);
        Point[] points = new Point[2000];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(random.nextDouble() * 10000, random.nextDouble() * 10000);
        }
        assertEquals(bruteForce(points), ClosestPairSolver.solve(points).distance(), 1e-9);
    }

    @Test
    void handlesRepeatedCoordinatesAndVerticalLines() {
        Point[] repeated = {new Point(2, 3), new Point(2, 3), new Point(-1, 4), new Point(5, 6)};
        assertEquals(0.0, ClosestPairSolver.solve(repeated).distance());
        Point[] vertical = {new Point(4, -3), new Point(4, 2), new Point(4, 0), new Point(4, 9)};
        assertEquals(2.0, ClosestPairSolver.solve(vertical).distance());
    }

    @Test
    void rejectsFewerThanTwoPointsAndNullValues() {
        assertThrows(IllegalArgumentException.class, () -> ClosestPairSolver.solve(new Point[0]));
        assertThrows(IllegalArgumentException.class, () -> ClosestPairSolver.solve(new Point[]{new Point(0, 0)}));
        assertThrows(IllegalArgumentException.class, () -> ClosestPairSolver.solve(new Point[]{new Point(0, 0), null}));
    }

    private double bruteForce(Point[] points) {
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                best = Math.min(best, Math.hypot(points[i].x() - points[j].x(), points[i].y() - points[j].y()));
            }
        }
        return best;
    }
}
