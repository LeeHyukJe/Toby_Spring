package springbook.learningtest.spring.pointcut;

import org.junit.Test;

public class Bean {
    @Test
    public void method() throws RuntimeException,Exception{
        System.out.println(Target.class.getMethod("minus",int.class,int.class));
    }



}
