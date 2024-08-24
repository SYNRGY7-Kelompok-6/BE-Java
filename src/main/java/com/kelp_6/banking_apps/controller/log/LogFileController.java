package com.kelp_6.banking_apps.controller.log;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/logs")
public class LogFileController {

    private final String logDirectory = System.getProperty("user.dir") + "/logs";
    // Set this to your log directory

    @GetMapping("/latest")
    public ResponseEntity<Resource> downloadLatestLogFile() {
        try {
            // Construct the path to the latest log file
            Path latestLogFilePath = Paths.get(logDirectory).resolve("banking-apps.log").normalize();

            // Load the latest log file as a resource
            Resource resource = new UrlResource(latestLogFilePath.toUri());

            // Check if the file exists
            if (!resource.exists() || !resource.isReadable()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Prepare the response with the file
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
