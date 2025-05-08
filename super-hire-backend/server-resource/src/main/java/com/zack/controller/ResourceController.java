package com.zack.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello user";
    }
}
