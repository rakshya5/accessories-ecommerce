package com.example.ca.validation;

public class ValidationError {
	private String email;
	private String username;
	private String login;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean hasErrors() {
		return email != null || username != null || login != null;
	}

}