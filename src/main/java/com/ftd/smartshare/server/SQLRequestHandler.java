package com.ftd.smartshare.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ftd.smartshare.dto.DownloadRequestDto;
import com.ftd.smartshare.dto.FileDto;
import com.ftd.smartshare.dto.SummaryDto;
import com.ftd.smartshare.dto.UploadRequestDto;

public class SQLRequestHandler {

	private static final String URL = "jdb:postgresql://localhost:5432/postgres/smartshare";
	private static final String USERNAME = "postgres";
	private static final String PASSWORD = "bondstone";

	public SQLRequestHandler() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to load the postgreSQL Driver.");
		}
	}

	/**
	 * Get File from SQL Database
	 *
	 * @param downloadRequestDto JAXB annotated class representing the download
	 *                           request
	 * @return return FileDto, return null if not found or expired
	 */
	public synchronized FileDto getFile(DownloadRequestDto downloadRequest) {
		FileDto downloadedFile = null;
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);) {
			// Get file with the given file_name and password
			String sqlReturn = "SELECT * FROM smartshare.files WHERE file_name = ? AND password = ? ;";
			PreparedStatement preparedStatementReturn = connection.prepareStatement(sqlReturn);
			preparedStatementReturn.setString(1, downloadRequest.getFilename());
			preparedStatementReturn.setString(2, downloadRequest.getPassword());
			ResultSet resultSet = preparedStatementReturn.executeQuery();

			while (resultSet.next()) {
				// Make sure it is not expired
				if ((new Timestamp(System.currentTimeMillis())).before(resultSet.getTimestamp("expiry_time"))) {
					// get download information
					downloadedFile = new FileDto();
					downloadedFile.setFilename(resultSet.getString("file_name"));
					downloadedFile.setFile(resultSet.getBytes("file"));

					// -1 means the file has unlimited downloads
					if (resultSet.getInt("max_downloads") != -1) {
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

	/**
	 * Upload File into SQL Database
	 *
	 * @param uploadRequestDto JAXB annotated class representing the upload request
	 * @return return true if uploaded, false if not uploaded
	 */
	public synchronized boolean setFile(UploadRequestDto uploadRequest) {

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

			sql = "INSERT INTO smartshare.files (file_name, file, time_created, expiry_time, max_downloads, total_downloads, password) VALUES"
					+ "(?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, uploadRequest.getFilename());
			preparedStatement.setBytes(2, uploadRequest.getFile()); // File
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setTimestamp(4,
					new Timestamp(System.currentTimeMillis() + (uploadRequest.getExpirayTime() * 60000))); // int to
																											// mili (in
																											// minutes)
			preparedStatement.setInt(5, uploadRequest.getMaxDownloads()); // max_downloads;
			preparedStatement.setInt(6, 0);
			preparedStatement.setString(7, uploadRequest.getPassword());

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			System.out.println("SetFile Failed: ");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get summary from SQL Database
	 *
	 * @param downloadRequestDto JAXB annotated class representing the summary
	 *                           request
	 * @return return SummaryDto, return null if not found or expired
	 */
	public SummaryDto getSummary(DownloadRequestDto downloadRequest) {
		SummaryDto summary = null;
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);) {
			String sql = "SELECT * FROM smartshare.files WHERE file_name = ? AND password = ? ;";
			PreparedStatement preparedStatementReturn = connection.prepareStatement(sql);
			preparedStatementReturn.setString(1, downloadRequest.getFilename());
			preparedStatementReturn.setString(2, downloadRequest.getPassword());
			ResultSet resultSet = preparedStatementReturn.executeQuery();

			while (resultSet.next()) {
				// if not expired
				if ((new Timestamp(System.currentTimeMillis())).before(resultSet.getTimestamp("expiry_time"))) {
					// Fill summary information
					summary = new SummaryDto();

					summary.setTimeCreated(resultSet.getTimestamp("time_created"));
					summary.setDownloadsRemaining(
							resultSet.getInt("max_downloads") - resultSet.getInt("total_downloads"));
					// Convert to minutes
					summary.setTimeTilExpiration(((int) (new Timestamp(
							resultSet.getTimestamp("expiry_time").getTime() - System.currentTimeMillis()).getTime()))
							/ 60000);
				}
			}
		} catch (SQLException e) {
			System.out.println("getSummary Failed: ");
			e.printStackTrace();
		}

		return summary;
	}
}
