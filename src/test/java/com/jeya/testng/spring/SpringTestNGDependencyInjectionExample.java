package com.jeya.testng.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration("context.xml")
public class SpringTestNGDependencyInjectionExample extends AbstractTestNGSpringContextTests {
	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private Foo foo;
	
	@Test
	public void verifyFooName() {
		System.out.println("verifyFooName: Is foo not null? " + (foo != null));
	}
}
