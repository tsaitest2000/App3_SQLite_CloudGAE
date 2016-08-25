package com.example.student.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletAddEntity extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      resp.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Entity entity = new Entity("Book");
      entity.setProperty("title", "Programming");
      entity.setProperty("author", "Kevin");
      entity.setProperty("price", new Random().nextInt(500) + 300);
      entity.setProperty("time", new Date().getTime());
      datastoreService.put(entity);

      resp.getWriter().println("Add Successfully");
   }

}
