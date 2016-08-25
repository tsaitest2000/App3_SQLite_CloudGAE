package lab.app_cloud;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import lab.app_cloud.po.Book;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private Button mBtnAdd;
   private ListView mListView;
   private ArrayAdapter<Book> mAdapter;
   private Handler mHandler;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mContext = this;
      mBtnAdd = (Button) this.findViewById(R.id.btnAdd);
      mListView = (ListView) this.findViewById(R.id.listView);
      mHandler = new Handler();

      mBtnAdd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            addBook();
         }
      });
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = (Book) parent.getItemAtPosition(position); //Adapter中裝的是Book陣列
            updateBook(book);
         }
      });
      mHandler.post(mRunnable); //模擬及時資料庫FireBase：每秒會下載資料一次
   }



   Runnable mRunnable = new Runnable() {
      @Override
      public void run() {
         new RunWork_Query().start();
         mHandler.postDelayed(mRunnable, 1000);
      }
   };

   private void addBook() {
      AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
      alert.setTitle("新增 Book");
      View view = View.inflate(mContext, R.layout.add, null);
      final EditText etTitle = (EditText) view.findViewById(R.id.title);
      final EditText etAuthor = (EditText) view.findViewById(R.id.author);
      final EditText etPrice = (EditText) view.findViewById(R.id.price);
      alert.setView(view);
      alert.setPositiveButton("新增", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            new RunWork_Add(
               new Book(
                  etTitle.getText().toString(),
                  etAuthor.getText().toString(),
                  Integer.parseInt(etPrice.getText().toString())))
               .start();
         }
      });
      alert.setNegativeButton("取消", null);
      alert.show();
   }

   /***********************************************************************************************/
   // 雲端資料庫_新增操作 ============================================================================
   class RunWork_Add extends Thread {

      Book book;
      String result; //接收來自雲端資料庫的訊息 response.getWriter().println("Add Ok");

      RunWork_Add(Book book) {
         this.book = book;
      }

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         try {
            String url = String.format("http://new20160630.appspot.com/add?title=%s&author=%s&price=%s",
               book.getTitle(), book.getAuthor(), String.valueOf(book.getPrice()));
            result = run(url);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /***********************************************************************************************/
   // 雲端資料庫_刪除操作 ============================================================================
   /* 設計思維：使用者點擊ListView的某個Item時，在出現的AlertDialog中按下刪除按鈕即可刪除某筆資料 *********/
   class RunWork_Delete extends Thread {

      String key;
      String result; //接收來自雲端資料庫的訊息 response.getWriter().println("Add Ok");

      RunWork_Delete(String key) {
         this.key = key;
      }

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         try {
            String url = String.format("http://new20160630.appspot.com/delete?key=%s", key);
            result = run(url);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void updateBook(Book book) {
      AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
      alert.setTitle("修改 Book");
      View view = View.inflate(mContext, R.layout.update, null);
      final EditText etKey = (EditText) view.findViewById(R.id.key);
      final EditText etTitle = (EditText) view.findViewById(R.id.title);
      final EditText etAuthor = (EditText) view.findViewById(R.id.author);
      final EditText etPrice = (EditText) view.findViewById(R.id.price);

      etKey.setText(book.getKey() + "");
      etTitle.setText(book.getTitle());
      etAuthor.setText(book.getAuthor());
      etPrice.setText(book.getPrice() + "");
      alert.setView(view);

      alert.setPositiveButton("修改", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            new RunWork_Update(
               new Book(
                  Long.parseLong(etKey.getText().toString()),
                  etTitle.getText().toString(),
                  etAuthor.getText().toString(),
                  Integer.parseInt(etPrice.getText().toString())))
               .start();
         }
      });
      alert.setNeutralButton("刪除", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            new RunWork_Delete(etKey.getText().toString()).start();
         }
      });
      alert.setNegativeButton("取消", null);
      alert.show();
   }


   /***********************************************************************************************/
   // 雲端資料庫_修改操作 ============================================================================
   class RunWork_Update extends Thread {

      Book book;
      String result; //接收來自雲端資料庫的訊息 response.getWriter().println("Add Ok");

      RunWork_Update(Book book) {
         this.book = book;
      }

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         try {
            String url = String.format("http://new20160630.appspot.com/update?key=%s&title=%s&author=%s&price=%s",
               book.getKey(), book.getTitle(), book.getAuthor(), book.getPrice());
            result = run(url);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /***********************************************************************************************/
   // 雲端資料庫_查詢操作 ============================================================================
   class RunWork_Query extends Thread {

      String json;
      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         try {
            json = run("http://new20160630.appspot.com/query");
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Gson gson = new Gson();
                  Book[] books = gson.fromJson(json, Book[].class);
                  mAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, books);
                  mListView.setAdapter(mAdapter);
               }
            });
         } catch (Exception e) {

         }
      }
   }

   public void onDestroy() {
      super.onDestroy();
      mHandler.removeCallbacks(mRunnable);
   }

}
