package com.eg.hidesecretkeyingitspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class HideSecretKeyInGitSpringBootApplication {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        SpringApplication.run(HideSecretKeyInGitSpringBootApplication.class, args);
    }

}
