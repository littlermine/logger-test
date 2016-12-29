package com.littlemine.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "application/json" })
public class LoggerController {

    @ResponseBody
    @RequestMapping(params = { "Action=LoggerTest" })
    public String loggerTest() {
        log.info("this is logger test interface..this is logger test interface..this is logger test interface..this is info");
        return "{\"Status\":200}";
    }

}
