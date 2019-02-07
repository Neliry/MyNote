package com.neliry.db_sql;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Neliry on 09.06.2018.
 */

public class AudioBoxManager {

    RelativeLayout borderBox, audiobox;
    static RelativeLayout controlButtonBox;
    float x, y, touchedX, touchedY;
    EditorScreen editorScreen;
    Context context;
    ImageButton audioButton;
    MediaPlayer mp;

    private static WeakReference<Activity> mActivityRef;

    public static void updateAktivity(Activity activity){
        mActivityRef=new WeakReference<Activity>(activity);
    }


    public AudioBoxManager(Context context){
        this.context=context;
        editorScreen=(EditorScreen) mActivityRef.get();
    }

    void CreateAudiooBox(Uri uri, int height){

        mp= new MediaPlayer();
        try { mp.setDataSource(context, uri); } catch (Exception e) { }
        try { mp.prepare(); } catch (Exception e) { }

//        mp.setDataSource();

        audiobox=new RelativeLayout(context);
                RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(editorScreen.dpToPixel(80),
                        editorScreen.dpToPixel(80));
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        audiobox.setLayoutParams(params0);
        params0.setMargins(10,editorScreen.dpToPixel(50), 0, 0);
        audiobox.setLayoutParams(params0);
        audiobox.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                audiobox.post(new Runnable() {
                    @Override
                    public void run() {

                        RelativeLayout.LayoutParams p1=(RelativeLayout.LayoutParams) controlButtonBox.getLayoutParams();
                        p1.setMargins((int)audiobox.getX(), (int)audiobox.getY(), 0, 0);
                        controlButtonBox.setLayoutParams(p1);
                        controlButtonBox.requestLayout();
                    }
                });

            }
        });

        audioButton= new ImageButton(context);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params2.setMargins(editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15));
        audioButton.setLayoutParams(params2);
        audioButton.setTag(uri.toString());
        audioButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        audioButton.setImageResource(R.drawable.ic_play);
//        audioButton.setTag(mp.getAudioSessionId());

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent=(View) view.getParent();
                if(audiobox!=parent){

                    ClearFocus();
                    stopPlaying();
                    mp= new MediaPlayer();
                }
                if(controlButtonBox.getVisibility()==View.GONE){
                    if (mp!=null)
                    if (!mp.isPlaying())
                    audioButton.setImageResource(R.drawable.ic_play);
                    audiobox=(RelativeLayout) parent;
                    audioButton=(ImageButton) view;
                    SetFocus();
                }
                else {
//                    if (mp==null)
                    audiobox = (RelativeLayout) parent;
                    audioButton = (ImageButton) view;
                    try {
                        mp.setDataSource(context, Uri.parse(audioButton.getTag().toString()));
                    } catch (Exception e) {
                    }
                    try {
                        mp.prepare();
                    } catch (Exception e) {
                    }

                    if (!mp.isPlaying()) {
                        mp.start();
                        if (mp.isPlaying())
                            audioButton.setImageResource(R.drawable.ic_pause);

                    } else {
                        mp.pause();
                        if (!mp.isPlaying())
                            audioButton.setImageResource(R.drawable.ic_play);
                    }
                }

            }
        });

        audiobox.addView(audioButton);

    }
    void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
    void CreateControlButton(){
        ImageButton moveButton, resizeButton, removeButton, confirmButton;
        controlButtonBox=new RelativeLayout(context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(editorScreen.dpToPixel(80),
                        editorScreen.dpToPixel(80));
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

                ViweMove(audiobox, motionEvent);
                return false;
            }
        });

        resizeButton= new ImageButton(context);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(editorScreen.dpToPixel(30), editorScreen.dpToPixel(30));
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resizeButton.setLayoutParams(params3);
        resizeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        resizeButton.setImageResource(R.drawable.ic_replay_24dp);

        resizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Replay();
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
                RemoveAudio();
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
                UpdateAudioBoxСoordinates();
                editorScreen.SetScroll(true);
                break;

            default:
                break;
        }
    }

    void Replay(){
        stopPlaying();
        mp=new MediaPlayer();
        try {
            mp.setDataSource(context, Uri.parse(audioButton.getTag().toString()));
        } catch (Exception e) {
        }
        try {
            mp.prepare();
        } catch (Exception e) {
        }
        mp.start();
        audioButton.setImageResource(R.drawable.ic_pause);
    }

    void RemoveAudio(){

        if(audiobox!=null) {
            stopPlaying();
            RemoveAudioBox();
            ClearFocus();
            editorScreen.image_canvas.removeView(audiobox);
        }
    }

    void SetFocus()
    {
        if(editorScreen.textBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.textBoxManager.textbox!=null)
            editorScreen.textBoxManager.ClearFocus();
        if(editorScreen.videoBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.videoBoxManager.videobox!=null)
            editorScreen.videoBoxManager.ClearFocus();
        if(editorScreen.imageBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.imageBoxManager.imagebox!=null)
            editorScreen.imageBoxManager.ClearFocus();
        if (mp==null)
            mp=new MediaPlayer();
        try { mp.setDataSource(context,Uri.parse(audioButton.getTag().toString())); } catch (Exception e) { }
        try { mp.prepare(); } catch (Exception e) { }

        audiobox.bringToFront();
        controlButtonBox.bringToFront();
        audiobox.post(new Runnable() {
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

    void CreateNew(Uri uri, int takeFlags){
                        long lastId;
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        lastId=editorScreen.dbHelper.createView(editorScreen.parentPageId, date, uri.toString(), takeFlags, 1,(int)audiobox.getX(), (int)audiobox.getY(),"audio");
                        audiobox.setId((int)lastId);
    }

    void UpdateAudioBoxСoordinates(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_X, audiobox.getX() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(audiobox.getId())});
        contentValues.put(DBHelper.KEY_Y, audiobox.getY() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(audiobox.getId())});
    }


    void RemoveAudioBox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_ID + "= ?" , new String[] {String.valueOf(audiobox.getId())});
    }
}
