package com.dajia.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dajia.Application;
import com.dajia.domain.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
public class RepositoryTests {

	@Autowired
	private PropertyRepo repo;

	@Test
	public void testPropertyRepo() throws Exception {
		Property p = repo.findByPropertyKey("appkey");
		System.out.println(p.propertyValue);
	}
}
