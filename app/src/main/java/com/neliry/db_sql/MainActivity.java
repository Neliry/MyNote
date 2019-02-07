package com.neliry.db_sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    MyCustomAdapter boxAdapter;
    MyCustomPageAdapter pajeAdapter;

//    Button btnAdd, btnRead, btnClear, btnUpd, btnDel, btnList, btnBack;
//    EditText etName, etId;
    String[]  date;
    String [][]chapters_array, pages;
    DBHelper dbHelper;
    DBPages dbPages;
    Cursor cursor;
    ListView listView;
    ListView listView2;
    String parentChapter="start";
    ImageButton imageBtnBack;
    private Toolbar toolbar;
    Context mContext=this;
    public FloatingActionButton flobtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        imageBtnBack=(ImageButton)findViewById(R.id.imabtnBack);
        imageBtnBack.setOnClickListener(this);
        TextView textView = (TextView)findViewById(R.id.Title);
        flobtn=(FloatingActionButton)findViewById(R.id.fab);
        flobtn.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        dbPages= new DBPages(this);
        //this.deleteDatabase(dbHelper.DATABASE_NAME);
        boxAdapter = new MyCustomAdapter(this);
        pajeAdapter = new MyCustomPageAdapter(this);
        ListUpdate();

        listView=(ListView) findViewById(R.id.main_list);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                parentChapter=chapters_array[position][1]+"_"+chapters_array[position][0];
                Log.d("myLog", "itemClick: position = " + position + ", id = "
                        + chapters_array[position][1]+", name="+chapters_array[position][0]+", parentChapter="+parentChapter);
                ListUpdate();
            }
        });
        listView2=(ListView) findViewById(R.id.page_list);
        registerForContextMenu(listView2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent t=new Intent(MainActivity.this, EditorScreen.class);
                Log.d("myLog", "itemClick: position = " + position + ", id = "
                        + pages[position][3]+", name="+pages[position][0]);
                t.putExtra("key",pages[position][3]);
                startActivity(t);
            }
        });
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                showPagePopupMenu(view, pos, id);

                return true;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                showPopupMenu(view, pos, id);

                return true;
            }
        });

    }


//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imabtnBack:
                Back();
                break;

            case R.id.fab:
                Log.d("myLog", "parentChapter = "+parentChapter);
                CreatePage();

            break;
        }
    }

    private void CreatePage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Створення сторінки");
        alert.setMessage("Назва");
        final EditText input = new EditText(mContext);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.equalsIgnoreCase("")){
                    dbHelper.close();
                    return ;
                }
                long lastId;
                ContentValues contentValues = new ContentValues();
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                lastId=dbHelper.createPage(value, date, parentChapter);
//                ListUpdate();
                String id=""+lastId;
                dbHelper.close();
                Log.i("lastId","id="+  lastId);
                Intent t=new Intent(MainActivity.this, EditorScreen.class);
                t.putExtra("key",id);
                startActivity(t);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

    }

    private  void  Back(){
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            String parent_id = parentChapter.split("_")[0];
            Log.d("myLog", "s = "+parentChapter+" b="+parent_id);
            cursor = database.query(DBHelper.TABLE_CHAPTERS, new String[] { "parent" },"_id = ?" ,new String[] {parent_id}, null, null, null);
            if (cursor.moveToFirst())
                parentChapter = (cursor.getString(cursor.getColumnIndex("parent")));
            cursor.close();
            ListUpdate();
            dbHelper.close();

        Log.d("myLog", "parentChapter = "+parentChapter);
            if(parentChapter.equals("start")){
               imageBtnBack.setVisibility(View.GONE);
            }
    }

