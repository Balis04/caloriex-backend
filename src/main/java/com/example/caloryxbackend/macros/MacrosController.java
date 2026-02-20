package com.example.caloryxbackend.macros;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MacrosController {

    @GetMapping("/summary")
    public String getSummary() {
        return "Summary data";
    }
}
