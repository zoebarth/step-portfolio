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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginStatusServlet extends HttpServlet {
  private static final UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject json = new JsonObject();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/index.html#contact-me";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      json.addProperty("loggedIn", true);
      json.addProperty("userEmail", userEmail);
      json.addProperty("logoutUrl", logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/index.html#contact-me";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      json.addProperty("loggedIn", false);
      json.addProperty("loginUrl", loginUrl);
    }
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
