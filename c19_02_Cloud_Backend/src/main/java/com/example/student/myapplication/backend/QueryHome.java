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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueryHome extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      resp.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("Book");

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
      String toJson = gson.toJson(list);
      resp.getWriter().println(toJson);
   }

}
