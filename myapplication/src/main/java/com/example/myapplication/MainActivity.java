package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private ListView mListView;
   private SQLiteDatabase mDb;
   private Cursor mCursor;
   private CursorAdapter mAdapter;
   private ImageView mIvEdit;
   private Bitmap mBitmap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      mContext = MainActivity.this;
      mListView = (ListView) this.findViewById(R.id.listView);

      mDb = MainActivity.this.openOrCreateDatabase("Car.db", SQLiteDatabase.OPEN_READWRITE, null);
      mDb.execSQL(
         "create table if not exists car(_id integer primary key, name varchar2(20), price integer, imgResId integer, image blob)"
      );
      mCursor = mDb.rawQuery("select * from car", null);
      mAdapter = new SimpleCursorAdapter(
         mContext, R.layout.row, mCursor,
         new String[]{"imgResId", "name", "price"},
         new int[]{R.id.ivRow, R.id.tvNameRow, R.id.tvPriceRow},
         CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
      );
      mListView.setAdapter(mAdapter);
      mListView.setOnItemClickListener(new lvOnClickLnr());
      mListView.setOnItemLongClickListener(new lvOnLongClickLnr());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, 1, Menu.NONE, "BMW 2系列雙門跑車");
      menu.add(0, 2, Menu.NONE, "BMW 3系列Gran Turbo");
      menu.add(0, 3, Menu.NONE, "BMW 5系列Gran Turbo");
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      String sql = "insert into car(name, price, imgResId, image) values(?,?,?,?)";
      Object[] args = null;
      switch (item.getItemId()) {
         case 1:
            args = new Object[]{"BMW 2系列雙門跑車", 650000, R.drawable.a01, imgResId_to_byteArray(R.drawable.a01)};
            break;
         case 2:
            args = new Object[]{"BMW 2系列雙門跑車", 610000, R.drawable.a02, imgResId_to_byteArray(R.drawable.a02)};
            break;
         case 3:
            args = new Object[]{"BMW 2系列雙門跑車", 520000, R.drawable.a03, imgResId_to_byteArray(R.drawable.a03)};
            break;
      }
      mDb.execSQL(sql, args);
      reloadDb();
      return super.onOptionsItemSelected(item);
   }

   private void reloadDb() {
      mCursor = mDb.rawQuery("select * from car", null);
      mAdapter.changeCursor(mCursor);
      mAdapter.notifyDataSetChanged();
   }

   private class lvOnLongClickLnr implements android.widget.AdapterView.OnItemLongClickListener {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor itemAtPosition = (Cursor) parent.getItemAtPosition(position);
         int _id = itemAtPosition.getInt(0);
         String sql = "delete from car where _id=?";
         mDb.execSQL(sql, new Object[]{_id});
         reloadDb();
         return true;
      }
   }


   private class lvOnClickLnr implements AdapterView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor itemAtPosition = (Cursor) parent.getItemAtPosition(position);
         final int _id = itemAtPosition.getInt(0);
         String name = itemAtPosition.getString(1);
         int price = itemAtPosition.getInt(2);
         byte[] byteArray = itemAtPosition.getBlob(4);

         View inflate = LayoutInflater.from(mContext).inflate(R.layout.edit, null);
         mIvEdit = (ImageView) inflate.findViewById(R.id.ivEdit);
         final EditText etNameEdit = (EditText) inflate.findViewById(R.id.etNameEdit);
         final EditText etPriceEdit = (EditText) inflate.findViewById(R.id.etPriceEdit);
         mIvEdit.setImageBitmap(byteArray_to_bitmap(byteArray));
         etNameEdit.setText(name);
         etPriceEdit.setText(String.valueOf(price));

         mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               MainActivity.this.startActivityForResult(intent, 101);
            }
         });

         new AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_btn_speak_now)
            .setView(inflate)
            .setCancelable(false)
            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  String sql = null;
                  Object[] args = null;
                  if (mBitmap != null) {
                     mIvEdit.setImageBitmap(mBitmap);
                     sql = "update car set name=?, price=?, image=? where _id=?";
                     args = new Object[]{
                        etNameEdit.getText().toString(),
                        Integer.parseInt(etPriceEdit.getText().toString()),
                        bitmap_to_byteArray(mBitmap),
                        _id
                     };

                  } else {
                     sql = "update car set name=?, price=? where _id=?";
                     args = new Object[]{
                        etNameEdit.getText().toString(),
                        Integer.parseInt(etPriceEdit.getText().toString()),
                        _id
                     };
                  }
                  mDb.execSQL(sql, args);
                  reloadDb();
               }
            })
            .setNegativeButton("取消", null)
            .show();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
         Bundle bundle = data.getExtras();
         mBitmap = (Bitmap) bundle.get("data");
         mIvEdit.setImageBitmap(mBitmap);
      }
   }

   private byte[] imgResId_to_byteArray(int imgResId) {

      Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), imgResId);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
   }

   private byte[] bitmap_to_byteArray(Bitmap bitmap) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
   }

   private Bitmap byteArray_to_bitmap(byte[] byteArray) {
      Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
      return bitmap;
   }

}
