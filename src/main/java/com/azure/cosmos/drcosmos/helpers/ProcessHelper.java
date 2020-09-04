package com.azure.cosmos.drcosmos.helpers;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessHelper {

    public static void run(File output, String... cmd) throws IOException, InterruptedException {
        Process iostat = new ProcessBuilder().command(cmd)
                                             .redirectError(output)
                                             .redirectOutput(output).start();
        iostat.waitFor(10l, TimeUnit.SECONDS);
        return;
    }
}
