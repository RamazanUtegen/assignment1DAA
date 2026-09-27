package daa;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SortingTest {
    @Test
    void bothSortersMatchArraysSortForRequiredInputTypes() {
        int[][] inputs = {
                {},
                {7},
                {5, 4, 3, 2, 1},
                {1, 2, 3, 4, 5},
                {4, 1, 4, 2, 4, 1, 2},
                {Integer.MIN_VALUE, 0, Integer.MAX_VALUE, -1, 1}
        };
        for (int[] input : inputs) {
            assertMatchesReference(input);
        }
    }

    @Test
    void bothSortersMatchArraysSortForRandomArrays() {
        Random random = new Random(8142);
        for (int trial = 0; trial < 200; trial++) {
            int[] input = new int[random.nextInt(501)];
            for (int i = 0; i < input.length; i++) {
                input[i] = trial % 2 == 0 ? random.nextInt(21) - 10 : random.nextInt();
            }
            assertMatchesReference(input);
        }
    }

    @Test
    void nullInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> MergeSorter.sort(null));
        assertThrows(IllegalArgumentException.class, () -> QuickSorter.sort(null));
    }

    private void assertMatchesReference(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        int[] mergeInput = input.clone();
        int[] quickInput = input.clone();
        MergeSorter.sort(mergeInput);
        QuickSorter.sort(quickInput);
        assertArrayEquals(expected, mergeInput);
        assertArrayEquals(expected, quickInput);
    }
}
