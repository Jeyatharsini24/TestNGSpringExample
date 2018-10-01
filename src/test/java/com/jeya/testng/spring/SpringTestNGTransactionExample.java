package com.jeya.testng.spring;

import static org.springframework.test.context.transaction.TestTransaction.end;
import static org.springframework.test.context.transaction.TestTransaction.flagForCommit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration("context.xml")
public class SpringTestNGTransactionExample extends AbstractTransactionalTestNGSpringContextTests
/**
 * extends AbstractTransactionalTestNGSpringContextTests is important for the tests to automatically
 * run within a transactional.
 */
{
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String method;

	/**
	 * Configuration method to capture the method name which will be used later to
	 * run test based asserts
	 * @param method Method
	 */
	@BeforeMethod
	public void saveMethodName(Method method) {
		this.method = method.getName();
	}

	/**
	 * Assure that JdbcTemplate bean is injected and not null
	 */
	@Test
	public void tran() {
		System.out.println("tran: verify JdbcTemplate is not null");
		Assert.assertNotNull(jdbcTemplate);
	}

	/**
	 * It executes before the start of transaction. All the data from
	 * employee table is deleted and recreated
	 */
	@BeforeTransaction
	public void beforeTransaction() {
		System.out.println("before transaction starts, delete all employees and re-run employee script");
		deleteFromTables("employee");
		executeSqlScript("classpath:/com/jeya/testng/spring/data.sql", false);
	}

	/**
	 * New employee is inserted and committed explicitly
	 * The newly added employee will be there after the transaction
	 */
	@Test
	public void insertEmployeeAndCommit() {
		System.out.println("insertEmployeeAndCommit: insert employee 'Bill' and commit");
		String emp = "Bill";
		jdbcTemplate.update("insert into employee(name) values (?)", emp);
		Assert.assertEquals(countRowsInTableWhere("employee", "name='" + emp + "'"), 1);
		flagForCommit();
		end();
	}

	/**
	 * New employee is inserted. Since transaction gets rolled back after the test's execution,
	 * new employee will not be there in @AfterTransaction method
	 */
	@Test
	public void insertEmployeeWithRollbackAsDefault() {
		System.out.println("insertEmployeeWithRollbackAsDefault: insert employee 'Bill', rollback by default");
		String emp = "Bill";
		jdbcTemplate.update("insert into employee(name) values (?)", emp);
		Assert.assertEquals(countRowsInTableWhere("employee", "name='" + emp + "'"), 1);
	}

	/**
	 * Default behavior of rollback is overridden by using the annotation @Rollback(false)
	 * Newly added value will be there
	 */
	@Test
	@Rollback(false)
	public void insertEmployeeWithCommitAsDefault() {
		System.out.println("insertEmployeeWithCommitAsDefault: insert employee 'Bill', commit by default");
		String emp = "Bill";
		jdbcTemplate.update("insert into employee(name) values (?)", emp);
		Assert.assertEquals(countRowsInTableWhere("employee", "name='" + emp + "'"), 1);
	}

	/**
	 * An SQL script can be run with @Sql annotation.
	 * Additional_data.sql is passed as value.
	 */
	@Test
	@Sql("additional_data.sql")
	public void insertEmployeeUsingSqlAnnotation() {
		System.out.println(
				"insertEmployeeUsingSqlAnnotation: run additional sql using @sql annotation, rollback by default");
		Assert.assertEquals(countRowsInTableWhere("employee", "name='John'"), 1);
	}

	/**
	 * Here all the asserts are run to make sure the expected behavior
	 */
	@AfterTransaction
	public void afterTransaction() {
		switch (method) {
		case "insertEmployeeAndCommit":
			assertEmployees("Bill", "Joe", "Sam");
			System.out.println("insertEmployeeAndCommit: employees found: 'Bill', 'Joe' and 'Sam'");
			break;
		case "insertEmployeeWithRollbackAsDefault":
			System.out.println("insertEmployeeWithRollbackAsDefault: employees found: 'Joe' and 'Sam'");
			assertEmployees("Joe", "Sam");
			break;
		case "insertEmployeeWithCommitAsDefault":
			System.out.println("insertEmployeeWithCommitAsDefault: employees found: 'Bill', 'Joe' and 'Sam'");
			assertEmployees("Bill", "Joe", "Sam");
			break;
		case "tran":
			break;
		case "insertEmployeeUsingSqlAnnotation":
			System.out.println("insertEmployeeWithRollbackAsDefault: employees found: 'Joe' and 'Sam'");
			assertEmployees("Joe", "Sam");
			break;
		default:
			throw new RuntimeException("missing 'after transaction' assertion for test method: " + method);
		}
	}

	private void assertEmployees(String... users) {
		List expected = Arrays.asList(users);
		Collections.sort(expected);
		List actual = jdbcTemplate.queryForList("select name from employee", String.class);
		Collections.sort(actual);
		System.out.println("Employees found: " + actual);
		Assert.assertEquals(actual, expected);
	}
}