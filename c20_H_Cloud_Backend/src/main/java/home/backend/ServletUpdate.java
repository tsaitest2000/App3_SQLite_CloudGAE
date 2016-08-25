/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package home.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUpdate extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain");

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("PirateKing", Long.parseLong(request.getParameter("key")));

        try {
            Entity entity = datastoreService.get(key);
            entity.setProperty("name", request.getParameter("name"));
            entity.setProperty("price", Integer.parseInt(request.getParameter("price")));
//            entity.setProperty("time", new Date().getTime()); //我：時間值不予更新
            datastoreService.put(entity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace(response.getWriter());
        }
        response.getWriter().println("~ Update OK ~");
    }

}
