package com.azure.cosmos.drcosmos.javaagent;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MyJavaAgentLoader {

    static final Logger logger = LoggerFactory.getLogger(MyJavaAgentLoader.class);
    private static String jarFilePath;

    static {
        try {
            jarFilePath = new File(MyJavaAgentLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    public static void loadAgent(String pid, String option) {
        logger.info("dynamically loading javaagent");

        try {
            if (jarFilePath == null) {
                System.out.println("failed to inialized");
                return;
            }
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