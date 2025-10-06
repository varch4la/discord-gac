package com.github.varch4la.dgac.cfg;

public class Config {
	private final String token;
	private final String database;

	public Config() {
		this.token = "token";
		this.database = "bot.sqlite3";
	}

	public String getDatabase() {
		return database;
	}

	public String getToken() {
		return token;
	}
}
