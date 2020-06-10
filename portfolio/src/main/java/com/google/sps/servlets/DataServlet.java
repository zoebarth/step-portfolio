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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting and displaying comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final Gson gson = new Gson();
  private final UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    String limitString = request.getParameter("limit");

    // Use FetchOptions to limit number of comments.
    FetchOptions fetch;
    try {
      fetch = FetchOptions.Builder.withLimit(Integer.parseInt(limitString));
    } catch (NumberFormatException e) {
      fetch = FetchOptions.Builder.withDefaults();
    }

    // Add comments to ArrayList.
    ArrayList<String> comments = new ArrayList<String>();
    for (Entity entity : results.asIterable(fetch)) {
      String text = (String) entity.getProperty("text");
      comments.add(text);
    }

    // Send response with comments written as json.
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();
    String userEmail = userService.getCurrentUser().getEmail();

    Entity comment = new Entity("Comment");
    comment.setProperty("text", text);
    comment.setProperty("timestamp", timestamp);
    comment.setProperty("email", userEmail);

    datastore.put(comment);
  }
}
