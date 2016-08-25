/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.student.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUpdate extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

      response.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Key key = KeyFactory.createKey("Book", Long.parseLong(request.getParameter("key")));

      try {
         Entity book = datastoreService.get(key);
         book.setProperty("title", request.getParameter("title"));
         book.setProperty("author", request.getParameter("author"));
         book.setProperty("price", Integer.parseInt(request.getParameter("price")));
         book.setProperty("time", new Date().getTime());

         datastoreService.put(book);
         response.getWriter().print("Update OK");
      } catch (Exception e) {
         e.printStackTrace(response.getWriter()); //實用的方法，記起來，好用!
      }
   }

}
