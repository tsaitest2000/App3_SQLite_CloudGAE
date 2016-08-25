package lab.app_cloud.po;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Book {

   private long key;
   private String title;
   private String author;
   private int price;
   private long time;

   public Book() {

   }

   public Book(String title, String author, int price) {
      this.title = title;
      this.author = author;
      this.price = price;
   }

   public Book(long key, String title, String author, int price) {
      this.key = key;
      this.title = title;
      this.author = author;
      this.price = price;
   }

   @Override
   public String toString() {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
      String data = String.format("書名：%s\tkey：%d\n作者：%s\t價格：%d\n上架時間：%s", title, key, author, price, f.format(new Date(time)));
      return data;
   }

   public String getAuthor() {
      return author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public long getKey() {
      return key;
   }

   public void setKey(long key) {
      this.key = key;
   }

   public int getPrice() {
      return price;
   }

   public void setPrice(int price) {
      this.price = price;
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

}
