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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static ArrayList<String> comments;
  private static Gson gson;

  /** 
   * Initializes comments and gson variables
   */
  public DataServlet() {
    comments = new ArrayList<String>();
    gson = new Gson();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = gson.toJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("comment");
    comments.add(text);
    response.setContentType("text/html;");
    response.getWriter().println(comments);
  }
  
}
