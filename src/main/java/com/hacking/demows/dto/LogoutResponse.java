package com.hacking.demows.dto;

import java.io.Serializable;

public class LogoutResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String result;

	public LogoutResponse(String result) {
		this.result = result;
	}

	public String getResult() {
		return this.result;
	}
}