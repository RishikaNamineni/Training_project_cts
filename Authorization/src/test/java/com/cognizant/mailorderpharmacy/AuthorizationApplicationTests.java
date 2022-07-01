package com.cognizant.mailorderpharmacy;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cognizant.mailorderpharmacy.controller.AuthController;


@SpringBootTest
 class AuthorizationApplicationTests {
	
	@Autowired
	AuthController obj;
	@Test
	void contextLoads() {
		assertNotNull(obj);
	}
}
