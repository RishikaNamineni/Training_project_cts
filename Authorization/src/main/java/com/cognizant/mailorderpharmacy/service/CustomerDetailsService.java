package com.cognizant.mailorderpharmacy.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cognizant.mailorderpharmacy.dao.UserDAO;
import com.cognizant.mailorderpharmacy.exception.UnauthorizedException;
import com.cognizant.mailorderpharmacy.model.UserData;

/**Service class*/
@Service
public class CustomerDetailsService implements UserDetailsService {
	@Autowired
	private UserDAO userdao;

	/**
	 * @param String
	 * @return User 
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String uid) {
		
		try
		{
			
			Optional<UserData> user=userdao.findById(uid);
			if(user.isPresent()) {
				user.get().getUname();
				return new User(user.get().getUserid(), user.get().getUpassword(), new ArrayList<>());
			}
			else {
				throw new UsernameNotFoundException("User id not found");
			}
		}
		catch (Exception e) {
			throw new UnauthorizedException("UsernameNotFoundException");
		}	
		
		
	}

}
