package com.kelp_6.banking_apps.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testing {

    @GetMapping("/tes")
    public String tes(){
        return "Success";
    }
}
