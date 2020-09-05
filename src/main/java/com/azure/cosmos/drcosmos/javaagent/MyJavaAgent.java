package com.azure.cosmos.drcosmos.javaagent;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJavaAgent {

    static final Logger logger = LoggerFactory.getLogger(MyJavaAgent.class);

    private static Instrumentation instrumentation;
    private static JavaAgentConfig options;

    /**
     * JVM hook to statically load the javaagent at startup.
     *
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        logger.info("premain method invoked with args: {} and inst: {}", args, inst);
        instrumentation = inst;
        options = parse(args);
//        if (instrumentation != null) {
//            instrumentation.addTransformer(new MyClassFileTransformer());
//        }
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        logger.info("agentmain method invoked with args: {} and inst: {}", args, inst);
        instrumentation = inst;
        options = parse(args);


        String className = "com.azure.cosmos.implementation.HttpConstants$Versions";
        transformClass(className,inst);
    }

    private static JavaAgentConfig parse(String options) {
        String[] optionsList = options.split(";");
        String[] paths = optionsList[0].split("=");
        JavaAgentConfig cfg = new JavaAgentConfig();
        cfg.reportDirPath = paths[1];
        return cfg;
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize(String pid, String opts) {
        System.out.println("initialize");
        if (instrumentation == null) {
            MyJavaAgentLoader.loadAgent(pid, opts);
        }
    }

    private static void transformClass(
        String className, Instrumentation instrumentation) {
        Class<?> targetCls = null;
        ClassLoader targetClassLoader = null;
        // see if we can get the class using forName
        try {
            targetCls = Class.forName(className);
            targetClassLoader = targetCls.getClassLoader();
            transform(targetCls, targetClassLoader, instrumentation, false);
            System.out.println("found the target class");
            return;
        } catch (Exception ex) {
            logger.error("Class [{}] not found with Class.forName");
        }

        System.out.println("blind filtering ...");
        // otherwise iterate all loaded classes and find what we want
        for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(className)) {
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, instrumentation, true);
                return;
            }
        }
        throw new RuntimeException(
            "Failed to find class [" + className + "]");
    }

    private static void transform(
        Class<?> clazz,
        ClassLoader classLoader,
        Instrumentation instrumentation,
        boolean filter) {

        try {
            if (!filter || MyClassFileTransformer.accept(clazz)) {
                System.out.println("in transform :" + clazz.getName());
                MyClassFileTransformer transformer = new MyClassFileTransformer(options);
                instrumentation.addTransformer(transformer, true);
                instrumentation.retransformClasses(clazz);
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                "Transform failed for: [" + clazz.getName() + "]", ex);
        }
    }

}
