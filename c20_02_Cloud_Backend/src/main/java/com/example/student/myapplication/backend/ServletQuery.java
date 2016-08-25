/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.student.myapplication.backend;

import com.example.student.myapplication.backend.po.Book;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletQuery extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

      response.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("Book");
      query.addSort("time", Query.SortDirection.DESCENDING);

      List<Book> list = new ArrayList<>();
      for (Entity entity : datastoreService.prepare(query).asIterable()) {
         Book book = new Book(entity);
         list.add(book);
      }
      response.getWriter().println(new Gson().toJson(list));
   }

}
