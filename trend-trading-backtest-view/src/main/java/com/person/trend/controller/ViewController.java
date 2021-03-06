package com.person.trend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RefreshScope  //表示可以进行刷新
public class ViewController {

    @Value("${version}")
    String version;

    @GetMapping("/")
    public String view(Model model) {
        model.addAttribute("version", version);

        return "view";
    }


}
