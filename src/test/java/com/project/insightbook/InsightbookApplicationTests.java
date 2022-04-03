package com.project.insightbook;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InsightbookApplicationTests {

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Test
    void contextLoads() {
    }

    /*
    * sql 연동 테스트 : sqlsessiontemplate 으로 seqlsession 객체를 생성하고, 그것을 콘솔에 찍어본다
    * */
    @Test
    public void testSqlSession() throws Exception{
        System.out.println("testSqlSession - getConfig :: " + sqlSession.getConfiguration());
        System.out.println("testSqlSession - sqlsession :: " + sqlSession);
    }
}
