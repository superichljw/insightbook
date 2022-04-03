package com.project.insightbook.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:/application.properties")
@EnableTransactionManagement /*트랜잭션 어노테이션*/
public class DataConfiguration {

    /*
    * 스프링 마이바티스에서 sqlsessionfactory 를 생성하기 위해서는 sqlsessionfactorybean 을 사용함
    * 만약 스프링이 아닌 마이바티스 단독으로 사용할 경우, sqlsessionfactorybuilder 를 사용함
    *
    * 1. bean의 생성과 제어를 위해 applicationcontext 객체를 생성 (@configuration 어노테이션 선언되어야 함)
    * 2. 이때 applicaion.properties 에 저장된 설정정보를 읽어온다
    * 3. 히카리cp의 설정정보가 담긴 객체를 생성
    * 4. 히카리의 정보가 담긴 객체를 바탕으로 컨넥션풀 생성 및 db 연결
    * 5. sqlsessionfactory 에서 생성된 컨넥션풀을 바탕으로 db와 연결하고, sql 문의 경로를 설정
    * 6. sqlsessiontemplate 객체를 생성하여, 쓰레드별로 sql 문을 실행하고 생명주기를 관리함
    * */

    /*
    * applicationcontext : 스프링의 빈의 생성과 관계설정 등의 제어를 담당하는 beanfactory를 상속받아 빈어 생성, 관계설정등의 제어작업을 총괄함
    * */
    @Autowired
    private ApplicationContext applicationContext;

    /*
    * 히카리cp : 스프링에서 사용하는 컨넥션 풀로, 기존 톰캣 컨넥션 풀에서 히카리로 변경됨
    * 제조사에서 제공되는 커넥션풀보다 빠른속도가 장점
    *
    * 커넥션풀(DBCP-Data Base Connection Pool) : DB와 커넥션을 맺고 있는 객체를 관리하는 역할
    * was 가 실행되면서 db와 미리 연결해놓은 객체들을 pool에 저장해두었다가,
    * 클라이언트 요청이 오면 connection 을 빌려주고, 처리가 끝나면 다시 connection 을 반납받아 pool에 저장하는 방식
    *
    * connection 을 미리 생성해놓고 사용하기 때문에 매 요청마다 db 에 연결시간을 소비하지 않으나,
    * connection을 재사용하기 때문에 생성되는 connection 수를 제한적으로 설정한다
    *
    * 동시접속자가 많을 경우, connection 이 반환될 때까지 대기상태로 기다리며
    * 그렇다고 was에 connection 수를 많게 설정하면 메모리 소모가 크다
    * */



    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig(){
        return new HikariConfig();
    }

    /*
    * 히카리 설정이 담긴 객체를 파라미터로 넘겨 커넥션 풀을 생성하고, 데이터 베이스 객체를 생성한다
    * */
    @Bean
    public DataSource dataSource() throws Exception{
        DataSource dataSource = new HikariDataSource(hikariConfig());
        System.out.println(dataSource.toString());
        return dataSource;
    }

    /*
    * application.properties에 설정한 마이바티스 카멜케이스 변환 설정을 담은 객체를 생성
    * */
    @Bean
    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration mybatisConfig(){
        return new org.apache.ibatis.session.Configuration();
    }

    /*
    * sqlsessionfactory 는 데이터 베이스와의 연결과 sql 을 실행하는 객체
    * 생성된 데이터베이스 컨넥션풀을 가지고 데이터베이스를 연결하고,
    * sql 을 실행하는 mapper 파일의 경로와 파일형태를 지정해준다
    */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/sql-*.xml"));
        sqlSessionFactoryBean.setConfiguration(mybatisConfig());
        return sqlSessionFactoryBean.getObject();
    }
    /*
    * sqlsession : sql 의 실행과 생명주기를 담당(세션을 열고 매핑문을 커밋하거나 롤백 - 트랜잭션의 보장)
    * sqlsessiontemplate : sqlsession 을 구현하고 코드에서 sqlsession 을 대체하는 역할을 함
    * */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    /*
     * 트랜잭션 :: 데이터베이스의 상태를 변화시킬 때 더이상 분리할 수 없는 작업의 단위를 의미
     * - ACID
     *   - Atomicity(원자성) : 트랜잭션은 하나 이상의 관련된 동작을 하나의 작업단위로 처리한다 - 작업 중 하나라도 실패하면 관련된 트랜잭션 내에서 먼저 처리한 동작들도 처음상태로 돌아간다
     *       - "되려면 다 되고, 안되면 다 안되어야 한다"
     *   - Consistency(일관성) : 트랜잭션이 정상 처리되면, 관련된 모든 데이터는 일관성을 유지해야한다
     *   - Isolation(고립성) : 트랜잭션은 독립적으로 처리되며, 처리과정에서 외부의 간섭은 없어야 한다
     *   - Durability(지속성) : 트랜잭션이 성공적으로 처리되면 그 결과는 지속적으로 유지되어야 한다
     *
     * */

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception{
        return new DataSourceTransactionManager(dataSource());
    }
}
