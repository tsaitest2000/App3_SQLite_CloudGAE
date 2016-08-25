package po;

import com.google.appengine.api.datastore.Entity;

public class Figure {

   private long key;
   private String name;
   private int price;
   private long time;

   public Figure() {
   }

   public Figure(Entity entity) {
      this.key = entity.getKey().getId();
      this.name = entity.getProperty("name").toString();
      this.price = Integer.parseInt(entity.getProperty("price").toString());
      this.time = Long.parseLong(entity.getProperty("time").toString());
   }

   public long getKey() {
      return key;
   }

   public void setKey(long key) {
      this.key = key;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
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

}
