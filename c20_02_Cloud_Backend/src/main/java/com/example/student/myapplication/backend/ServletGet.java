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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletGet extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

      resp.setContentType("text/plain");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Key key = KeyFactory.createKey("Book", 5075880531460096L); //long值是取自網頁的查詢結果

      try {
         Entity entity = datastoreService.get(key);
         // Mapper(Hibernate)：從資料庫取出後，其欄位值填入物件的屬性 此操作稱之為：Mapper
         /*****************************************************************************************/
         Book book = new Book(entity); //實體化Book型別物件時，輸入Entity型別物件為參數，詳見Book.java
         resp.getWriter().print(book); //詳見Book.java中的toString()方法
         /*****************************************************************************************/
      } catch (EntityNotFoundException e) {
         e.printStackTrace(resp.getWriter()); //實用的方法，記起來，好用!
      }
   }

}
