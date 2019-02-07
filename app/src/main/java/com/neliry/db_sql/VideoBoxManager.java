package com.neliry.db_sql;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Neliry on 04.06.2018.
 */

public class VideoBoxManager {
    RelativeLayout   borderBox;
    FrameLayout videobox;
    static RelativeLayout controlButtonBox;
    float x, y, touchedX, touchedY;
    EditorScreen editorScreen;
    Context context;
    VideoView videoView;
    String previewImage, path;

    private static WeakReference<Activity> mActivityRef;

    public static void updateAktivity(Activity activity){
        mActivityRef=new WeakReference<Activity>(activity);
    }


    public VideoBoxManager(Context context){
        this.context=context;
        editorScreen=(EditorScreen) mActivityRef.get();

    }

    void CreateVideoBox( String realPath,  int height){
        videobox=new FrameLayout(context);
        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(editorScreen.dpToPixel(300),
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        videobox.setLayoutParams(params0);
        params0.setMargins(10,height, 0, 0);
        videobox.setLayoutParams(params0);
        videobox.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                videobox.post(new Runnable() {
                    @Override
                    public void run() {
                        controlButtonBox.getLayoutParams().height=videobox.getHeight();
                        controlButtonBox.getLayoutParams().width=videobox.getWidth();
                        controlButtonBox.requestLayout();
                    }
                });
            }
        });
        FrameLayout frameLayout=new FrameLayout(context);
        RelativeLayout.LayoutParams params2 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params2.setMargins(editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15), editorScreen.dpToPixel(15));
        frameLayout.setLayoutParams(params2);
        frameLayout.setTag("frameLayout");
        videobox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videobox!=view){
                    ClearFocus();
                }
                if(controlButtonBox.getVisibility()==View.GONE){
                    videobox=(FrameLayout) view;
                    SetFocus();
                }
                videoView= (VideoView) videobox.findViewWithTag("videoView");
                videoView.setBackground(null);
            }
        });

        path=realPath;
        videoView=new VideoView(context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        videoView.setLayoutParams(params1);
        videoView.setVideoPath(realPath);
        MediaController mediaController = new MediaController(context);
        videoView.setMediaController(mediaController);
        videoView.setTag("videoView");
        mediaController.setAnchorView(videoView);
        previewImage=realPath;

        if(realPath!=""){
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(realPath,
                MediaStore.Images.Thumbnails.MINI_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
            videoView.getLayoutParams().height=editorScreen.dpToPixel(300);
            videoView.setBackgroundDrawable(bitmapDrawable);
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                if(previewImage!=""){
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(previewImage,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                    videoView.setBackgroundDrawable(bitmapDrawable);
                }
            }
        });
        videobox.setTag("videobox");
        frameLayout.addView(videoView);
        videobox.addView(frameLayout);
    }

    void CreateControlButton(){
        ImageButton moveButton, resizeButton, removeButton, confirmButton;
        controlButtonBox=new RelativeLayout(context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(0,
                        0);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params1.setMargins(0,0, 0, 0);
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

                ViweMove(videobox, motionEvent);
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
                ResizeVideo(videobox, motionEvent);
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
                RemoveVideo();
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
                UpdateVideoBoxСoordinates();
                break;

            default:
                break;
        }
    }

    void ResizeVideo(View v, MotionEvent event){

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
                v.getLayoutParams().width = Math.round(width);
                v.requestLayout();
                videoView.getLayoutParams().height=RelativeLayout.LayoutParams.WRAP_CONTENT;
                break;

            case  MotionEvent.ACTION_UP:
                editorScreen.SetScroll(true);
                UpdateVideoBox();
                break;

            default:
                break;


        }
    }

    void RemoveVideo(){
        if(videobox!=null) {
            RemoveVideobox();
            ClearFocus();
            editorScreen.image_canvas.removeView(videobox);
        }
    }

    void SetFocus()
    {
        if(editorScreen.textBoxManager.textbox!=null)
            editorScreen.textBoxManager.ClearFocus();
        if(editorScreen.imageBoxManager.imagebox!=null)
            editorScreen.imageBoxManager.ClearFocus();
        if(editorScreen.audioBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.audioBoxManager.audiobox!=null)
            editorScreen.audioBoxManager.ClearFocus();
        editorScreen.videoBoxManager.videobox.bringToFront();
        editorScreen.videoBoxManager.controlButtonBox.bringToFront();
        videobox.post(new Runnable() {
            @Override
            public void run() {
                controlButtonBox.getLayoutParams().height=videobox.getHeight();
                controlButtonBox.getLayoutParams().width=videobox.getWidth();
                controlButtonBox.requestLayout();
                RelativeLayout.LayoutParams p1=(RelativeLayout.LayoutParams) controlButtonBox.getLayoutParams();
                p1.setMargins((int)videobox.getX(), (int)videobox.getY(), 0, 0);
                controlButtonBox.setLayoutParams(p1);
                controlButtonBox.requestLayout();
                controlButtonBox.setVisibility(View.VISIBLE);
            }
        });

    }

    void ClearFocus()
    {
        controlButtonBox.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!videoView.isPlaying()){
                    videoView.seekTo(0);
                    if(previewImage!=""){
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(previewImage,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        final BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                        editorScreen.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                videoView.setBackgroundDrawable(bitmapDrawable);
                            }
                        });

                    }
                }
            }
        }).start();

    }

    void CreateNew(){
        long lastId;
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        lastId=editorScreen.dbHelper.createView(editorScreen.parentPageId, date, path, videobox.getHeight(), videobox.getWidth(),(int)videobox.getX(), (int)videobox.getY(),"video");
        videobox.setId((int)lastId);
        editorScreen.dbHelper.close();
    }
//editorScreen.dpToPixel(300)
    void UpdateVideoBoxСoordinates(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_X, videobox.getX() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(videobox.getId())});
        contentValues.put(DBHelper.KEY_Y, videobox.getY() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(videobox.getId())});
    }

    void UpdateVideoBox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_WIDTH, videobox.getWidth() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(videobox.getId())});
        contentValues.put(DBHelper.KEY_HEIGHT, videobox.getHeight() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(videobox.getId())});
    }

    void RemoveVideobox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_ID + "= ?" , new String[] {String.valueOf(videobox.getId())});
    }

}
