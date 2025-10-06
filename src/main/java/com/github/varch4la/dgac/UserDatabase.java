package com.github.varch4la.dgac;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.api.entities.User;

public class UserDatabase {
	private final Connection connection;

	public UserDatabase(File file) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		try (Statement statement = connection.createStatement()) {
			statement.execute(
					"CREATE TABLE IF NOT EXISTS users (\"user\" INTEGER NOT NULL, optin INTEGER DEFAULT (false) NOT NULL,CONSTRAINT users_pk PRIMARY KEY (\"user\"))");
		}
	}

	public void optOut(User user) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("delete from users where \"user\" = ?")) {
			statement.setLong(1, user.getIdLong());
			statement.execute();
		}
	}

	public void optIn(User user) throws SQLException {
		try (PreparedStatement statement = connection
				.prepareStatement("insert or replace into users (\"user\", optin) values (?, true)")) {
			statement.setLong(1, user.getIdLong());
			statement.execute();
		}
	}

	public boolean isOptedIn(User user) throws SQLException {
		try (PreparedStatement statement = connection
				.prepareStatement("select * from users where \"user\" = ? and optin = true")) {
			statement.setLong(1, user.getIdLong());
			try (ResultSet set = statement.executeQuery()) {
				return set.next();
			}
		}
	}
}
