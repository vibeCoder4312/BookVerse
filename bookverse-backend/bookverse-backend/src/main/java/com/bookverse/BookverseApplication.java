package com.bookverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication is actually THREE annotations combined into one:
//   1. @Configuration   -> tells Spring this class can define beans/config
//   2. @EnableAutoConfiguration -> Spring Boot auto-configures things like
//                                  the web server and database connection
//                                  based on what dependencies it finds in pom.xml
//   3. @ComponentScan   -> tells Spring to scan this package (com.bookverse)
//                          and all sub-packages for @Component, @Service,
//                          @Repository, @Controller classes and register them
//
// This is why our folder structure matters: everything must live under
// com.bookverse so Spring can find it automatically.
@SpringBootApplication
public class BookverseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookverseApplication.class, args);
    }

}
