package com.project.insightbook.aop;

/*
 *  트랜잭션 어노테이션 - 클래스나 메서드에 사용가능 : 어노테이션에 적용된 대상은 설정된 트랜잭션 빈에 의해 트랜잭션 처리가 된다
 *  쉽게 사용할 수 있다는 장점이 있지만, 클래스를 생성할 때마다 계속 어노테이션 선언해야 한다
 *  그래서 aop 단에서 트랜잭션 처리하기도 한다
 *
 * */

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;

@Configuration
public class TransactionAspect {

    private static final String AOP_TRANSACTION_METHOD_NAME = "*";
    private static final String AOP_TRANSACTION_EXPRESSION = "execution(* com.project..service.*Impl.*(..))";

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public TransactionInterceptor transactionAdvice(){
        MatchAlwaysTransactionAttributeSource source = new MatchAlwaysTransactionAttributeSource();
        RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();

        transactionAttribute.setName(AOP_TRANSACTION_METHOD_NAME);
        // 자바의 모든 예외는 Exception을 상속받기 때문에, 모든 예외가 발생시 롤백되도록 롤백룰 추가
        // Collections.singletonList 는 값 변경이 불가능한 1개의 객체만을 갖는 리스트 , 사이즈가 1로 고정됨
        // Arrays.asList 와 종종 비교되곤 하는데, Arrays.asList 는 추가 삭제는 되지 않지만, 담고있는 속성의 값은 set 메서드를 이용해 변경할 수 있다
        // Collections.singletonList 는 크기는 항상 1이며, 담고있는 요소의 값도 변경이 불가능하다
        // 만약 특정 예외 몇개를 지정하여 사용하고 싶다면, 아래와 같이 사용하면 된다
        // 근데 이게 제대로 적용되는가는 모르겠다. 실제로 테스트해봤는데, nullpointerexception 만 롤백룰을 셋팅하고,
        // service, serviceImpl 에서도 nullpointexception 만 처리하고 , arithmaticexception 을 발생시켰는데 조회수가 증가하지 않더라.
        //
        //        RollbackRuleAttribute[] arrays = {new RollbackRuleAttribute(NullPointerException.class),new RollbackRuleAttribute(ArithmeticException.class), new RollbackRuleAttribute(NumberFormatException.class)};
        //        List<RollbackRuleAttribute> list = Arrays.asList(arrays);
        //        transactionAttribute.setRollbackRules(list);
        transactionAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));

        source.setTransactionAttribute(transactionAttribute);

        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor transactionAdviceAdvisor(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_TRANSACTION_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
    }
}
