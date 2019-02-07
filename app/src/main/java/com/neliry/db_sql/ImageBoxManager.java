package com.neliry.db_sql;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Neliry on 04.06.2018.
 */

public class ImageBoxManager {
    RelativeLayout imagebox,  borderBox;
    static RelativeLayout controlButtonBox;
    float x, y, touchedX, touchedY;
    EditorScreen editorScreen;
    Context context;
    ImageView imageView;
    String path;

    private static WeakReference<Activity> mActivityRef;

    public static void updateAktivity(Activity activity){
        mActivityRef=new WeakReference<Activity>(activity);
    }


    public ImageBoxManager(Context context){
        this.context=context;
        editorScreen=(EditorScreen) mActivityRef.get();

    }

    void CreateImageBox(BitmapDrawable drawable, int height, String realPath){
        imagebox=new RelativeLayout(context);
        int w=drawable.getIntrinsicWidth()+editorScreen.dpToPixel(30), h=drawable.getIntrinsicHeight()+editorScreen.dpToPixel(30);

        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(w,
                       h);
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        imagebox.setLayoutParams(params0);
        params0.setMargins(10,height, 0, 0);
        imagebox.setLayoutParams(params0);
        imagebox.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                imagebox.post(new Runnable() {
                    @Override
                    public void run() {
                        controlButtonBox.getLayoutParams().height=imagebox.getHeight();
                        controlButtonBox.getLayoutParams().width=imagebox.getWidth();
                        RelativeLayout.LayoutParams p1=(RelativeLayout.LayoutParams) controlButtonBox.getLayoutParams();
                        p1.setMargins((int)imagebox.getX(), (int)imagebox.getY(), 0, 0);
                        controlButtonBox.setLayoutParams(p1);
                        controlButtonBox.requestLayout();
                    }
                });

            }
        });
        path= realPath;
        imageView=new ImageView(context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        params1.setMargins(editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15));
        imageView.setLayoutParams(params1);
        imageView.setTag("imageView");
        imageView.setBackground(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent=(View) view.getParent();
                if(imagebox!=parent){
                    ClearFocus();
                }
                if(controlButtonBox.getVisibility()==View.GONE){
                    imagebox=(RelativeLayout) parent;
                    imageView=(ImageView) view;
                    SetFocus();
                }
            }
        });
        imagebox.addView(imageView);
    }

    void CreateControlButton(){
        ImageButton moveButton, resizeButton, removeButton, confirmButton;
        controlButtonBox=new RelativeLayout(context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(0,
                        0);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        controlButtonBox.setLayoutParams(params1);


        moveButton= new ImageButton(context);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(editorScreen.dpToPixel(30), editorScreen.dpToPixel(30));
        params2.setMargins(0,0,0,0);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        moveButton.setLayoutParams(params2);
        moveButton.setTag("moveButton");
        moveButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        moveButton.setImageResource(R.drawable.ic_icon_move);

        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                ViweMove(imagebox, motionEvent);
                return false;
            }
        });

        resizeButton= new ImageButton(context);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(editorScreen.dpToPixel(30), editorScreen.dpToPixel(30));
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resizeButton.setLayoutParams(params3);
        resizeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        resizeButton.setImageResource(R.drawable.ic_icon_resize);

        resizeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ResizeImage(imagebox, motionEvent);
                return false;
            }
        });

        removeButton= new ImageButton(context);
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(editorScreen.dpToPixel(30), editorScreen.dpToPixel(30));
        params5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        removeButton.setLayoutParams(params5);
        removeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        removeButton.setImageResource(R.drawable.ic_icon_close);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveImage();
            }
        });

        confirmButton= new ImageButton(context);
        RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(editorScreen.dpToPixel(30), editorScreen.dpToPixel(30));
        params6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params6.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        confirmButton.setLayoutParams(params6);
        confirmButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        confirmButton.setImageResource(R.drawable.ic_icon_check);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
            }
        });

        borderBox=new RelativeLayout(context);
        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params0.setMargins(editorScreen.dpToPixel(15),editorScreen.dpToPixel(15),editorScreen.dpToPixel(15),editorScreen.dpToPixel(15));
        borderBox.setLayoutParams(params0);
        borderBox.setBackground(ContextCompat.getDrawable(context, R.drawable.border_b));

        controlButtonBox.addView(borderBox);
        controlButtonBox.addView(removeButton);
        controlButtonBox.addView(moveButton);
        controlButtonBox.addView(resizeButton);
        controlButtonBox.addView(confirmButton);
        controlButtonBox.setVisibility(View.GONE);
    }


    void ViweMove(View v, MotionEvent event){
        editorScreen.SetScroll(false);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x= v.getX();
                y= v.getY();

                touchedX=event.getRawX();
                touchedY=event.getRawY();

                break;

            case  MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams p=(RelativeLayout.LayoutParams) v.getLayoutParams();
                p.setMargins((int)(x+(event.getRawX()-touchedX)), (int) (y+(event.getRawY()-touchedY)), 0, 0);
                RelativeLayout.LayoutParams p1=(RelativeLayout.LayoutParams) controlButtonBox.getLayoutParams();
                p1.setMargins((int)(x+(event.getRawX()-touchedX)), (int) (y+(event.getRawY()-touchedY)), 0, 0);

                controlButtonBox.setLayoutParams(p1);
                v.setLayoutParams(p);

                break;

            case  MotionEvent.ACTION_UP:
                editorScreen.SetScroll(true);
                UpdateImageBoxСoordinates();
                break;

            default:
                break;
        }
    }

    void ResizeImage(View v, MotionEvent event){

        editorScreen.SetScroll(false);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = v.getX();
                y = v.getY();
                if (event.getRawX() < (v.getWidth() + v.getX())) {
                    touchedX = (v.getWidth() + v.getX()) - event.getRawX();
                    x = v.getX() - touchedX;
                }

                touchedY = editorScreen.myScrollView.getScrollY();

                break;

            case MotionEvent.ACTION_MOVE:

                float width = event.getRawX() - x;
                float height = event.getRawY() - y - 200 + touchedY;
                v.getLayoutParams().width = Math.round(width);
                v.getLayoutParams().height = Math.round(height);

                v.requestLayout();
                break;

            case  MotionEvent.ACTION_UP:
                editorScreen.SetScroll(true);
                UpdateImegetBox();
                break;

            default:
                break;


        }
    }

    void RemoveImage(){
        if(imagebox!=null) {
            RemoveeImegetBox();
            ClearFocus();
            editorScreen.image_canvas.removeView(imagebox);
        }
    }

    void SetFocus()
    {
        if(editorScreen.textBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.textBoxManager.textbox!=null)
        editorScreen.textBoxManager.ClearFocus();
        if(editorScreen.videoBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.videoBoxManager.videobox!=null)
            editorScreen.videoBoxManager.ClearFocus();
        if(editorScreen.audioBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.audioBoxManager.audiobox!=null)
            editorScreen.audioBoxManager.ClearFocus();
        imagebox.bringToFront();
        controlButtonBox.bringToFront();
        imagebox.post(new Runnable() {
            @Override
            public void run() {
                controlButtonBox.setVisibility(View.VISIBLE);
            }
        });
    }

    void ClearFocus()
    {
        controlButtonBox.setVisibility(View.GONE);
    }

    void CreateNew(){
        imagebox.post(new Runnable() {
            @Override
            public void run() {
                imagebox.post(new Runnable() {
                    @Override
                    public void run() {
                        long lastId;
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        lastId=editorScreen.dbHelper.createView(editorScreen.parentPageId, date, path, imagebox.getHeight(), imagebox.getWidth(),(int)imagebox.getX(), (int)imagebox.getY(),"image");
                        imagebox.setId((int)lastId);
                    }
                });

            }
        });

    }

    void UpdateImageBoxСoordinates(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_X, imagebox.getX() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(imagebox.getId())});
        contentValues.put(DBHelper.KEY_Y, imagebox.getY() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(imagebox.getId())});
    }

    void UpdateImegetBox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_WIDTH, imagebox.getWidth() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(imagebox.getId())});
        contentValues.put(DBHelper.KEY_HEIGHT, imagebox.getHeight() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(imagebox.getId())});
    }

    void RemoveeImegetBox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_ID + "= ?" , new String[] {String.valueOf(imagebox.getId())});
    }
}
