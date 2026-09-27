package daa;

public final class DeterministicSelector {
    private DeterministicSelector() {
    }

    public static int select(int[] values, int k) {
        return select(values, k, new AlgorithmMetrics());
    }

    public static int select(int[] values, int k, AlgorithmMetrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("Values must not be null");
        }
        if (k < 0 || k >= values.length) {
            throw new IndexOutOfBoundsException("k must be between 0 and values.length - 1");
        }
        metrics.reset();
        return select(values, 0, values.length - 1, k, 1, metrics);
    }

    private static int select(int[] values, int low, int high, int k, int depth, AlgorithmMetrics metrics) {
        metrics.recursiveCall(depth);
        if (high - low < 5) {
            insertionSort(values, low, high, metrics);
            return values[k];
        }

        int medianCount = 0;
        for (int groupStart = low; groupStart <= high; groupStart += 5) {
            int groupEnd = Math.min(groupStart + 4, high);
            insertionSort(values, groupStart, groupEnd, metrics);
            int median = groupStart + (groupEnd - groupStart) / 2;
            swap(values, low + medianCount, median, metrics);
            medianCount++;
        }

        int medianIndex = low + medianCount / 2;
        int pivot = select(values, low, low + medianCount - 1, medianIndex, depth + 1, metrics);
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

        if (k < less) {
            return select(values, low, less - 1, k, depth + 1, metrics);
        }
        if (k > greater) {
            return select(values, greater + 1, high, k, depth + 1, metrics);
        }
        return pivot;
    }

    private static void insertionSort(int[] values, int low, int high, AlgorithmMetrics metrics) {
        for (int i = low + 1; i <= high; i++) {
            int value = values[i];
            int j = i - 1;
            while (j >= low) {
                metrics.comparison();
                if (values[j] <= value) {
                    break;
                }
                values[j + 1] = values[j];
                metrics.swap();
                j--;
            }
            values[j + 1] = value;
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
