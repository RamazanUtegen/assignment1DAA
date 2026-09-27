package daa;

public final class MergeSorter {
    private static final int INSERTION_CUTOFF = 16;

    private MergeSorter() {
    }

    public static void sort(int[] values) {
        sort(values, new AlgorithmMetrics());
    }

    public static void sort(int[] values, AlgorithmMetrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("Values must not be null");
        }
        metrics.reset();
        if (values.length < 2) {
            return;
        }
        int[] buffer = new int[values.length];
        sort(values, buffer, 0, values.length - 1, 1, metrics);
    }

    private static void sort(int[] values, int[] buffer, int low, int high, int depth, AlgorithmMetrics metrics) {
        metrics.recursiveCall(depth);
        if (high - low + 1 <= INSERTION_CUTOFF) {
            insertionSort(values, low, high, metrics);
            return;
        }
        int middle = low + (high - low) / 2;
        sort(values, buffer, low, middle, depth + 1, metrics);
        sort(values, buffer, middle + 1, high, depth + 1, metrics);
        metrics.comparison();
        if (values[middle] <= values[middle + 1]) {
            return;
        }
        merge(values, buffer, low, middle, high, metrics);
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

    private static void merge(int[] values, int[] buffer, int low, int middle, int high, AlgorithmMetrics metrics) {
        System.arraycopy(values, low, buffer, low, high - low + 1);
        int left = low;
        int right = middle + 1;
        for (int target = low; target <= high; target++) {
            if (left > middle) {
                values[target] = buffer[right++];
            } else if (right > high) {
                values[target] = buffer[left++];
            } else {
                metrics.comparison();
                if (buffer[left] <= buffer[right]) {
                    values[target] = buffer[left++];
                } else {
                    values[target] = buffer[right++];
                }
            }
        }
    }
}
