package com.project.insightbook.controller;

import com.project.insightbook.paging.CommonParams;
import com.project.insightbook.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/*
* @RestController 로 해야 json api 통신을하게된다
* @Controller 로 했다가 500 에러 떠서 엄청 삽질 했다
* @RequiredArgsConstructor - final로 선언된 객체에 대해 자동으로 생성자를 만들어준다
* */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    @GetMapping("/boardListApi")
    public Map<String,Object> boardListApi(CommonParams params) throws Exception{
        return boardService.selectBoardList(params);
    }
}
