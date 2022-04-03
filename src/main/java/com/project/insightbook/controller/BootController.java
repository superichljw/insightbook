package com.project.insightbook.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/boot")
public class BootController {

    @RequestMapping("/index")
    public String bootIndex(){
        return "/bootstrap/index";
    }

    @RequestMapping("/home-portfolid")
    public String homePortfolio(){
        return "/bootstrap/index-portfolio";
    }

    @RequestMapping("/news-list")
    public String newsList(){
        return "/bootstrap/news-list";
    }
}
