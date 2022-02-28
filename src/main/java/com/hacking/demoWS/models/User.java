package com.hacking.demows.models;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String user;
	private String pass;
	private String role;
	
	public User(String user, String pass, String role){
		setUser(user);
		setPass(pass);
		setRole(role);
	}
	public User(int id, String user, String pass, String role){
		this.id = id;
		setUser(user);
		setPass(pass);
		setRole(role);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
    }
    public void setPass(String pass) {
		this.pass = pass;
	}
	public String getRole() {
		return role;
    }
    public void setRole(String role) {
		this.role = role;
	}
	@Override
    public String toString() {
        String value = "user : " + user + "\npass : " + pass;
        return value;
    }
}