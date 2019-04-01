package springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DynamicProxyTest {

    @Test
    public void simpleProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertThat(proxiedHello.sayHello("Hyukje"), is("HELLO HYUKJE"));
    }

    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String) methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Hyukje"), is("HELLO HYUKJE"));
        assertThat(proxiedHello.sayHi("HYUKJE"), is("HI HYUKJE"));
        assertThat(proxiedHello.sayThankyou("HYUKJE"), is("THANK YOU HYUKJE"));
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut,
                new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();
        assertThat(proxiedHello.sayHello("Hyukje"), is("HELLO HYUKJE"));
        assertThat(proxiedHello.sayHi("HYUKJE"), is("HI HYUKJE"));
        assertThat(proxiedHello.sayThankyou("HYUKJE"), is("THANK YOU HYUKJE"));
    }

    @Test
    public void classNamePointcutAdvisor() {
        NameMatchMethodPointcut classMethodPointcut =
                new NameMatchMethodPointcut() {
                    public ClassFilter getClassFilter() {
                        return new ClassFilter() {
                            @Override
                            public boolean matches(Class<?> aClass) {
                                return aClass.getSimpleName().startsWith("HelloT");
                            }
                        };
                    }
                };
        classMethodPointcut.setMappedName("sayH*");

        // 테스트
        checkAdviced(new HelloTarget(),classMethodPointcut,true);

        class HelloWorld extends HelloTarget{}
        checkAdviced(new HelloWorld(),classMethodPointcut,false);

        class HelloHyukje extends HelloTarget{}
        checkAdviced(new HelloHyukje(),classMethodPointcut,true);
    }



    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean=new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut,new UppercaseAdvice()));
        Hello proxiedHello=(Hello) pfBean.getObject();

        if(adviced){
            assertThat(proxiedHello.sayHello("Hyukje"),is("HELLO HYUKJE"));
            assertThat(proxiedHello.sayHi("Hyukje"),is("HI HYUKJE"));
            assertThat(proxiedHello.sayThankyou("Hyukje"),is("Thank you Hyukje"));
        }
        else{
            assertThat(proxiedHello.sayHello("Hyukje"),is("Hello Hyukje"));
            assertThat(proxiedHello.sayHi("Hyukje"),is("Hi Hyukje"));
            assertThat(proxiedHello.sayThankyou("Hyukje"),is("Thank you Hyukje"));
        }
    }


}
