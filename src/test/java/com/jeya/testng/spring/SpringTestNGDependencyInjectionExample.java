package com.jeya.testng.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Context file is provided using the type annotation @ContextConfiguration wuth file name context.xml as the value.
 * Context is loaded by this and then set to the protected member application context.
 * @author JJeyatharsini
 */
@ContextConfiguration("context.xml")
public class SpringTestNGDependencyInjectionExample extends AbstractTestNGSpringContextTests
/**
 * Through the extends AbstractTestNGSpringContextTests, dependencies are injected into our test instance.
 */
{
	/**
	 * Spring automatically injects BeanFactory bean into it.
	 */
	@Autowired
	private BeanFactory beanFactory;
	
	/**
	 * Bean foo is injected
	 */
	@Autowired
	private Foo foo;
	
	/**
	 * verifies that the foo bean is injected and its name is same as the one set in the context file
	 */
	@Test
	public void verifyFooName() {
		System.out.println("verifyFooName: Is foo not null? " + (foo != null));
		Assert.assertNotNull(foo);
        System.out.println("verifyFooName: Foo name is '" + foo.getName() + "'");
        Assert.assertEquals(foo.getName(), "TestNG Spring");
	}
	
	/**
	 * verifies that the bean factory is injected
	 */
	@Test
	public void verifyBeanFactory()
	{
		System.out.println("verifyBeanFactory: Is bean factory not null? " + (beanFactory != null));
		Assert.assertNotNull(beanFactory);
	}
}
