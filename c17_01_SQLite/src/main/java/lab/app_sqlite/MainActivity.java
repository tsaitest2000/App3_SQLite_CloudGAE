package lab.app_sqlite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ListView mLvDB;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mLvDB = (ListView) findViewById(R.id.listView);

        // 使用自訂Cursor → 參數三設為null; 執行此程式碼 → 在/data/data/lab.app_sqlite中即產生資料庫實體檔
        mDb = MainActivity.this.openOrCreateDatabase("store17.db", SQLiteDatabase.OPEN_READWRITE, null);
        // 加上 IF NOT EXISTS 以避免執行時期的錯誤
        mDb.execSQL("CREATE TABLE IF NOT EXISTS mcd(" +
            "_id INTEGER PRIMARY KEY, name TEXT, price NUMERIC, imageResId NUMERIC, image BLOB)");
        mCursor = mDb.rawQuery("select _id, name, price, imageResId, image from mcd", null);
        // SimpleCursorAdapter (Context context,int layout, Cursor c, String[] from, int[] to, int flags)
        // int: Flags used to determine the behavior of the adapter, as per CursorAdapter(Context, Cursor, int)
        mAdapter = new SimpleCursorAdapter(mContext, R.layout.row, mCursor,
            new String[]{"imageResId", "name", "price"}, new int[]{R.id.iv1, R.id.tv1, R.id.tv2},
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        mLvDB.setAdapter(mAdapter);
        mLvDB.setOnItemClickListener(new lvOnItemLongClkLnr());
        mLvDB.setOnItemLongClickListener(new lvOnItemClkLnr());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, Menu.NONE, "新增 大麥克");
        menu.add(0, 1, Menu.NONE, "新增 蘋果派");
        menu.add(0, 2, Menu.NONE, "新增 麥香魚");
        menu.add(0, 3, Menu.NONE, "刪除全部品項");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sql = "INSERT INTO mcd('name', 'price','imageResId') VALUES(?, ?, ?)";
        Object[] args = null;
        switch (item.getItemId()) {
            case 0:
                args = new Object[]{"大麥克", new Random().nextInt(100) + 100, R.drawable.big_mac};
                mDb.execSQL(sql, args);
                break;
            case 1:
                args = new Object[]{"蘋果派", new Random().nextInt(100) + 100, R.drawable.apple_pie};
                mDb.execSQL(sql, args);
                break;
            case 2:
                args = new Object[]{"麥香魚", new Random().nextInt(100) + 100, R.drawable.fish};
                mDb.execSQL(sql, args);
                break;
            case 3:
                mDb.execSQL("delete from mcd");
                break;
        }
        refresh();
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        mCursor = mDb.rawQuery("select _id, name, price, imageResId, image from mcd", null);
        /*********************************************************************/
        /*********************************************************************/
        mAdapter.changeCursor(mCursor); //因為資料在資料庫之內，故，必須重新進行查詢
        mAdapter.notifyDataSetChanged(); //通知適配器
    }

    private class lvOnItemLongClkLnr implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /**********************************************************/
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            final int _id = cursor.getInt(0);
            int price = cursor.getInt(2);

            final EditText editText = new EditText(mContext);
            editText.setText(String.valueOf(price));
            editText.setGravity(Gravity.LEFT);
            editText.setPadding(60, 0, 0, 0);

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("修改價格");
            alert.setView(editText);
            alert.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String sql = "Update mcd set price=? Where _id=?";
                    Object[] args = {editText.getText().toString(), _id};
                    mDb.execSQL(sql, args);
                    refresh();
                }
            });
            alert.setNegativeButton("取消", null);
            alert.show();
        }
    }

    private class lvOnItemClkLnr implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int _id = cursor.getInt(0);
            String sql = "Delete from mcd Where _id = ?";
            Object[] args = {_id};
            mDb.execSQL(sql, args);
            refresh();
            return true; //事件不往下傳遞; 若為false，則會將事件傳遞給onClick
        }
    }

}
