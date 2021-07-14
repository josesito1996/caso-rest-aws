package com.samy.service.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CasoServiceAwsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CasoServiceAwsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String fromFile = "src/main/resources/proxy.conf";
        String toFile = "/etc/nginx/conf.d/01_proxy.conf";

        boolean result = moveFile(fromFile, toFile);
        log.info(result ? "Success! File moved (Éxito! Fichero movido)"
                : "Error! Failed to move the file (Error! No se ha podido mover el fichero)");
    }

    private boolean moveFile(String fromFile, String toFile) {
        File origin = new File(fromFile);
        File destination = new File(toFile);
        if (origin.exists()) {
            try {
                if (!destination.exists()) {
                    destination.createNewFile();
                }
                InputStream in = new FileInputStream(origin);
                OutputStream out = new FileOutputStream(destination);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                return true;
            } catch (IOException ioe) {
                log.error("Error : " + ioe.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }
}
