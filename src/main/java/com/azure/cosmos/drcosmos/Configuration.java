package com.azure.cosmos.drcosmos;

import com.beust.jcommander.Parameter;

public class Configuration {

    public int getProcessId() {
        return processId;
    }

    @Parameter(names = "-processId", description = "Process ID", required = true)
    private int processId;

    @Parameter(names = { "-help", "--help", "-h" }, description = "help")
    private boolean help;

    public boolean isHelp() {
        return help;
    }
}

