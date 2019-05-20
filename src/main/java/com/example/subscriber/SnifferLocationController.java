package com.example.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnifferLocationController {

    @Autowired
    private SnifferLocationService snifferLocationService;

    @RequestMapping("/update")
    public void updateSniffers(){
        snifferLocationService.update();
    }
}
