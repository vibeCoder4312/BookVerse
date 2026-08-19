package com.bookverse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController is a shortcut for @Controller + @ResponseBody.
// It tells Spring: "this class handles HTTP requests, and whatever
// its methods return should be written directly into the HTTP response
// body (as JSON/text), not treated as the name of an HTML template."
@RestController
public class HealthController {

    // @GetMapping maps HTTP GET requests at this path to this method.
    // So a browser or Postman visiting GET http://localhost:8080/api/health
    // will trigger this method and get its return value back.
    @GetMapping("/api/health")
    public String health() {
        return "BookVerse backend is running!";
    }

}
