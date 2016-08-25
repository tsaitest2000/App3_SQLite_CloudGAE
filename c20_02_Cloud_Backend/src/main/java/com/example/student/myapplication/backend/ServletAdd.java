/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.student.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletAdd extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain");

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Entity entity = new Entity("Book");
        entity.setProperty("title", request.getParameter("title"));
        entity.setProperty("author", request.getParameter("author"));
        entity.setProperty("price", Integer.parseInt(request.getParameter("price").toString()));
        entity.setProperty("time", new Date().getTime());

        datastoreService.put(entity);
        response.getWriter().print("Add OK");
    }

}
