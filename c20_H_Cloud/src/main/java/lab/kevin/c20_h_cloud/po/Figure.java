package lab.kevin.c20_h_cloud.po;

public class Figure {

   private long key;
   private String name;
   private int price;
   private long time;

   public Figure() {
   }

   public Figure(String name, int price) {
      this.name = name;
      this.price = price;
   }

   public Figure(long key, String name, int price) {
      this.key = key;
      this.name = name;
      this.price = price;
   }

   public Figure(String name, int price, long time) {
      this.name = name;
      this.price = price;
      this.time = time;
   }

   public Figure(long key, String name, int price, long time) {
      this.key = key;
      this.name = name;
      this.price = price;
      this.time = time;
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
