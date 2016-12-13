package com.littlemine.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "application/json" })
public class LoggerController {

    @ResponseBody
    @RequestMapping(params = { "Action=LoggerTest" })
    public String loggerTest() {
        //        log.trace("this is logger test interface..trace");
        //        log.debug("this is logger test interface..debug");
        log.info("this is logger test interface..this is logger test interface..this is logger test interface..this is logger test interface..info");
        //        log.warn("this is logger test interface..warn");
        //        log.error("this is logger test interface..error");
        return "{\"Status\":200}";
    }

}
