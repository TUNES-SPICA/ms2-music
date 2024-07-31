package org.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MS2MusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MS2MusicApplication.class, args);

        System.out.println("http://127.0.0.1:8080/index.html");
    }
}
