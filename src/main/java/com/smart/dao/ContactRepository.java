package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// current-page = page
	// contacts per page = 5
	public Page<Contact> findContactByUserId(int userId,Pageable pageable);

}
