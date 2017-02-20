/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wiacek.martyna.mastersresearch;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddUserServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) 
      throws IOException, ServletException {
      
	  String userName = req.getParameter("username");
	    String workStatus = req.getParameter("workStatus");
	    String sex = req.getParameter("sex");
	    String birthYear = req.getParameter("birthYear");
	    String education = req.getParameter("education");
	    String isFromWarsaw = req.getParameter("isFromWarsaw");
	    String relationshipStatus = req.getParameter("relationship");
	    String car = req.getParameter("transportation");
	    
	    final String createTableSql = "CREATE TABLE IF NOT EXISTS users ( user_id INT NOT NULL "
		        + "AUTO_INCREMENT, username VARCHAR(46) NOT NULL, work_status VARCHAR(46) NOT NULL, "
		        + "sex VARCHAR(46) NOT NULL,  birth_year VARCHAR(46) NOT NULL, education VARCHAR(46) NOT NULL, "
		        + "warsaw VARCHAR(46) NOT NULL, relationship VARCHAR(46) NOT NULL, transportation VARCHAR(46) NOT NULL, "
		        + " timestamp DATETIME NOT NULL, "
		        + "PRIMARY KEY (user_id) )";
		    final String createUsersSql = "INSERT INTO users (username, work_status, sex, birth_year, "
		    		+ "education, warsaw, relationship, transportation, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		    PrintWriter out = resp.getWriter();
		    resp.setContentType("text/plain");
		    String url;
		    if (System
		        .getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
		      // Check the System properties to determine if we are running on appengine or not
		      // Google App Engine sets a few system properties that will reliably be present on a remote
		      // instance.
		      url = System.getProperty("ae-cloudsql.cloudsql-database-url");
		      try {
		        // Load the class that provides the new "jdbc:google:mysql://" prefix.
		        Class.forName("com.mysql.jdbc.GoogleDriver");
		      } catch (ClassNotFoundException e) {
		        throw new ServletException("Error loading Google JDBC Driver", e);
		      }
		    }else {
			      // Set the url with the local MySQL database connection url when running locally
			      url = System.getProperty("ae-cloudsql.local-database-url");
			    }
		    log("connecting to: " + url);
		    try (Connection conn = DriverManager.getConnection(url);
		        PreparedStatement statementCreateVisit = conn.prepareStatement(createUsersSql)) {
		      conn.createStatement().executeUpdate(createTableSql);
		      statementCreateVisit.setString(1, userName);
		      statementCreateVisit.setString(2, workStatus);
		      statementCreateVisit.setString(3, sex);
		      statementCreateVisit.setString(4, birthYear);
		      statementCreateVisit.setString(5, education);
		      statementCreateVisit.setString(6, isFromWarsaw);
		      statementCreateVisit.setString(7, relationshipStatus);
		      statementCreateVisit.setString(8, car);
		      statementCreateVisit.setTimestamp(9, new Timestamp(new Date().getTime()));
		      statementCreateVisit.executeUpdate();
		      

		      resp.setStatus(HttpServletResponse.SC_CREATED);
		    } catch (SQLException e) {
		    	  resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		    	 throw new ServletException("SQL error", e);
		    }

  }
}
