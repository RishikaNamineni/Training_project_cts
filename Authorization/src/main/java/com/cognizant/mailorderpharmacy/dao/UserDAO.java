package com.cognizant.mailorderpharmacy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.mailorderpharmacy.model.UserData;




/**JPA Repository which interacts with database*/
@Repository
public interface UserDAO extends JpaRepository<UserData, String> {

}
