package com.cognizant.mailorderpharmacy.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.mailorderpharmacy.dao.UserDAO;
import com.cognizant.mailorderpharmacy.model.AuthResponse;
import com.cognizant.mailorderpharmacy.model.UserData;
import com.cognizant.mailorderpharmacy.service.CustomerDetailsService;
import com.cognizant.mailorderpharmacy.service.JwtUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(produces = "application/json", value = "Creating and validating the Jwt token")
public class AuthController {

	@Autowired
	private JwtUtil jwtutil;
	@Autowired
	private CustomerDetailsService custdetailservice;
	@Autowired
	private UserDAO userservice;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@ApiOperation(value = "Verify credentials and generate JWT Token", response = ResponseEntity.class)
	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@RequestBody UserData userlogincredentials) {
		//Generates token for login
		final UserDetails userdetails = custdetailservice.loadUserByUsername(userlogincredentials.getUserid());
		String uid = "";
		String generateToken = "";
		if (userdetails.getPassword().equals(userlogincredentials.getUpassword())) {
			uid = userlogincredentials.getUserid();
			generateToken = jwtutil.generateToken(userdetails);
			LOGGER.info("Token: " + generateToken);
			return new ResponseEntity<>(new UserData(uid, null, null, generateToken), HttpStatus.OK);
		} else {
			LOGGER.info("At Login : ");
			LOGGER.error("Not Accesible");
			return new ResponseEntity<>("Not Accesible", HttpStatus.FORBIDDEN);
		}
	}
	
	@ApiOperation(value = "Verify credentials and generate JWT Token", response = ResponseEntity.class)
	@PostMapping(value = "/register")
	public ResponseEntity<Object> register(@RequestBody UserData user) {
			LOGGER.info("before find : ");
			try {
				Optional<UserData> user1=userservice.findById(user.getUserid());
				if(user1.isPresent()) {
					
					return new ResponseEntity<>(false, HttpStatus.CONFLICT);
				}
				else {
					userservice.save(user);
					LOGGER.info("User Is Registered");
					return new ResponseEntity<>(true, HttpStatus.OK);
				}
			}
			catch(Exception e) {
				userservice.save(user);
				LOGGER.info("User Is Registered");
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
			
	}
	
	@ApiOperation(value = "Validate JWT Token", response = ResponseEntity.class)
	@GetMapping(value = "/validate")
	public ResponseEntity<Object> getValidity(@RequestHeader("Authorization") final String token) {
		//Returns response after Validating received token
		String token1 = token.substring(7);
		AuthResponse res = new AuthResponse();
		if (Boolean.TRUE.equals(jwtutil.validateToken(token1))) {
			res.setUid(jwtutil.extractUsername(token1));
			res.setValid(true);
			Optional<UserData> user1=userservice.findById(jwtutil.extractUsername(token1));
			if(user1.isPresent())
				res.setName(user1.get().getUname());
		} else {
			res.setValid(false);
			LOGGER.info("At Validity : ");
			LOGGER.error("Token Has Expired");
		}
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

}