package daa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Experiment {
    private static final int[] SIZES = {500, 2000, 8000};
    private static final int TRIALS = 3;
    private static final String[] INPUT_TYPES = {"random", "sorted", "reverse_sorted", "duplicate_heavy"};

    private Experiment() {
    }

    public static List<String[]> run(Path output) throws IOException {
        Files.createDirectories(output.getParent());
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"algorithm", "input_type", "n", "median_time_ns", "median_recursion_depth",
                "median_comparisons", "median_swaps", "median_recursive_calls"});
        StringBuilder console = new StringBuilder();
        console.append("Divide-and-Conquer Algorithm Experiment\n");
        console.append("Trials per case: ").append(TRIALS).append("\n");
        console.append(String.format(Locale.ROOT, "%-24s %-16s %8s %14s %10s %14s %10s%n",
                "Algorithm", "Input", "n", "Median ns", "Depth", "Comparisons", "Swaps"));

        for (int size : SIZES) {
            for (String inputType : INPUT_TYPES) {
                long seed = 20260927L + size * 31L + inputType.hashCode();
                int[] base = createArray(size, inputType, seed);
                measureSort("MergeSort", inputType, base, rows, console);
                measureSort("QuickSort", inputType, base, rows, console);
                measureSelect(inputType, base, rows, console);
                Point[] points = createPoints(size, inputType, seed);
                measureClosest(inputType, points, rows, console);
            }
        }

        StringBuilder csv = new StringBuilder();
        for (String[] row : rows) {
            csv.append(String.join(",", row)).append('\n');
        }
        Files.writeString(output, csv.toString());
        Files.writeString(Path.of("results", "experiment-output.txt"), console.toString());
        System.out.print(console);
        System.out.println("CSV saved to " + output.toString().replace('\\', '/'));
        return rows;
    }

    private static void measureSort(String algorithm, String inputType, int[] base,
                                    List<String[]> rows, StringBuilder console) {
        long[] times = new long[TRIALS];
        long[] comparisons = new long[TRIALS];
        long[] swaps = new long[TRIALS];
        long[] calls = new long[TRIALS];
        long[] depths = new long[TRIALS];
        for (int trial = 0; trial < TRIALS; trial++) {
            int[] values = base.clone();
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            long start = System.nanoTime();
            if (algorithm.equals("MergeSort")) {
                MergeSorter.sort(values, metrics);
            } else {
                QuickSorter.sort(values, metrics);
            }
            times[trial] = System.nanoTime() - start;
            comparisons[trial] = metrics.getComparisons();
            swaps[trial] = metrics.getSwaps();
            calls[trial] = metrics.getRecursiveCalls();
            depths[trial] = metrics.getMaximumRecursionDepth();
        }
        addRow(algorithm, inputType, base.length, times, depths, comparisons, swaps, calls, rows, console);
    }

    private static void measureSelect(String inputType, int[] base, List<String[]> rows, StringBuilder console) {
        long[] times = new long[TRIALS];
        long[] comparisons = new long[TRIALS];
        long[] swaps = new long[TRIALS];
        long[] calls = new long[TRIALS];
        long[] depths = new long[TRIALS];
        for (int trial = 0; trial < TRIALS; trial++) {
            int[] values = base.clone();
            int expected = Arrays.stream(base).sorted().skip(base.length / 2).findFirst().orElseThrow();
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            long start = System.nanoTime();
            int actual = DeterministicSelector.select(values, values.length / 2, metrics);
            times[trial] = System.nanoTime() - start;
            if (actual != expected) {
                throw new IllegalStateException("Selection result does not match the sorted reference");
            }
            comparisons[trial] = metrics.getComparisons();
            swaps[trial] = metrics.getSwaps();
            calls[trial] = metrics.getRecursiveCalls();
            depths[trial] = metrics.getMaximumRecursionDepth();
        }
        addRow("DeterministicSelect", inputType, base.length, times, depths, comparisons, swaps, calls, rows, console);
    }

    private static void measureClosest(String inputType, Point[] points, List<String[]> rows, StringBuilder console) {
        long[] times = new long[TRIALS];
        long[] comparisons = new long[TRIALS];
        long[] swaps = new long[TRIALS];
        long[] calls = new long[TRIALS];
        long[] depths = new long[TRIALS];
        for (int trial = 0; trial < TRIALS; trial++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            long start = System.nanoTime();
            ClosestPairSolver.solve(points, metrics);
            times[trial] = System.nanoTime() - start;
            comparisons[trial] = metrics.getComparisons();
            swaps[trial] = metrics.getSwaps();
            calls[trial] = metrics.getRecursiveCalls();
            depths[trial] = metrics.getMaximumRecursionDepth();
        }
        addRow("ClosestPair", inputType, points.length, times, depths, comparisons, swaps, calls, rows, console);
    }

    private static void addRow(String algorithm, String inputType, int size, long[] times, long[] depths,
                               long[] comparisons, long[] swaps, long[] calls, List<String[]> rows,
                               StringBuilder console) {
        long time = median(times);
        long depth = median(depths);
        long comparison = median(comparisons);
        long swap = median(swaps);
        long call = median(calls);
        rows.add(new String[]{algorithm, inputType, Integer.toString(size), Long.toString(time),
                Long.toString(depth), Long.toString(comparison), Long.toString(swap), Long.toString(call)});
        console.append(String.format(Locale.ROOT, "%-24s %-16s %8d %14d %10d %14d %10d%n",
                algorithm, inputType, size, time, depth, comparison, swap));
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    private static int[] createArray(int size, String inputType, long seed) {
        Random random = new Random(seed);
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = inputType.equals("duplicate_heavy") ? random.nextInt(11) - 5 : random.nextInt();
        }
        if (inputType.equals("sorted") || inputType.equals("reverse_sorted")) {
            Arrays.sort(values);
            if (inputType.equals("reverse_sorted")) {
                reverse(values);
            }
        }
        return values;
    }

    private static Point[] createPoints(int size, String inputType, long seed) {
        Random random = new Random(seed);
        Point[] points = new Point[size];
        for (int i = 0; i < size; i++) {
            if (inputType.equals("duplicate_heavy")) {
                points[i] = new Point(random.nextInt(11) - 5, random.nextInt(11) - 5);
            } else if (inputType.equals("sorted")) {
                points[i] = new Point(i, random.nextDouble() * size);
            } else if (inputType.equals("reverse_sorted")) {
                points[i] = new Point(size - i, random.nextDouble() * size);
            } else {
                points[i] = new Point(random.nextDouble() * size, random.nextDouble() * size);
            }
        }
        return points;
    }

    private static void reverse(int[] values) {
        for (int left = 0, right = values.length - 1; left < right; left++, right--) {
            int value = values[left];
            values[left] = values[right];
            values[right] = value;
        }
    }
}