private void ListUpdate(){
    Arrays();
    if(!parentChapter.equals("start")){
        imageBtnBack.setVisibility(View.VISIBLE);
    }
    else
        imageBtnBack.setVisibility(View.INVISIBLE);
    listView=findViewById(R.id.main_list);
    listView.setAdapter(boxAdapter);
    listView2=findViewById(R.id.page_list);
    listView2.setAdapter(pajeAdapter);
    ListUtils.setDynamicHeight(listView);
    ListUtils.setDynamicHeight(listView2);

    TextView textView = (TextView)findViewById(R.id.Title);
    if(parentChapter.equals("start"))
        textView.setText("Головна");
    else {
        String[] name = parentChapter.split("_");
        textView.setText(name[1]);
    }
}

    public int ArrayCount(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_CHAPTERS, new String[] { "count(*) as Count" }, "parent = ?", new String[] { parentChapter}, null, null, null);
        int count=0;
        if (cursor.moveToFirst())
            count = Integer.parseInt((cursor.getString(cursor.getColumnIndex("Count"))));
        cursor.close();
        dbHelper.close();
        TextView textView=(TextView) findViewById(R.id.chapters);
        if (count==0)
        textView.setText("");
        else textView.setText("Розділи");
        return count;
    }

    public int PageNumber(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_PAGES, new String[] { "count(*) as Count" }, "parent = ?", new String[] { parentChapter}, null, null, null);
        int count=0;
        if (cursor.moveToFirst())
            count = Integer.parseInt((cursor.getString(cursor.getColumnIndex("Count"))));
        cursor.close();
        dbHelper.close();
        TextView textView=(TextView) findViewById(R.id.pages);
        if (count==0)
            textView.setText("");
        else textView.setText("Сторінки");
        return count;
    }

    public void Arrays(){
        chapters_array=new String[ArrayCount()][2];
        date=new String[ArrayCount()];
        pages=new String[PageNumber()][4];
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int i;
        cursor = database.query(DBHelper.TABLE_PAGES, null, "parent = ?", new String[] {parentChapter},  null, null, null);
        i=0;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int daateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int nameIndex  = cursor.getColumnIndex(DBHelper.KEY_NAME);
            do {
                pages[i][0]=cursor.getString(nameIndex);
                pages[i][1]=cursor.getString(daateIndex);
                pages[i][2] = "  ";
                pages[i][3]=cursor.getString(idIndex);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        for(int j=0; j<pages.length; j++) {
            cursor = database.query(DBHelper.TABLE_VIEWS, new String[]{"content"}, "parent = ? and  name = ?", new String[]{pages[j][3], "text"}, null, null, null);
            if (cursor.moveToFirst()) {
                int contentIndex = cursor.getColumnIndex(DBHelper.KEY_CONTENT);
                pages[j][2] = cursor.getString(contentIndex);
                cursor.close();
            }
        }



        cursor = database.query(DBHelper.TABLE_CHAPTERS, new String[] { "name" }, "parent = ?", new String[] {parentChapter},  null, null, null);
        i=0;
        if (cursor.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cursor.getColumnNames()) {
                    str = str.concat(cursor.getString(cursor.getColumnIndex(cn)));
                }
                chapters_array[i][0]=str;
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        cursor = database.query(DBHelper.TABLE_CHAPTERS, new String[] { "_id" }, "parent = ?", new String[] { parentChapter },  null, null, null);
        i=0;
        if (cursor.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cursor.getColumnNames()) {
                    str = str.concat(cursor.getString(cursor.getColumnIndex(cn)));
                }
                chapters_array[i][1]=str;
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }

            cursor = database.query(DBHelper.TABLE_CHAPTERS, new String[] { "date" }, "parent = ?", new String[] { parentChapter }, null, null, null);
        i=0;
        if (cursor.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : cursor.getColumnNames()) {
                    str = str.concat(cursor.getString(cursor.getColumnIndex(cn)));
                }
                date[i]=str;
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }

            dbHelper.close();
    }

    private class MyCustomAdapter extends BaseAdapter {
        Context mContext;

        MyCustomAdapter(Context context) {
            mContext=context;
        }
        // кол-во элементов
        @Override
        public int getCount() {
            int count= ArrayCount() ;
            return count;
        }
        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }
        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return "Text";
        }
        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view

            LayoutInflater layoutInflater=LayoutInflater.from(mContext);

            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.main_row, parent, false);
            }

            ((TextView) view.findViewById(R.id.nameTV)).setText(chapters_array[position][0]);
            ((TextView) view.findViewById(R.id.dateTV)).setText(date[position]);

            return view;
        }

    }

    private class MyCustomPageAdapter extends BaseAdapter {
        Context mContext;

        MyCustomPageAdapter(Context context) {
            mContext=context;
        }
        // кол-во элементов
        @Override
        public int getCount() {
            int count=PageNumber();
            return count;
        }
        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }
        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return "Text";
        }
        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view

            LayoutInflater layoutInflater=LayoutInflater.from(mContext);

            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.page_list_row, parent, false);
            }

            ((TextView) view.findViewById(R.id.name)).setText(pages[position][0]);
            Spanned htmlDescription = Html.fromHtml(pages[position][2]);
            String descriptionWithOutExtraSpace = new String(htmlDescription.toString()).trim();
            ((TextView) view.findViewById(R.id.content)).setText(htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length()));
            ((TextView) view.findViewById(R.id.date)).setText(pages[position][1]);
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int id=item.getItemId();
        final Context context = mContext;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (id) {
            case R.id.action_menu1:
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Новий розділ");
                alert.setMessage("Назва");
                final EditText input = new EditText(context);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        String value = input.getText().toString();
                        if (value.equalsIgnoreCase("")){
                            return ;
                        }
                        ContentValues contentValues = new ContentValues();
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        dbHelper.createChapter(value, date, parentChapter);
                        Log.d("myLog","edd");
                        ListUpdate();
                        dbHelper.close();
                    }
                });

                alert.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
                break;

            case R.id.action_menu2:
                database.delete(DBHelper.TABLE_CHAPTERS, null, null);
                database.delete(DBHelper.TABLE_PAGES, null, null);
                database.delete(DBHelper.TABLE_VIEWS, null, null);
                ListUpdate();
                break;
        }
        dbHelper.close();
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu(final View v, final int pos, long id) {
    PopupMenu popupMenu = new PopupMenu(this, v);
    popupMenu.inflate(R.menu.flo_menu); // Для Android 4.0
    final Context context = mContext;
    final int p=pos;

    // для версии Android 3.0 нужно использовать длинный вариант
    // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
    // popupMenu.getMenu());

    popupMenu
            .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String id = chapters_array[p][1];
                    final SQLiteDatabase[] database = new SQLiteDatabase[1];


                    switch (item.getItemId()) {
                        case R.id.delete:
                            database[0] = dbHelper.getWritableDatabase();
                            if (id.equalsIgnoreCase("")){
                                break;
                            }
                            int delCount = database[0].delete(DBHelper.TABLE_CHAPTERS, DBHelper.KEY_ID + "= ?" , new String[] {id});
                            ListUpdate();

                            return true;
                        case R.id.edit:
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Редагування назви");
                            alert.setMessage("Нова назва");
                            final EditText input = new EditText(context);
                            alert.setView(input);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = input.getText().toString();
                                    database[0] = dbHelper.getWritableDatabase();
                                    if (value.equalsIgnoreCase("")){
                                        return ;
                                    }
                                    //contentValues.put(DBHelper.KEY_MAIL, email);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(DBHelper.KEY_NAME, value);
                                    int updCount = database[0].update(DBHelper.TABLE_CHAPTERS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {id});

                                    ListUpdate();
                                }
                            });

                            alert.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.show();
                            return true;
                        default:
                            return false;
                    }
                    return false;
                }
            });
    dbHelper.close();
    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

        @Override
        public void onDismiss(PopupMenu menu) {
        }
    });
    popupMenu.show();
}

    private void showPagePopupMenu(final View v, final int pos, long id) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.flo_menu); // Для Android 4.0
        final Context context = mContext;
        final int p=pos;

        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String id = pages[p][3];
                        final SQLiteDatabase[] database = new SQLiteDatabase[1];


                        switch (item.getItemId()) {
                            case R.id.delete:
                                database[0] = dbHelper.getWritableDatabase();
                                if (id.equalsIgnoreCase("")){
                                    break;
                                }
                                database[0].delete(DBHelper.TABLE_PAGES, DBHelper.KEY_ID + "= ?" , new String[] {id});
                                int delCount = database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_PARENT + "= ?" , new String[] {id});
                                Log.d("myLog", "deleted views count = " + delCount);
                                ListUpdate();

                                return true;
                            case R.id.edit:
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Редагування назви");
                                alert.setMessage("Нова назва");
                                final EditText input = new EditText(context);
                                alert.setView(input);

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String value = input.getText().toString();
                                        database[0] = dbHelper.getWritableDatabase();
                                        if (value.equalsIgnoreCase("")){
                                            return ;
                                        }
                                        //contentValues.put(DBHelper.KEY_MAIL, email);
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBHelper.KEY_NAME, value);
                                        int updCount = database[0].update(DBHelper.TABLE_PAGES, contentValues, DBHelper.KEY_ID + "= ?", new String[] {id});

                                        Log.d("myLog", "updates rows count = " + updCount);
                                        ListUpdate();
                                    }
                                });

                                alert.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });

                                alert.show();
                                return true;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
        dbHelper.close();
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}


