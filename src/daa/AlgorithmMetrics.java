package daa;

public final class AlgorithmMetrics {
    private long comparisons;
    private long swaps;
    private long recursiveCalls;
    private int maximumRecursionDepth;

    public void reset() {
        comparisons = 0;
        swaps = 0;
        recursiveCalls = 0;
        maximumRecursionDepth = 0;
    }

    public void comparison() {
        comparisons++;
    }

    public void swap() {
        swaps++;
    }

    public void recursiveCall(int depth) {
        recursiveCalls++;
        maximumRecursionDepth = Math.max(maximumRecursionDepth, depth);
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getRecursiveCalls() {
        return recursiveCalls;
    }

    public int getMaximumRecursionDepth() {
        return maximumRecursionDepth;
    }
}
