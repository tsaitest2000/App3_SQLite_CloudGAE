package lab.app_cloud.po;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Book {

   private long key;
   private String title;
   private String author;
   private int price;
   private long time;

   @Override
   public String toString() {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
      String data = String.format(
         "書名：%s\tkey：%d\n作者：%s\t價格：%d\n上架時間：%s", title, key, author, price, f.format(new Date(time)));
      return data;
   } /*實體化Date物件時給入long型別的time值 → 就不是當下的時間值*/

   public void setKey(long key) {
      this.key = key;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public void setPrice(int price) {
      this.price = price;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public long getKey() {
      return key;
   }

   public String getAuthor() {
      return author;
   }

   public String getTitle() {
      return title;
   }

   public int getPrice() {
      return price;
   }

   public long getTime() {
      return time;
   }

}
