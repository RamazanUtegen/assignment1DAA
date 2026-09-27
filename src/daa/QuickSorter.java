package daa;

import java.util.concurrent.ThreadLocalRandom;

public final class QuickSorter {
    private QuickSorter() {
    }

    public static void sort(int[] values) {
        sort(values, new AlgorithmMetrics());
    }

    public static void sort(int[] values, AlgorithmMetrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("Values must not be null");
        }
        metrics.reset();
        if (values.length > 1) {
            sort(values, 0, values.length - 1, 1, metrics);
        }
    }

    private static void sort(int[] values, int low, int high, int depth, AlgorithmMetrics metrics) {
        metrics.recursiveCall(depth);
        while (low < high) {
            int pivotIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
            int pivot = values[pivotIndex];
            int less = low;
            int current = low;
            int greater = high;
            while (current <= greater) {
                metrics.comparison();
                if (values[current] < pivot) {
                    swap(values, less++, current++, metrics);
                } else {
                    metrics.comparison();
                    if (values[current] > pivot) {
                        swap(values, current, greater--, metrics);
                    } else {
                        current++;
                    }
                }
            }
            int leftSize = less - low;
            int rightSize = high - greater;
            if (leftSize < rightSize) {
                if (low < less - 1) {
                    sort(values, low, less - 1, depth + 1, metrics);
                }
                low = greater + 1;
            } else {
                if (greater + 1 < high) {
                    sort(values, greater + 1, high, depth + 1, metrics);
                }
                high = less - 1;
            }
        }
    }

    private static void swap(int[] values, int first, int second, AlgorithmMetrics metrics) {
        if (first != second) {
            int value = values[first];
            values[first] = values[second];
            values[second] = value;
            metrics.swap();
        }
    }
}
