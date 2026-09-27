package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DeterministicSelectorTest {
    @Test
    void matchesSortedReferenceForMoreThanOneHundredRandomTests() {
        Random random = new Random(16384);
        for (int trial = 0; trial < 150; trial++) {
            int[] input = new int[1 + random.nextInt(300)];
            for (int i = 0; i < input.length; i++) {
                input[i] = random.nextInt(51) - 25;
            }
            int[] sorted = input.clone();
            Arrays.sort(sorted);
            int[] ranks = {0, input.length / 2, input.length - 1};
            for (int rank : ranks) {
                assertEquals(sorted[rank], DeterministicSelector.select(input.clone(), rank));
            }
        }
    }

    @Test
    void handlesSmallArraysAndDuplicates() {
        int[] input = {8, 2, 8, -1, 2, 8, 0};
        int[] sorted = input.clone();
        Arrays.sort(sorted);
        for (int rank = 0; rank < sorted.length; rank++) {
            assertEquals(sorted[rank], DeterministicSelector.select(input.clone(), rank));
        }
    }

    @Test
    void rejectsInvalidRanksAndEmptyInput() {
        assertThrows(IndexOutOfBoundsException.class, () -> DeterministicSelector.select(new int[0], 0));
        assertThrows(IndexOutOfBoundsException.class, () -> DeterministicSelector.select(new int[]{1}, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> DeterministicSelector.select(new int[]{1}, 1));
    }
}
