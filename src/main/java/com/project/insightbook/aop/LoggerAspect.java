package com.project.insightbook.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggerAspect {
    /*
    * AOP : Aspect Oriented Programming - 관점 지향 프로그래밍
    * OOP : Object Oriented Programming - 객체 지향 프로그래밍
    *
    * 자바에서 OOP 는 공통 기능을 클래스로 만들고, 다른 클래스에서는 공통기능이 담긴 클래스의 객체를 생성해서 사용한다 - 코드를 중복작성할 필요없이 호출해서 사용하면 되서
    * 코드의 재사용성을 높이게 된다
    * AOP 는 OOP 를 더욱 OOP 답게 만들어준다
    *
    *         |       컨트롤러        |       서비스      |       DAO
    * 계정     |  권한          로깅   |  로깅     트랜잭션  |  로깅   ->
    * 게시판    |  권한          로깅   |  로깅     트랜잭션  |  로깅   ->
    * 계좌이체   |  권한          로깅   |  로깅     트랜잭션  |  로깅   ->
    *
    * 객체의 핵심 기능 이외에도 부가적 기능에서 공통적으로 들어가야 하는 기능들이 있다
    * 핵심기능의 관점에서는 비지니스 로직 + 공통 부가기능 이 들어가야 하지만,
    * 관점을 핵심기능이 아닌 '부가기능' 이라는 관점에서 보면
    * 권한, 로깅, 로깅, 트랜잭션, 로깅 등의 부가기능들을 하나로 묶을 수 있다
    *
    *         |       컨트롤러        |       서비스      |       DAO
    * 계정     |  권          로      |  로      트랜     |  로   ->
    * 게시판    |  한          깅     |   깅      잭      |  깅   ->
    * 계좌이체  |  |           |      |   |      션      |  |   ->
    *
    * 이렇게 부가기능들을 하나로 묶음으로써 비지니스로직은 신경쓰지 않고, 부가기능이 들어갈 시점만 파악해서 적용하면 되고,
    * 비지니스로직에서 호출해서 쓰는게 아니라, 부가기능을 외부에서 추가해주는 것이 핵심이다
    *
    * Aspect : 관점 - 공통적으로 적용될 기능을 의미(횡단 관심사의 기능이라고도 할 수 있음) : 한개 이상의 포인트컷과 어드바이스의 조합으로 만들어짐
    * Advice : 어드바이스 - 관점의 구현체 : 조인포인트에 삽입되어 동작하는 것을 의미 - 스프링에서는 동작시점에 따라 5종류로 분류
    *       - @Before : 대상 메서드가 실행되기 전에 적용할 어드바이스를 정의
    *       - @AfterReturning : 대상 메서드가 성공적으로 실행되고 결과값을 반환한 후 적용할 어드바이스를 정의
    *       - @AfterThrowing : 대상 메서드에서 예외가 발생했을때 적용할 어드바이스를 정의 - try/catch 에서 catch 와 유사
    *       - @After : 대상 메서드의 정상 실행 여부와 무관하게 무조건 실행되는 어드바이스 정의 - finally 와 유사
    *       - @Around : 대상 메서드의 호출 전호, 예외발생 등 모든 시점에 적용할 수 있는 어드바이스 - 가장 범용적 사용가능
    * Joinpoint : 조인포인트 - 어드바이스를 적용하는 지점 : 스프링에서는 항상 메서드 실행단계만 가능
    * Pointcut : 포인트컷 - 어드바이스를 적용할 조인포인트를 선별하는 과정이나 그 기능을 정의한 모듈을 의미 : 정규표현식이나 AspectJ의 문법을 사용해 어떤 조인포인트를 사용할지 결정
    *       - 정규표현식이나 AspectJ 문법을 이용해 어떤 조인포인트를 사용할 것인지 결정
    *       - 대표적 : execution , within, bean
    *       - execution : 대표 포인트컷 - * 은 모든 값을 의미 > select* > select로 시작하는 모든 메서드 선택
    *           - ..은 0개 이상을 의미
    *               - execution(void select*(..)) :: 리턴타입이 void이고, 메서드 이름이 select 로 시작 하며, 파라미터가 0개 이상인 모든 메서드
    *               - execution(* com.project.controller.*()) :: controller 패키지 밑에 파라미터가 없는 모든 메서드
    *               - execution(* com.project.controller.*(..)) :: controller 패키지 밑에 파라미터가 0개 이상인 모든 메서드
    *               - execution(* com.project..select*(*)) :: 패키지 하위 모든 메서드 중 select 로 시작하고, 파라미터가 1개인 모든 메서드
    *               - execution(* com.project..select*(*,*)) :: 패키지 하위 모든 메서드 중 select 로 시작하고, 파라미터가 2개인 모든 메서드
    *       - within : 특정 타입에 속하는 메서드를 포인트 컷으로 사용
    *               - within(com.project.service.boardServiceImpl) :: boardServiceImpl 클래스의 메서드가 호출될 때
    *               - within(com.project.service.*ServiceImpl) :: serviceImpl 로 끝나는 모든 메서드가 호출될때
    *       - bean : 스프링의 빈의 이름의 패턴으로 포인트컷 설정
    *               - bean(boardServiceImpl) :: boardServiceImpl 이라는 이름을 가진 빈의 메서드가 호출될 때
    *               - bean(*ServiceImpl) :: ServiceImpl이라는 이름으로 끝나는 빈의 메서드가 호출될 때
    * Target : 타겟 - 어드바이스를 받을 대상을 의미
    * Weaving : 위빙 - 어드바이스를 적용하는 것을 의미 : 즉, 공통코드를 원하는 대상에 삽입하는 것을 의미
    *
    * */

    @Around("execution(* com.project..controller.*Controller.*(..)) || " +
            "execution(* com.project..service.*Impl.*(..)) ||" +
            "execution(* com.project..mapper.*Mapper.*(..))")
    public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable{
        String type = "";
        String name = joinPoint.getSignature().getDeclaringTypeName();
        if(name.indexOf("Controller") > -1){
            type = "Controller \t : ";
        }else if(name.indexOf("Service") > -1){
            type = "ServiceImpl \t : ";
        }else if(name.indexOf("Mapper") > -1){
            type = "Mapper \t : ";
        }
        log.debug(type + name + "." + joinPoint.getSignature().getName() + "()");
        return joinPoint.proceed();
    }

}
