package com.project.insightbook.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice/*예외처리용 어노테이션*/
public class ExceptionHandler {

    /*
    * 해당 메서드에서 처리할 예외를 어노테이션으로 지정함
    * 여기서는 모든 예외를 한꺼번에 처리했지만, 실무에서는 예외상황별로 처리함
    * */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ModelAndView defaultExceptionHandler(HttpServletRequest request, Exception exception){
        ModelAndView mv = new ModelAndView("/error/error_default");
        mv.addObject("exception",exception);
        log.error("exception", exception);

        return mv;
    }
}
