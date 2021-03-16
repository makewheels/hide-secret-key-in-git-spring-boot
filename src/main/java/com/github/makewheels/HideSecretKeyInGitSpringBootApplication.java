package com.github.makewheels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HideSecretKeyInGitSpringBootApplication {

    public static void main(String[] args) {
        SecretKeyUtil.overrideKeys();

        SpringApplication.run(HideSecretKeyInGitSpringBootApplication.class, args);
    }

}