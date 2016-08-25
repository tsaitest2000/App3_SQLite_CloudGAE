package lab.app_cloud;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import lab.app_cloud.po.Book;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private ListView mListView;
   private ArrayAdapter<Book> mAdapter;
   private Handler mHandler;
   private String mJson;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mContext = this;
      mListView = (ListView) findViewById(R.id.listView);
      mHandler = new Handler();
      mHandler.post(mRunnable);
   }

   Runnable mRunnable = new Runnable() {
      @Override
      public void run() {
         new RunWork().start();
         mHandler.postDelayed(mRunnable, 1000); //每經過一秒，會下載一次網頁上的資料
      }
   };

   class RunWork extends Thread {

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      Runnable r = new Runnable() {
         @Override
         public void run() {
            Gson gson = new Gson();
            Book[] books = gson.fromJson(mJson, Book[].class);
            mAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, books);
            mListView.setAdapter(mAdapter);
         }
      };

      @Override
      public void run() {
         try {
            mJson = run("http://new20160620.appspot.com/query");
            runOnUiThread(r);
         } catch (Exception e) {

         }
      }
   }

   // 釋放資源，否則會消秏系統資源
   public void onDestroy() {
      super.onDestroy();
      mHandler.removeCallbacks(mRunnable);
   }

}
