package backend.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class VersionServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Database stuff
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/sesirental?user=root&password=");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM frou");
			while(rs.next()) {
				System.out.println(rs.getString("name"));
			}
			// Now do something with the ResultSet ....
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {}
				conn = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {}
				stmt = null;
			}
		}
		// Write to client
		response.getWriter().append("0.1").flush();
	}

}