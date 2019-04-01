package springbook.learningtest.jdk;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReflectionTest {
    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length()
        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer) lengthMethod.invoke(name), is(6));

        // chartAt()
        assertThat(name.charAt(0), is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character) charAtMethod.invoke(name, 0), is('S'));
    }


    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget(); // 타깃은 인터페이스로 접근하는 습관을 들이자
        assertThat(hello.sayHello("Hyukje"), is("Hello Hyukje"));
        assertThat(hello.sayHi("Hyukje"), is("Hi Hyukje"));
        assertThat(hello.sayThankyou("Hyukje"), is("Thank you Hyukje"));

        //        -- 다이나믹 프록시를 사용하기 전--
//        Hello proxiedHello = new HelloUppercase(new HelloTarget());
//        assertThat(proxiedHello.sayHello("Hyukje"),is("HELLO HYUKJE"));
//        assertThat(proxiedHello.sayHi("HYUKJE"),is("HI HYUKJE"));
//        assertThat(proxiedHello.sayThankyou("HYUKJE"),is("THANK YOU HYUKJE"));

        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class}, // 구현 할 인터페이스
                new UppercaseHandler(new HelloTarget()) // 부가기능과 위임 코드를 담은 InvocationHandler
        );
        assertThat(proxiedHello.sayHello("Hyukje"),is("HELLO HYUKJE"));
        assertThat(proxiedHello.sayHi("HYUKJE"),is("HI HYUKJE"));
        assertThat(proxiedHello.sayThankyou("HYUKJE"),is("THANK YOU HYUKJE"));
    }


}
