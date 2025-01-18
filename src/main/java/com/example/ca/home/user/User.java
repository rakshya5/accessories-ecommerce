package com.example.ca.home.user;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.ca.home.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(unique=true) // this is UniqueConstraint
	private String username;
	
	private String fullname;
	
	private String email;
	
	private String password;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	private String session; //temporary password
	
	@Enumerated(value=EnumType.STRING)
	private UserType type;
	
//Getters and setters
	public int getId() {
		return id;
	}

	

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	// Method to check if the user is an admin
    public boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }
	
	
}
