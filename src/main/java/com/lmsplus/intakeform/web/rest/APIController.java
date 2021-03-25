package com.lmsplus.intakeform.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @GetMapping("test")
    public String test()
    {
        return "it works";
    }
}
