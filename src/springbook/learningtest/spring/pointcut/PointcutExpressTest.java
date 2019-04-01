package springbook.learningtest.spring.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PointcutExpressTest {

    @Test
    public void methodSignaturePointcut() throws SecurityException,NoSuchMethodException{
        AspectJExpressionPointcut pointcut=new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int springbook.learningtest.spring.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("minus",int.class, int.class),null)
        ,is(true));
    }
}
