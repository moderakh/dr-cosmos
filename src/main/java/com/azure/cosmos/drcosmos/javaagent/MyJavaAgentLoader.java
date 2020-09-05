package com.azure.cosmos.drcosmos.javaagent;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJavaAgentLoader {

    static final Logger logger = LoggerFactory.getLogger(MyJavaAgentLoader.class);

    private static final String jarFilePath = "/Users/moderakh/github/dr-cosmos/target/drcosmos-0"
        + ".1-jar-with-dependencies.jar";

    public static void loadAgent(String pid, String option) {
        logger.info("dynamically loading javaagent");

        try {
            System.out.println("connecting to pid " + pid);
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath, option);
            System.out.println("detaching ...");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}