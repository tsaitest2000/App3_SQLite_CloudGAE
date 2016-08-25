package lab.kevin.c18_h_pirate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private ListView mListView;
   private SQLiteDatabase mDb;
   private Cursor mCursor;
   private SimpleCursorAdapter mAdapter;
   private ImageView ivEdit;
   private EditText etNameEdit;
   private EditText etPriceEdit;
   private Bitmap mBitmap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      buildViews();
   }

   private void buildViews() {
      mContext = MainActivity.this;
      mListView = (ListView) this.findViewById(R.id.listView);

      mDb = MainActivity.this.openOrCreateDatabase("Pirate18.db", SQLiteDatabase.OPEN_READWRITE, null);
      mDb.execSQL(
         "create table if not exists king(_id integer primary key, name varchar2(20), price integer, imgResId integer, image blob)");
      mCursor = mDb.rawQuery("select _id, name, price, imgResId, image from king", null);
      mAdapter = new SimpleCursorAdapter(mContext, R.layout.row, mCursor,
         new String[]{"imgResId", "name", "price"}, new int[]{R.id.ivRow, R.id.tvNameRow, R.id.tvPriceRow},
         CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER //★★★★
      ) {
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.row, null);
            ImageView ivRow = (ImageView) inflate.findViewById(R.id.ivRow);
            TextView tvNameRow = (TextView) inflate.findViewById(R.id.tvNameRow);
            TextView tvPriceRow = (TextView) inflate.findViewById(R.id.tvPriceRow);

            Cursor cursor = (Cursor) getItem(position); //★★★★
            String name = cursor.getString(1);
            int price = cursor.getInt(2);
            byte[] byteArray = cursor.getBlob(4);
            ivRow.setImageBitmap(bytes_2_bitmap(byteArray));
            tvNameRow.setText(name);
            tvPriceRow.setText(String.valueOf(price));
            return inflate;
         }
      };
      mListView.setAdapter(mAdapter);
      mListView.setOnItemClickListener(new lvOnItemClkLnr());
      mListView.setOnItemLongClickListener(new lvOnItemLongClkLnr());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, 1, Menu.NONE, "席爾巴斯.雷利");
      menu.add(0, 2, Menu.NONE, "蒙奇.D.路飛");
      menu.add(0, 3, Menu.NONE, "女帝.波雅.漢庫克");
      menu.add(0, 4, Menu.NONE, "波特卡斯.D.艾斯");
      menu.add(0, 5, Menu.NONE, "航海士.娜美");
      menu.add(0, 6, Menu.NONE, "歷史家.妮可羅賓");
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      String sql = "insert into king(name ,price, image) values (?, ?, ?)";
      Object[] args = null;
      switch (item.getItemId()) {
         case 1:
            args = new Object[]{"席爾巴斯.雷利", 6250, imgRes_to_bytes(R.drawable.a01)};
            break;
         case 2:
            args = new Object[]{"蒙奇.D.路飛", 5750, imgRes_to_bytes(R.drawable.a02)};
            break;
         case 3:
            args = new Object[]{"女帝.波雅.漢庫克", 8000, imgRes_to_bytes(R.drawable.a03)};
            break;
         case 4:
            args = new Object[]{"波特卡斯.D.艾斯", 6000, imgRes_to_bytes(R.drawable.a04)};
            break;
         case 5:
            args = new Object[]{"航海士.娜美", 7600, imgRes_to_bytes(R.drawable.a06)};
            break;
         case 6:
            args = new Object[]{"歷史家.妮可羅賓", 7400, imgRes_to_bytes(R.drawable.a07)};
            break;
      }
      mDb.execSQL(sql, args);
      reloadDatabase();
      return super.onOptionsItemSelected(item);
   }

   // ★★★★★ 務必執行的程式區塊
   private void reloadDatabase() {
      mCursor = mDb.rawQuery("select _id, name, price, imgResId, image from king", null);
      mAdapter.changeCursor(mCursor);
      mAdapter.notifyDataSetChanged();
   }

   private byte[] imgRes_to_bytes(int imgResId) {
      Resources resources = MainActivity.this.getResources();
      Bitmap bitmap = BitmapFactory.decodeResource(resources, imgResId);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
      return stream.toByteArray();
   }

   private byte[] bitmap_2_bytes(Bitmap bitmap) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
      return stream.toByteArray();
   }

   private Bitmap bytes_2_bitmap(byte[] byteArray) {
      Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
      return bmp;
   }

   private class lvOnItemClkLnr implements AdapterView.OnItemClickListener {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor cursor = (Cursor) parent.getItemAtPosition(position); //★★★★
         final int _id = cursor.getInt(0);
         final String name = cursor.getString(1);
         final int price = cursor.getInt(2);
         final int imgResId = cursor.getInt(3);
         final byte[] byteArray = cursor.getBlob(4);

         View inflate = LayoutInflater.from(mContext).inflate(R.layout.edit, null);
         ivEdit = (ImageView) inflate.findViewById(R.id.ivEdit);
         etNameEdit = (EditText) inflate.findViewById(R.id.etNameEdit);
         etPriceEdit = (EditText) inflate.findViewById(R.id.etPriceEdit);

         ivEdit.setImageBitmap(bytes_2_bitmap(byteArray));
         etNameEdit.setText(name);
         etPriceEdit.setText(String.valueOf(price));

         ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               MainActivity.this.startActivityForResult(intent, 101);
            }
         });

         new AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_btn_speak_now)
            .setTitle("變更公仔資訊")
            .setView(inflate)
            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  if (mBitmap != null) {
                     String sql = "update king set name = ?, price = ?, image = ? where _id = ?";
                     Object[] args = new Object[]{
                        etNameEdit.getText().toString(), etPriceEdit.getText().toString(), bitmap_2_bytes(mBitmap), _id};
                     mDb.execSQL(sql, args);
                     reloadDatabase();
                     mBitmap = null; /**避免商業邏輯錯誤：若按click item而未再按ImageView照相時**/
                  } else {
                     String sql = "update king set name = ?, price = ? where _id = ?";
                     Object[] args = new Object[]{
                        etNameEdit.getText().toString(), etPriceEdit.getText().toString(), _id};
                     mDb.execSQL(sql, args);
                     reloadDatabase();
                  }
               }
            })
            .setNegativeButton("取消", null)
            .show();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
         Bundle bundle = data.getExtras();
         mBitmap = (Bitmap) bundle.get("data");
         ivEdit.setImageBitmap(mBitmap);
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   private class lvOnItemLongClkLnr implements AdapterView.OnItemLongClickListener {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor cursor = (Cursor) parent.getItemAtPosition(position);
         mDb.execSQL("delete from king where _id = ?", new Object[]{cursor.getInt(0)});
         reloadDatabase();
         return true;
      }
   }

}
