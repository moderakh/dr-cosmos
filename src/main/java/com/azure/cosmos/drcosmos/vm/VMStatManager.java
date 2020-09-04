package com.azure.cosmos.drcosmos.vm;

import reactor.netty.http.client.HttpClient;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VMStatManager {
    private static final String AZURE_VM_STAT_ENDPOINT = "http://169.254.169.254/metadata/instance?api-version=2020-06-01";

    public void reportAzureVMStat(Path reportRootPath) {
        try {
            Path filePath = Files.createFile(Paths.get(reportRootPath.toString(), "azure-vm.txt"));

            // Prepares an HTTP client ready for configuration
            // client should connect
            // Specifies that POST method will be used
            // Specifies the path
            // Receives the response body
            String stat = HttpClient

                .create()             // Prepares an HTTP client ready for configuration
                .headers(h -> h.add("Metadata", "true"))
                // client should connect
                .get()             // Specifies that POST method will be used
                .uri(AZURE_VM_STAT_ENDPOINT)   // Specifies the path
                .responseContent()    // Receives the response body
                .aggregate()
                .asString()
                .block();

            try(FileWriter fos = new FileWriter((filePath.toFile()))) {
                fos.write(stat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
