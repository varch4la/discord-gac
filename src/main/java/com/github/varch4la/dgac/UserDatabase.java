package com.github.varch4la.dgac;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {
	private final Connection connection;

	public UserDatabase(File file) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		try (Statement statement = connection.createStatement()) {
			statement.execute(
					"CREATE TABLE users (\"user\" INTEGER NOT NULL,optout INTEGER DEFAULT (true) NOT NULL,CONSTRAINT users_pk PRIMARY KEY (\"user\"))");
		}
	}
}
