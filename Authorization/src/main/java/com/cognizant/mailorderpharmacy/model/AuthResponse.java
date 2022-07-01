package com.cognizant.mailorderpharmacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**Model class for the business details*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	/**
	 *Id for user 
	 */
	private String uid;
	/**
	 *Name of the user
	 */
	private String name;
	/**
	 *Validity check
	 */
	private boolean isValid;
}
