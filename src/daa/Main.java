package daa;

import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Path output = args.length == 0 ? Path.of("results", "results.csv") : Path.of(args[0]);
        Experiment.run(output);
    }
}
