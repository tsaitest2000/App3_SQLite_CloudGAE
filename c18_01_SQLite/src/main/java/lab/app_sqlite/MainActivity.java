package lab.app_sqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {

   private Context mContext;
   private SQLiteDatabase mDb;
   private Cursor mCursor;
   private SimpleCursorAdapter mAdapter;

   private ListView mListView;
   private ImageView mIvEdit; //edit.xml中的元件
   private EditText mEtNameEdit; //edit.xml中的元件
   private EditText mEtPriceEdit; //edit.xml中的元件
   private Bitmap mBmp;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      buildViews();
   }

   private void buildViews() {
      mContext = MainActivity.this;
      mListView = (ListView) findViewById(R.id.listView);
      mDb = openOrCreateDatabase("store18.db", MODE_PRIVATE, null);
      mDb.execSQL("CREATE TABLE IF NOT EXISTS mcd(" +
         "_id INTEGER PRIMARY KEY, name TEXT, price NUMERIC, imageResId NUMERIC, image BLOB)");
      mCursor = mDb.rawQuery("select _id, name, price, imageResId, image from mcd", null);

      mAdapter = new SimpleCursorAdapter(mContext, R.layout.row, mCursor,
         new String[]{"imageResId", "name", "price"}, new int[]{R.id.iv1Row, R.id.tv1Row, R.id.tv2Row},
         CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
         /* ★★★★★ 若imageResId變成image，則發生難以解決的Exception
         android.database.sqlite.SQLiteException: unknown error (code 0): Unable to convert BLOB to string*/
      ) {
         /* 改寫getView() ★★★★★ 若不改寫則拍好的影像無法顯示在R.layout.row上 → 它只會顯示imageRedId */
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
            View layoutRow = LayoutInflater.from(mContext).inflate(R.layout.row, null);
            ImageView iv1 = (ImageView) layoutRow.findViewById(R.id.iv1Row);
            TextView tvName = (TextView) layoutRow.findViewById(R.id.tv1Row);
            TextView tvPrice = (TextView) layoutRow.findViewById(R.id.tv2Row);

            Cursor cursor = (Cursor) getItem(position); /* ★★★★★ */
            String name = cursor.getString(1);
            int price = cursor.getInt(2);
            int imageId = cursor.getInt(3);
            byte[] bytes = cursor.getBlob(4);

            tvName.setText(name);
            tvPrice.setText(String.valueOf(price));
            if (bytes == null) iv1.setImageResource(imageId); //沒有拍照取得新影像
            else iv1.setImageBitmap(bytes_To_Bmp(bytes)); // bytes轉換為bitmap
            return layoutRow;
         }
      };

      mListView.setAdapter(mAdapter);
      mListView.setOnItemClickListener(new MyOnItemClickListener());
      mListView.setOnItemLongClickListener(new MyOnItemLongClickListener());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, 0, Menu.NONE, "新增-大麥克");
      menu.add(0, 1, Menu.NONE, "新增-蘋果派");
      menu.add(0, 2, Menu.NONE, "新增-麥香魚");
      menu.add(0, 3, Menu.NONE, "全刪");
      return super.onCreateOptionsMenu(menu);
   }

   // 老師的做法：一開始並沒有將image(byte陣列)insert到資料表之中。以下 我將之寫入到資料表中以補強商業邏輯
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      String sql = "INSERT INTO mcd('name', 'price', 'imageResId', image) VALUES(?, ?, ?, ?)";
      byte[] bytes = null;
      Object[] args = null;
      switch (item.getItemId()) {
         case 0:
            bytes = resImg_To_Bytes(R.drawable.big_mac);
            args = new Object[]{"大麥克", new Random().nextInt(100) + 100, R.drawable.big_mac, bytes};
            mDb.execSQL(sql, args);
            refresh();
            break;
         case 1:
            bytes = resImg_To_Bytes(R.drawable.apple_pie);
            args = new Object[]{"蘋果派", new Random().nextInt(100) + 100, R.drawable.apple_pie, bytes};
            mDb.execSQL(sql, args);
            refresh();
            break;
         case 2:
            bytes = resImg_To_Bytes(R.drawable.fish);
            args = new Object[]{"麥香魚", new Random().nextInt(100) + 100, R.drawable.fish, bytes};
            mDb.execSQL(sql, args);
            refresh();
            break;
         case 3:
            mDb.execSQL("delete from mcd");
            refresh();
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   private void refresh() {
      mCursor = mDb.rawQuery("select _id, name, price, imageResId, image from mcd", null); //重查
      mAdapter.changeCursor(mCursor);
      mAdapter.notifyDataSetChanged(); //通知適配器
   }

   private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor cursor = (Cursor) parent.getItemAtPosition(position); /* ★★★★★ */
         final int _id = cursor.getInt(0);
         final String name = cursor.getString(1);
         final int price = cursor.getInt(2);
         final int imageResId = cursor.getInt(3);
         final byte[] bytes = cursor.getBlob(4);

         AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

         View layoutEdit = View.inflate(mContext, R.layout.edit, null); //亦可使用LayoutInflater
         mIvEdit = (ImageView) layoutEdit.findViewById(R.id.edit_image);
         mEtNameEdit = (EditText) layoutEdit.findViewById(R.id.edit_name);
         mEtPriceEdit = (EditText) layoutEdit.findViewById(R.id.edit_price);
         mIvEdit.setImageBitmap(bytes_To_Bmp(bytes)); //老師使用.setImageResource(imageResId)
         mEtNameEdit.setText(name);
         mEtPriceEdit.setText(String.valueOf(price));

         mIvEdit.setClickable(true);
         mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override //啟動照相功能
            public void onClick(View v) {
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               MainActivity.this.startActivityForResult(intent, 101);
            }
         });
         alertBuilder.setTitle("修改商品:");
         alertBuilder.setView(layoutEdit);
         alertBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               String sql = null;
               Object[] args = null;
               // 我:商業邏輯，若啟動相機照相則更新影像,name,price欄位資料; 否則只更新name,price欄位資料
               /********************************************************************************/
               if (mBmp != null) {
                  sql = "Update mcd set name=?, price=?, image=? Where _id = ?";
                  args = new Object[]{
                     mEtNameEdit.getText().toString(), mEtPriceEdit.getText().toString(), bmp_To_Bytes(mBmp), _id};
                  mBmp = null; /**避免商業邏輯錯誤：若按click item而未再按ImageView照相時**/
               } else {
                  sql = "Update mcd set name=?, price=? Where _id = ?";
                  args = new Object[]{
                     mEtNameEdit.getText().toString(), mEtPriceEdit.getText().toString(), _id};
               }
               mDb.execSQL(sql, args);
               refresh();
            }
         });
         alertBuilder.setNegativeButton("取消", null);
         alertBuilder.show();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
         mBmp = (Bitmap) data.getExtras().get("data");
         mIvEdit.setImageBitmap(mBmp);
      }
   }

   private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
         Cursor cursor = (Cursor) parent.getItemAtPosition(position);
         int _id = cursor.getInt(0);
         String sql = "Delete from mcd Where _id = ?";
         Object[] args = {_id};
         mDb.execSQL(sql, args);
         refresh();
         return true;
      }
   }

   /* Android中，ImageView介面的影像皆為Bitmap; 存入資料庫為Blob(byte[]) *****************************/
   /* 參數為res/drawable中的影像，輸出為byte[] → 我自己新增的方法，配合onOptionsItemSelected()的商業邏輯*/
   private byte[] resImg_To_Bytes(int resImgId) {
      Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), resImgId);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
      return stream.toByteArray();
   }

   /* 參數為Bitmap，輸出為byte[]  ******************************************************************/
   private byte[] bmp_To_Bytes(Bitmap bmp) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
      return stream.toByteArray();
   }

   /* 參數為byte[]，輸出為Bitmap *******************************************************************/
   private Bitmap bytes_To_Bmp(byte[] bytes) {
      Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      return bmp;
   }

}
