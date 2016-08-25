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
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

      resp.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("Book");
      /**************************************************************/
      query.addSort("time", Query.SortDirection.DESCENDING); //降冪排列

      List<Book> list = new ArrayList<>();
      for (Entity entity : datastoreService.prepare(query).asIterable()) {
         Book book = new Book();
         book.setKey(entity.getKey().getId());
         book.setTitle(entity.getProperty("title").toString());
         book.setAuthor(entity.getProperty("author").toString());
         book.setPrice(Integer.parseInt(entity.getProperty("price").toString()));
         book.setTime(Long.parseLong(entity.getProperty("time").toString()));
         list.add(book);
      }

      Gson gson = new Gson();
      String json = gson.toJson(list);
      resp.getWriter().println(json);
   }

}
