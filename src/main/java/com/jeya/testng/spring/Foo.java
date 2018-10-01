package com.jeya.testng.spring;

public class Foo {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}

/**
Inject this bean to test instance and then verify its value
*/