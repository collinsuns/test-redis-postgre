package com.mk.test;

import static java.lang.System.exit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mk.test.service.TestService;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {
    @Autowired
    private TestService testService;
    
    public static void main(String[] args) throws Exception {

        // disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    // access command line arguments
    @Override
    public void run(String... args) throws Exception {

        // if (args.length > 0) {
        // System.out.println(args[0].toString());
        // } else {
        // System.out.println("no args");
        // }

        System.out.println("+++++++++++++++ hello test!");
        testService.testService();

        exit(0);

    }

}
