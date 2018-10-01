package com.jeya.testng.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("context.xml")
public class SpringTestNGContextCacheExample extends AbstractTestNGSpringContextTests {

	@Autowired
	private Foo foo;

	private String nameFromSpring;

	private static final String MODIFIED_FOO_NAME = "TestNG Spring Changed";

	private ApplicationContext dirtiedApplicationContext;

	@BeforeClass
	private void saveFooName() {
		nameFromSpring = foo.getName();
		System.out.println("BeforeClass: foo name is '" + nameFromSpring + "'");
		Assert.assertEquals(nameFromSpring, "TestNG Spring");
	}
	
	@Test
	@DirtiesContext
	public void removeFromCache()
	{
		this.dirtiedApplicationContext = super.applicationContext;
		String newFooName = "New foo name";
		foo.setName(newFooName);
		System.out.println("removeFromCache: foo name changed to '" + foo.getName() + "'");
		System.out.println("removeFromCache: annotated @DirtiesContext so context will be marked for removal in afterMethod ");
	}
	
	@Test(dependsOnMethods = {"removeFromCache"})
	public void verifyContextNew()
	{
		System.out.println("verifyContextNew: is context re-cached? " + (dirtiedApplicationContext != applicationContext));
		System.out.println("verifyContextNew: foo name is '" + foo.getName() + "'");
		Assert.assertNotSame(super.applicationContext, this.dirtiedApplicationContext, "The application context should have been 'dirtied'.");
		Assert.assertEquals(foo.getName(), nameFromSpring);
		
        this.dirtiedApplicationContext = super.applicationContext;
        
        foo.setName(MODIFIED_FOO_NAME);
        System.out.println("verifyContextNew: modify foo name to '" + MODIFIED_FOO_NAME + "'");
	}
}