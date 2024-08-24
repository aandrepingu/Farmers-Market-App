package model.java;
import java.sql.CallableStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Connects to mysql database.
 */
public class FarmersMarketConnector {
	private Connection connection;
	/**
	 * Initialize the connection to the farmers market database
	 * @param databaseURL a database url of the form jdbc:subprotocol:subname
	 * @param username username for the database connection
	 * @param password password for the database connection
	 */
	public FarmersMarketConnector(String databaseURL, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.connection = DriverManager.getConnection(databaseURL, username, password);
            
            System.out.println("Connection established successfully.");

			
		}catch(Exception e) {
			e.printStackTrace();
			this.connection = null;
		}
	}
	
	/**
	 * Execute a query on the database and return the results.
	 * @param query Query string.
	 * @return a ResultSet view of the rows returned by the query, or null if query is an empty string.
	 * @throws SQLException if a database error occurs when executing the query
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		if(query.isBlank()) return null;
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }
	
	/**
	 * Execute an update to the database
	 * @param update SQL update statement (INSERT, UPDATE, DELETE)
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
	 * @throws SQLException if a database error occurs when executing the update.
	 */
	public int executeUpdate(String update) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(update);
    }
	
	/**
	 * Close connection to database.
	 * @throws SQLException if a database error occurs while closing.
	 */
	public void closeConnection() throws SQLException{
		this.connection.close();
	}
	
	
//	public static void main(String[] args) throws Exception{
//		FarmersMarketConnector c = new FarmersMarketConnector("jdbc:mysql://localhost:3306/farmers_markets", "appuser","ilovejava");
//         
//         // Create a CallableStatement
//         CallableStatement callableStatement = c.call("call calculate_distance(?, ?, ?, ?,?) ")   ;      
//         // Retrieve the output parameter
//         double distance = callableStatement.getDouble(5);
//         System.out.println("Distance: " + distance);
//		c.closeConnection();
//	}
}
