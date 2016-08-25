/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package home.backend;

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

import po.Figure;

public class ServletQuery extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain");

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query("PirateKing");
        query.addSort("time", Query.SortDirection.ASCENDING);

        List<Figure> list = new ArrayList<>();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            Figure figure = new Figure(entity);
            list.add(figure);
        }

        Gson gson = new Gson();
        String toJson = gson.toJson(list);
        response.getWriter().println(toJson);
    }

}
