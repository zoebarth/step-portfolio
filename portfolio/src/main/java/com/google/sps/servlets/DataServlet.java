// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that handles posting and displaying comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    String limitString = request.getParameter("limit");

    // Convert the input to an int.
    int limit;
    try {
      limit = Integer.parseInt(limitString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + limitString);
      limit = -1;
    }

    ArrayList<String> comments = new ArrayList<String>();
    int i = 0;
    for (Entity entity : results.asIterable()) {
      if (i < limit || limit == -1) {
        String text = (String) entity.getProperty("text");
        comments.add(text);
        i++;
      } else {
        break;
      }
    }

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    Entity comment = new Entity("Comment");
    comment.setProperty("text", text);
    comment.setProperty("timestamp", timestamp);
    
    datastore.put(comment);
    
    response.sendRedirect("/index.html#contact-me");
  }
  
}
