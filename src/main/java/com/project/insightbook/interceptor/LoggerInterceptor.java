package com.project.insightbook.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoggerInterceptor extends HandlerInterceptorAdapter {
    /*
    preHandle : 컨트롤러 실행전에 수행됨
    postHandle : 컨트롤러 수행 후 결과를 뷰로 보내기 전에 수행
    afterCompletion : 뷰의 작업까지 완료된 후 수행
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws  Exception{
        log.debug("============================== [ START ] ==============================");
        log.debug(" REQUEST URI : " + request.getRequestURI());
        return super.preHandle(request,response,handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception{
        log.debug("============================== [ END ] ==============================");
    }
}
