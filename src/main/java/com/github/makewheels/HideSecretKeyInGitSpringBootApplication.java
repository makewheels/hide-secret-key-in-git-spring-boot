package com.github.makewheels;

import com.github.makewheels.hideyourkeys.HideYourKeys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HideSecretKeyInGitSpringBootApplication {

    public static void main(String[] args) {
        HideYourKeys.hideKeysInSpringBoot();

        SpringApplication.run(HideSecretKeyInGitSpringBootApplication.class, args);
    }

}