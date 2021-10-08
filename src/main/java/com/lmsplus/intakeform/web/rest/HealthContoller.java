package com.lmsplus.intakeform.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthContoller {

    @GetMapping("/health")
    public HttpStatus health()
    {
        return HttpStatus.OK;
    }
}
