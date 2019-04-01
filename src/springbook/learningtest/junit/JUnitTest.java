package springbook.learningtest.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;
import static org.junit.matchers.JUnitMatchers.either;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/junit.xml")
public class JUnitTest {
    static Set<JUnitTest> testObject=new HashSet<JUnitTest>();

    @Autowired
    ApplicationContext context;
    static ApplicationContext contextObject = null;
    @Test
    public void test1(){
        assertThat(testObject, is(not(hasItem(this))));
        testObject.add(this);

        assertThat(contextObject==null||contextObject==this.context,
                is(true));
    }

    @Test
    public void test2(){
        assertThat(testObject, is(not(hasItem(this))));
        testObject.add(this);

        assertTrue(contextObject==null||contextObject==this.context);
        contextObject=this.context;
    }

    @Test
    public void test3(){
        assertThat(testObject, is(not(hasItem(this))));
        testObject.add(this);

        assertThat(contextObject,either(is(nullValue())).or(is(this.context)));
        contextObject=this.context;
    }
}
