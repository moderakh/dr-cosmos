package com.azure.cosmos.drcosmos;

import com.azure.cosmos.drcosmos.heap.HeapAnalysis;
import com.azure.cosmos.drcosmos.javaagent.MyJavaAgent;
import com.azure.cosmos.drcosmos.vm.VMStatManager;
import com.beust.jcommander.JCommander;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class Main {
    static {

    }

    public static void main(String[] args) throws Exception {


        Configuration cfg = new Configuration();
        try {
            JCommander jcommander = new JCommander(cfg, args);

            if (cfg.isHelp()) {
                // prints out the usage help
                jcommander.usage();
                return;
            }
        } catch (com.beust.jcommander.ParameterException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Instant now = Instant.now();
        String reportPath = "dr-cosmos-report-" + now.toString();
        reportPath = Paths.get(new File(".").getCanonicalPath(), reportPath).toString();
        Path reportRootPath = Files.createDirectory(Paths.get(reportPath));

        new VMStatManager().reportAzureVMStat(reportRootPath);
        HeapAnalysis.runHeapAnalysis(reportRootPath, cfg.getProcessId());

        if (cfg.isJavaAgent()) {
            System.out.println("injected javaagent");
            System.out.println("Main initialized. Using process id " + cfg.getProcessId());
            MyJavaAgent.initialize(Integer.toString(cfg.getProcessId()), "reportPath=" + reportPath);
        }
    }
}
