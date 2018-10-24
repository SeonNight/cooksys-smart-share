package com.ftd.smartshare.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ftd.smartshare.dto.DownloadRequestDto;
import com.ftd.smartshare.dto.SummaryDto;
import com.ftd.smartshare.dto.UploadRequestDto;

public class SQLRequestHandler {

	private static final String URL = "jdb:postgresql://localhost:5432/postgres/smartshare";
	private static final String USERNAME = "postgres";
	private static final String PASSWORD = "bondstone";

	public SQLRequestHandler() {
	}

	public boolean connect() {
		try {
			Class.forName("org.postgresql.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to load the postgreSQL Driver.");
			return false;
		}
	}

	public UploadRequestDto getFile(DownloadRequestDto downloadRequest) {
		UploadRequestDto downloadedFile = null;
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);) {
			String sqlReturn = "SELECT * FROM smartshare.files WHERE file_name = ? AND password = ? ;";
			PreparedStatement preparedStatementReturn = connection.prepareStatement(sqlReturn);
			preparedStatementReturn.setString(1, downloadRequest.getFilename());
			preparedStatementReturn.setString(2, downloadRequest.getPassword());
			ResultSet resultSet = preparedStatementReturn.executeQuery();

			while (resultSet.next()) {
				// Make sure it is not expired
				if ((new Timestamp(System.currentTimeMillis())).before(resultSet.getTimestamp("expiry_time"))) {
					// get download information
					downloadedFile = new UploadRequestDto();
					downloadedFile.setFilename(resultSet.getString("file_name"));
					downloadedFile.setFile(resultSet.getBytes("file"));

					// If max downloads reached (after this download) delete
					if (resultSet.getInt("max_downloads") <= resultSet.getInt("total_downloads") + 1) {
						PreparedStatement preparedStatement = connection
								.prepareStatement("DELETE FROM smartshare.files WHERE id = ?;");
						preparedStatement.setInt(1, resultSet.getInt("id"));
						preparedStatement.execute();
					} else { // If max downloads not reached
						// Increment total_downloads
						PreparedStatement preparedStatement = connection
								.prepareStatement("UPDATE smartshare.files SET total_downloads = ? WHERE id = ?;");
						preparedStatement.setInt(1, (resultSet.getInt("total_downloads") + 1));
						preparedStatement.setInt(2, resultSet.getInt("id"));
						preparedStatement.execute();
					}

				} else { // If it has expired delete entry
					PreparedStatement preparedStatement = connection
							.prepareStatement("DELETE FROM smartshare.files WHERE id = ?;");
					preparedStatement.setInt(1, resultSet.getInt("id"));
					preparedStatement.execute();
				}
			}

		} catch (SQLException e) {
			System.out.println("getFile Failed: ");
			e.printStackTrace();
		}

		return downloadedFile;
	}

	public boolean setFile(UploadRequestDto uploadRequest) {
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);) {

			// Check to make sure there isn't already a file with the same name
			// If there is return false
			String sql = "SELECT * FROM smartshare.files WHERE file_name = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, uploadRequest.getFilename());
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				if (resultSet.getString("file_name").equals(uploadRequest.getFilename())) {
					// If the file is expired, delete file and upload the new one
					if ((new Timestamp(System.currentTimeMillis())).after(resultSet.getTimestamp("expiry_time"))) {
						preparedStatement = connection.prepareStatement("DELETE FROM smartshare.files WHERE id = ?;");
						preparedStatement.setInt(1, resultSet.getInt("id"));
						preparedStatement.execute();
					} else {
						return false;
					}
				}
			}

			sql = "INSERT INTO smartshare.files (id, file_name,"
					+ "file, time_created, expiry_time, max_downloads, total_downloads, password) VALUES"
					+ "(?, ?, ?, ?, ?, ?, ? ,?);";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, 1); // Set this to serial?
			preparedStatement.setString(2, uploadRequest.getFilename());
			preparedStatement.setBytes(3, uploadRequest.getFile()); // File
			preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setTimestamp(5,
					new Timestamp(System.currentTimeMillis() + (uploadRequest.getExpirayTime() * 60000))); // int to
																											// mili (in
																											// minutes)
			preparedStatement.setInt(6, uploadRequest.getMaxDownloads()); // max_downloads;
			preparedStatement.setInt(7, 0);
			preparedStatement.setString(8, uploadRequest.getPassword());

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			System.out.println("SetFile Failed: ");
			e.printStackTrace();
		}
		return false;
	}

	public SummaryDto getSummary(DownloadRequestDto downloadRequest) {
		SummaryDto summary = null;
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);) {
			String sqlReturn = "SELECT * FROM smartshare.files WHERE file_name = ? AND password = ? ;";
			PreparedStatement preparedStatementReturn = connection.prepareStatement(sqlReturn);
			preparedStatementReturn.setString(1, downloadRequest.getFilename());
			preparedStatementReturn.setString(2, downloadRequest.getPassword());
			ResultSet resultSet = preparedStatementReturn.executeQuery();

			while (resultSet.next()) {
				// Fill summary information
				summary = new SummaryDto();
				
				summary.setTimeCreated(resultSet.getTimestamp("time_created"));
				summary.setDownloadsRemaining(resultSet.getInt("max_downloads") - resultSet.getInt("total_downloads"));
				summary.setTimeTilExpiration(((int) (new Timestamp(resultSet.getTimestamp("expiry_time").getTime() - System.currentTimeMillis()).getTime())) / 60000);
			}
		} catch (SQLException e) {
			System.out.println("getSummary Failed: ");
			e.printStackTrace();
		}

		return summary;
	}
}
