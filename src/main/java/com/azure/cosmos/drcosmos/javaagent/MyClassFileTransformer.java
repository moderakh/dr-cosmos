package com.azure.cosmos.drcosmos.javaagent;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.UUID;

public class MyClassFileTransformer implements ClassFileTransformer {
    private final JavaAgentConfig cfg;

    public MyClassFileTransformer(JavaAgentConfig cfg) throws IOException {
        this.cfg = cfg;
    }

    public static boolean accept(Class<?> classBeingRedefined) {
        String name = classBeingRedefined.getName();
        return name != null && name.contains("com.azure.cosmos.implementation.HttpConstants$Versions");
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {


        if (!accept(classBeingRedefined)) {
            return classfileBuffer;
        }

        try {
            Path filePath = Files.createFile(Paths.get(cfg.reportDirPath,
                "version-" + UUID.randomUUID().toString() + ".txt"));

            try (FileWriter fos = new FileWriter((filePath.toFile()))) {
                System.out.println("transformer: " + className);
                System.out.flush();
                try {
                    Field f = classBeingRedefined.getDeclaredField("SDK_VERSION");
                    f.setAccessible(true);
                    String val = (String) f.get(null);
                    fos.append("version is: " + val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
