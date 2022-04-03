package com.project.insightbook.config;

import com.project.insightbook.interceptor.LoggerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.nio.charset.Charset;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    /*
    * addPathPatterns() : 포함할 요청 주소의 패턴을 지정
    * excludePathPatterns() : 제외할 요청 주소의 패턴을 지정
    * 아무것도 등록하지 않으면 모든 요청에 대해서 수행한다
    * */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoggerInterceptor());
    }

    /*
    * 한글 인코딩 적용
    * 책에는 메서드명을 characterEncodingFilter 이렇게 지정했는데 오류가 뜨고 실행이 안됨
    * 검색해보니, 해당 메서드명을 그대로 사용할 경우 이름이 충돌나서 실행이 안되는 경우가 있다고 함
    * 그래서 이름을 바꿔서 실행해보니 정상작동된 것을 확인함
    * */
    @Bean
    public Filter customCharacterEncodingFilter(){
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        return characterEncodingFilter;
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter(){
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }
    /*
    * 파일업로드 빈등록
    *
    * 1. gradle 에 commons-io, commons file upload 추가
    * 2. 여기에 빈 등록
    * 3. InsightbookApplicaion 에서 첨부파일 업로드 관련 구성을 사용하지 않도록 exclude 처리해야함
    * 4. html 파일 수정
    * 5. 컨트롤러 수정
    * 6. serviceImpl 수정
    * 7. dto 추가
    * 8. fileUtils 클래스 추가
    * */
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSizePerFile(1024 * 1024 * 1024);

        return commonsMultipartResolver;
    }
}
