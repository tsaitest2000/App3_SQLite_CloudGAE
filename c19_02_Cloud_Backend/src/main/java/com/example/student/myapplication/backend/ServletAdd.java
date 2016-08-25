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
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletAdd extends HttpServlet {
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

      response.setContentType("text/plain");
      request.setCharacterEncoding("UTF-8");
      response.setCharacterEncoding("UTF-8");

      DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
      Entity entity = new Entity("Book"); //Book
      //即為資料表名稱
      entity.setProperty("title", "Android");
      entity.setProperty("author", "Kevin");
      entity.setProperty("price", new Random().nextInt(500) + 500);
      entity.setProperty("time", new Date().getTime()); //得到long型別的資料
      datastoreService.put(entity); //將entity實體放入資料庫中
      /******************************************************************************************/
      /*實作化Date類別時呼叫getTime()得到日期時間的原始RAW資料 → 以配合不同前端語法(Java, PHP, DotNet)*/
      /*不同的程式語言各自可以格式化日期時間的RAW資料。配套：可同時寫入RAW資料與Java格式的時間值到兩個欄位*/
      /******************************************************************************************/
      response.getWriter().print("Add Successfully"); //如同System.out.println一般的功能
   }

}
