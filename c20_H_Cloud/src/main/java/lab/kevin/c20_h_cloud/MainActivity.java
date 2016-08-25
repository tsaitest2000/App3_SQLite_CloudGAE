package lab.kevin.c20_h_cloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.kevin.c20_h_cloud.po.Figure;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private Button mBtnPirate;
   private ListView mLvPirate;
   private Handler mHandler;
   private SimpleAdapter mAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      buildViews();
   }

   private void buildViews() {
      mContext = MainActivity.this;
      mBtnPirate = (Button) this.findViewById(R.id.btnPirate);
      mLvPirate = (ListView) this.findViewById(R.id.lvPirate);
      mHandler = new Handler();

      mHandler.post(mRunnable);
      mBtnPirate.setOnClickListener(new btnOnClickLnr());
      mLvPirate.setOnItemClickListener(new lvOnItemClickLnr());
   }

   private Runnable mRunnable = new Runnable() {
      @Override
      public void run() {
         new RunWork_Query().start();
         mHandler.postDelayed(mRunnable, 1000);
      }
   };

   private class btnOnClickLnr implements View.OnClickListener {
      @Override
      public void onClick(View view) {
         View inflate = LayoutInflater.from(mContext).inflate(R.layout.add, null);
         final EditText etName = (EditText) inflate.findViewById(R.id.etAdd1);
         final EditText etPrice = (EditText) inflate.findViewById(R.id.etAdd2);
         new AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_btn_speak_now)
            .setTitle("請輸入公仔資訊")
            .setView(inflate)
            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  new RunWork_Add(
                     new Figure(etName.getText().toString(), Integer.parseInt(etPrice.getText().toString())))
                     .start();
               }
            })
            .setNegativeButton("取消", null)
            .show();
      }
   }

   private class RunWork_Add extends Thread {

      private Figure mFigure;
      private String mResult;

      public RunWork_Add(Figure figure) {
         this.mFigure = figure;
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
            String format = String.format("http://new20160630.appspot.com/add?name=%s&price=%s",
               mFigure.getName(), mFigure.getPrice());
            mResult = run(format);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, mResult, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private class lvOnItemClickLnr implements android.widget.AdapterView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
         Map map = (Map) adapterView.getItemAtPosition(i);
         View inflate = LayoutInflater.from(mContext).inflate(R.layout.update, null);
         final TextView tvKey = (TextView) inflate.findViewById(R.id.tvUpdate);
         final EditText etName = (EditText) inflate.findViewById(R.id.etUpdate1);
         final EditText etPrice = (EditText) inflate.findViewById(R.id.etUpdate2);
         final long key = (long) map.get("key");
         final String name = (String) map.get("name");
         final int price = (int) map.get("price");
         final String time = (String) map.get("time");

         tvKey.setText(String.valueOf(key));
         etName.setText(name);
         etPrice.setText(String.valueOf(price));

         new AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_btn_speak_now)
            .setTitle("請輸入欲變更的公仔資訊")
            .setView(inflate)
            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  new RunWork_Update(
                     new Figure(key, etName.getText().toString(), Integer.parseInt(etPrice.getText().toString()))
                  ).start();
               }
            })
            .setNeutralButton("刪除", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  new RunWork_Delete(key).start();
               }
            })
            .setNegativeButton("取消", null)
            .show();
      }
   }

   private class RunWork_Delete extends Thread {

      private long mKey;
      private String mResult;

      public RunWork_Delete(long key) {
         this.mKey = key;
      }

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         String format = String.format("http://new20160630.appspot.com/delete?key=%s", String.valueOf(mKey));
         try {
            mResult = run(format);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, mResult, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private class RunWork_Update extends Thread {

      private String mResult;
      private Figure mFigure;

      public RunWork_Update(Figure figure) {
         this.mFigure = figure;
      }

      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         String format = String.format("http://new20160630.appspot.com/update?key=%s&name=%s&price=%s",
            String.valueOf(mFigure.getKey()), mFigure.getName(), String.valueOf(mFigure.getPrice()));
         try {
            mResult = run(format);
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Toast.makeText(mContext, mResult, Toast.LENGTH_SHORT).show();
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private class RunWork_Query extends Thread {

      private String mJson;
      OkHttpClient client = new OkHttpClient();

      String run(String url) throws IOException {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();
         return response.body().string();
      }

      @Override
      public void run() {
         try {
            mJson = run("http://new20160630.appspot.com/query");
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Gson gson = new Gson();
                  Figure[] figures = gson.fromJson(mJson, Figure[].class);
                  List<Map<String, Object>> list = new ArrayList();
                  for (Figure figure : figures) {
                     Map map = new HashMap();
                     map.put("key", figure.getKey());
                     map.put("name", figure.getName());
                     map.put("price", figure.getPrice());
                     map.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm E").format(figure.getTime()));
                     list.add(map);
                  }
                  mAdapter = new SimpleAdapter(mContext, list, R.layout.query,
                     new String[]{"key", "name", "price", "time"},
                     new int[]{R.id.tvQuery1, R.id.tvQuery2, R.id.tvQuery3, R.id.tvQuery4});
                  mLvPirate.setAdapter(mAdapter);
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      mHandler.removeCallbacks(mRunnable);
   }

}
