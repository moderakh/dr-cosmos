package com.azure.cosmos.drcosmos.heap;

import com.azure.cosmos.drcosmos.helpers.ProcessHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HeapAnalysis {

    public static void runHeapAnalysis(Path reportRootPath, int processId) {

        try {
            Path filePath = Files.createFile(Paths.get(reportRootPath.toString(), "heap-histo.txt"));
            ProcessHelper.run(filePath.toFile(), "jmap", "-histo", Integer.toString(processId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
