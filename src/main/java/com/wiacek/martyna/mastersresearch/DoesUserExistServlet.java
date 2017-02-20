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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DoesUserExistServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws IOException, ServletException {
      
	  String userName = req.getParameter("username");
	    
	    
	    final String selectSql = "SELECT COUNT(*) AS ct FROM users WHERE username='"+userName+"'";

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
	    } else {
	      // Set the url with the local MySQL database connection url when running locally
	      url = System.getProperty("ae-cloudsql.local-database-url");
	    }
	    log("connecting to: " + url);
	    try (Connection conn = DriverManager.getConnection(url);) {
	    

	      try (ResultSet rs = conn.prepareStatement(selectSql).executeQuery()) {
	        while (rs.next()) {
	        	String rez = rs.getString("ct");
	        	
	        	if (rez.equals("0")) {
	        		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	        	} else {
	        		resp.setStatus(HttpServletResponse.SC_OK);
	        	}
	        }
	      }
	    } catch (SQLException e) {
	      throw new ServletException("SQL error", e);
	    }

  }
}
