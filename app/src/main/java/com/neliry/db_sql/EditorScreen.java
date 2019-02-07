package com.neliry.db_sql;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.util.List;


/**
 * Created by Neliry on 14.04.2018.
 */

public class EditorScreen extends AppCompatActivity implements View.OnClickListener {

    private static Context mContext;
    RelativeLayout  text_canvas, image_canvas;
    TextBoxManager textBoxManager;
    ImageBoxManager imageBoxManager;
    VideoBoxManager videoBoxManager;
    AudioBoxManager audioBoxManager;
   static CustomScrollViewHorizontal myHorizontalScrollView;
   static CustomScrollView myScrollView;
    LinearLayout bottomTollBar, textEditBar;
    ImageButton bold_button, italic_button, underline_button, strikethrough_button, color_button, back_color_button, align_left, align_right, align_center;
    private static int RESULT_LOAD_IMG = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    DBHelper dbHelper;
    String parentPageId;
    int maxViewHeight=0;
    private static final int MY_PERMISSION_REQUEST_READ_CONTACTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_READ_CONTACTS);
        }


        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_edittext);
        TextBoxManager.updateAktivity(this);
        ImageBoxManager.updateAktivity(this);
        VideoBoxManager.updateAktivity(this);
        AudioBoxManager.updateAktivity(this);
        mContext=this;
        text_canvas=(RelativeLayout)findViewById(R.id.text_canvas);
        image_canvas=(RelativeLayout)findViewById(R.id.image_canvas);
        text_canvas.getLayoutParams().height=dpToPixel(3500);
        image_canvas.getLayoutParams().height=dpToPixel(3500);
        RelativeLayout canvas=(RelativeLayout)findViewById(R.id.canvas);
        myHorizontalScrollView=(CustomScrollViewHorizontal) findViewById(R.id.horizontalScroll);
        myScrollView = (CustomScrollView) findViewById(R.id.verticalscrol);
        ImageButton addText, addImage, addVideo, addAudio, back;
        back=(ImageButton)findViewById(R.id.imabtnBack) ;
        bottomTollBar =(LinearLayout)findViewById(R.id.tools_on_b) ;
        textEditBar =(LinearLayout)findViewById(R.id.tools_text) ;
        addText=(ImageButton) findViewById(R.id.text);
        addImage=(ImageButton) findViewById(R.id.image);
        addVideo=(ImageButton) findViewById(R.id.video);
        addAudio=(ImageButton) findViewById(R.id.audio);
        bold_button=(ImageButton) findViewById(R.id.bold_button);
        italic_button=(ImageButton) findViewById(R.id.italic_button);
        underline_button=(ImageButton) findViewById(R.id.underline_button);
        strikethrough_button=(ImageButton) findViewById(R.id.strikethrough_button);
        color_button=(ImageButton) findViewById(R.id.text_color_button);
        back_color_button=(ImageButton) findViewById(R.id.back_color_button);
        align_right =(ImageButton) findViewById(R.id.align_right);
        align_left=(ImageButton) findViewById(R.id.align_left);
        align_center=(ImageButton) findViewById(R.id.align_center);
        addText.setOnClickListener(this);
        addImage.setOnClickListener(this);
        addVideo.setOnClickListener(this);
        addAudio.setOnClickListener(this);
        canvas.setOnClickListener(this);
        back.setOnClickListener(this);
        bold_button.setOnClickListener(this);
        italic_button.setOnClickListener(this);
        underline_button.setOnClickListener(this);
        strikethrough_button.setOnClickListener(this);
        color_button.setOnClickListener(this);
        back_color_button.setOnClickListener(this);
        align_right.setOnClickListener(this);
        align_left.setOnClickListener(this);
        align_center.setOnClickListener(this);
        textBoxManager=new TextBoxManager(this);
        textBoxManager.CreateControlButton();
        imageBoxManager=new ImageBoxManager(this);
        imageBoxManager.CreateControlButton();
        videoBoxManager=new VideoBoxManager(this);
        videoBoxManager.CreateControlButton();
        audioBoxManager=new AudioBoxManager(this);
        audioBoxManager.CreateControlButton();

        text_canvas.addView(textBoxManager.controlButtonBox);
        text_canvas.addView(audioBoxManager.controlButtonBox);
        text_canvas.addView(imageBoxManager.controlButtonBox);
        text_canvas.addView(videoBoxManager.controlButtonBox);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            parentPageId = extras.getString("key");
            Log.i("lastId","pid="+  parentPageId);
            LoadSave();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text:
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                textBoxManager.CreateTextBox(width);

                text_canvas.addView(textBoxManager.textbox);
                textBoxManager.textbox.post(new Runnable() {
                    @Override
                    public void run() {
                        textBoxManager.CreateNew();
                        RelativeLayout.LayoutParams p =
                                (RelativeLayout.LayoutParams) textBoxManager.controlButtonBox.getLayoutParams();
                        p.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        p.setMargins((int)textBoxManager.textbox.getX(),(int)textBoxManager.textbox.getY(), 0, 0);
                        textBoxManager.controlButtonBox.setLayoutParams(p);
                        textBoxManager.controlButtonBox.bringToFront();
                        textBoxManager.SetFocus();
                        textBoxManager.editText.setFocusableInTouchMode(true);
                        textBoxManager.editText.requestFocus();
                        showKeyboard(textBoxManager.editText);
                    }
                });
                break;
            case R.id.canvas:
                if(textBoxManager.textbox!=null&&textBoxManager.controlButtonBox.getVisibility()==View.VISIBLE)
                    textBoxManager.ClearFocus();
                if(imageBoxManager.imagebox!=null&&imageBoxManager.controlButtonBox.getVisibility()==View.VISIBLE)
                    imageBoxManager.ClearFocus();
                if(videoBoxManager.videobox!=null&&videoBoxManager.controlButtonBox.getVisibility()==View.VISIBLE)
                    videoBoxManager.ClearFocus();
                if(audioBoxManager.audiobox!=null&&audioBoxManager.controlButtonBox.getVisibility()==View.VISIBLE)
                    audioBoxManager.ClearFocus();
                break;
            case R.id.bold_button:
                if(!textBoxManager.isBold) {
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                        textBoxManager.editText.getEditableText().setSpan(new StyleSpan(Typeface.BOLD),
                                textBoxManager.selectionStart,
                                textBoxManager.selectionEnd,
                                Typeface.BOLD);
                    textBoxManager.isBold=true;
                }
                else{
                      if(textBoxManager.selectionStart!=textBoxManager.selectionEnd) {
                            StyleSpan[] styleSpans = textBoxManager.editText.getEditableText().getSpans(textBoxManager.selectionStart,
                                    textBoxManager.selectionEnd, StyleSpan.class);
                          textBoxManager.RemoveSpan(styleSpans,textBoxManager.selectionStart,  textBoxManager.selectionEnd, (byte)1 );
                      }
                    textBoxManager.isBold=false;
                }
                textBoxManager.ButtonColorChange();
                textBoxManager.UpdateText();
                break;
            case R.id.italic_button:
                if(!textBoxManager.isItalic) {
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                        textBoxManager.editText.getEditableText().setSpan(new StyleSpan(Typeface.ITALIC),
                                textBoxManager.selectionStart,
                                textBoxManager.selectionEnd,
                                Typeface.ITALIC);
                    textBoxManager.isItalic = true;
                }
                else{
                    if(textBoxManager.selectionStart!=textBoxManager.selectionEnd) {
                        StyleSpan[] styleSpans = textBoxManager.editText.getEditableText().getSpans(textBoxManager.selectionStart,
                                textBoxManager.selectionEnd, StyleSpan.class);
                        textBoxManager.RemoveSpan(styleSpans,textBoxManager.selectionStart,  textBoxManager.selectionEnd, (byte) 2 );
                    }
                    textBoxManager.isItalic=false;
                    }
                textBoxManager.ButtonColorChange();
                textBoxManager.UpdateText();
                break;
            case R.id.underline_button:
                if(!textBoxManager.isUnderline) {
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                    textBoxManager.editText.getEditableText().setSpan(new UnderlineSpan(),
                            textBoxManager.selectionStart,
                            textBoxManager.selectionEnd,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    textBoxManager.isUnderline = true;
                }
                else{
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd) {
                        UnderlineSpan[] underlineSpans = textBoxManager.editText.getEditableText().getSpans(textBoxManager.selectionStart,
                                textBoxManager.selectionEnd, UnderlineSpan.class);
                        textBoxManager.RemoveSpan(underlineSpans, textBoxManager.selectionStart,  textBoxManager.selectionEnd);
                    }
                    textBoxManager.isUnderline=false;
                }
                textBoxManager.ButtonColorChange();
                textBoxManager.UpdateText();
                break;
            case R.id.strikethrough_button:
                if(!textBoxManager.isStrikethrough) {
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                        textBoxManager.editText.getEditableText().setSpan(new StrikethroughSpan(),
                                textBoxManager.selectionStart,
                                textBoxManager.selectionEnd,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    textBoxManager.isStrikethrough = true;
                }
                else{
                    if(textBoxManager.selectionStart!= textBoxManager.selectionEnd) {
                        StrikethroughSpan[] StrikethroughSpan = textBoxManager.editText.getEditableText().getSpans(textBoxManager.selectionStart,
                                textBoxManager.selectionEnd, StrikethroughSpan.class);
                        textBoxManager.RemoveStrikethroughSpan(StrikethroughSpan, textBoxManager.selectionStart,  textBoxManager.selectionEnd);
                    }
                    textBoxManager.isStrikethrough=false;
                }
                textBoxManager.ButtonColorChange();
                textBoxManager.UpdateText();
                break;
            case R.id.align_right:
                textBoxManager.editText.setGravity(Gravity.RIGHT);
                textBoxManager.g="r";
                textBoxManager.UpdateGravity();
                break;
            case R.id.align_left:
                textBoxManager.editText.setGravity(Gravity.LEFT);
                textBoxManager.g="l";
                textBoxManager.UpdateGravity();
                break;
            case R.id.align_center:
                textBoxManager.editText.setGravity(Gravity.CENTER);
                textBoxManager.g="c";
                textBoxManager.UpdateGravity();
                break;
            case R.id.text_color_button:
                if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                   pickColor(v);
                break;
            case R.id.back_color_button:
                if(textBoxManager.selectionStart!= textBoxManager.selectionEnd)
                    pickBackColor(v);
                break;
            case R.id.image:
                loadImagefromGallery(v);
                break;
            case R.id.video:
                loadVideofromGallery(v);
                break;
            case R.id.audio:
                loadAudiofromGallery(v);
                break;
            case R.id.imabtnBack:
                if(audioBoxManager.audiobox!=null)
                audioBoxManager.stopPlaying();
                Intent t=new Intent(EditorScreen.this, MainActivity.class);
                startActivity(t);
                break;
            default:
                break;
        }
    }



    public static Context getContext()
    {
        return mContext;
    }

    public void SetScroll(Boolean s){
        myHorizontalScrollView.setEnableScrolling(s);
        myScrollView.setEnableScrolling(s);
    }

    public  int dpToPixel(float dp)  {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    public void showKeyboard(View v) {
       InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
    public void hideKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }



    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void loadVideofromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void loadAudiofromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                Uri selectedMediaUri = data.getData();
                if (selectedMediaUri.toString().contains("image")) {
                    if (requestCode == RESULT_LOAD_IMG && null != data) {

                        Uri selectedImage = data.getData();

                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        String realPath="";

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if(cursor.moveToFirst()){
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            realPath = cursor.getString(columnIndex);
                        } else {}
                        cursor.close();
//                        drawable = new BitmapDrawable(BitmapFactory
//                                .decodeFile(imgDecodableString));
                        File imgFile = new  File(realPath);
                        if(imgFile.exists()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(myBitmap);
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        imageBoxManager.CreateImageBox(bitmapDrawable, size.y/4+myScrollView.getScrollY(), realPath);
                        image_canvas.addView(imageBoxManager.imagebox);
                        imageBoxManager.imagebox.post(new Runnable() {
                            @Override
                            public void run() {
                                imageBoxManager.SetFocus();
                                imageBoxManager.CreateNew();
                            }

                        });
                        }
                    } else {}
                } else if (selectedMediaUri.toString().contains("video")) {
                    if ( requestCode == REQUEST_VIDEO_CAPTURE  && null != data) {
                        final Uri selectedVideoUri = data.getData();
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        String realPath="";
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedVideoUri, filePathColumn, null, null, null);
                        if(cursor.moveToFirst()){
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            realPath = cursor.getString(columnIndex);
                        } else {}
                        cursor.close();
                        videoBoxManager.CreateVideoBox( realPath, dpToPixel(30)+myScrollView.getScrollY());
                        image_canvas.addView(videoBoxManager.videobox);
                        videoBoxManager.videobox.post(new Runnable() {
                            @Override
                            public void run() {
                                videoBoxManager.controlButtonBox.bringToFront();
                                videoBoxManager.SetFocus();
                                videoBoxManager.CreateNew();
                            }
                        });
                    }
                }
                else if (selectedMediaUri.toString().contains("audio")){
                    if ( null != data) {

                        final Uri selectedAudioUri = data.getData();
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        mContext.grantUriPermission(mContext.getPackageName(), selectedAudioUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        audioBoxManager.CreateAudiooBox(selectedAudioUri, size.y/4+myScrollView.getScrollY());
                        image_canvas.addView(audioBoxManager.audiobox);
                        audioBoxManager.audiobox.post(new Runnable() {
                            @Override
                            public void run() {
                                audioBoxManager.CreateNew(selectedAudioUri, takeFlags);
                                audioBoxManager.SetFocus();
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
        }
    }
    void LoadSave(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_VIEWS, null, "parent = ?", new String[] {parentPageId},  null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int xIndex = cursor.getColumnIndex(DBHelper.KEY_X);
            int yIndex = cursor.getColumnIndex(DBHelper.KEY_Y);
            int widthIndex = cursor.getColumnIndex(DBHelper.KEY_WIDTH);
            int heightIndex = cursor.getColumnIndex(DBHelper.KEY_HEIGHT);
            int contentIndex = cursor.getColumnIndex(DBHelper.KEY_CONTENT);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);

            do {
                final Cursor cursor1=cursor;
                final int id=Integer.parseInt(cursor1.getString(idIndex));
                final String name=cursor1.getString(nameIndex);
                final String content=cursor1.getString(contentIndex);
                final int x=Integer.parseInt(cursor1.getString(xIndex));
                final int y=Integer.parseInt(cursor1.getString(yIndex));
                final int w=Integer.parseInt(cursor1.getString(widthIndex));
                final int h=Integer.parseInt(cursor1.getString(heightIndex));
                String g=cursor.getString(dateIndex);
                Display display1 = getWindowManager().getDefaultDisplay();
                Point size1 = new Point();
                display1.getSize(size1);
                final String realPath=cursor.getString(contentIndex);
                final Point size2=size1;
                                switch (name) {
                                    case "text":
                                        textBoxManager.CreateTextBox(w);
                                        textBoxManager.textbox.setId(id);
                                        Spanned htmlDescription = Html.fromHtml(content);
                                        String descriptionWithOutExtraSpace = new String(htmlDescription.toString()).trim();
                                        textBoxManager.editText.setText(htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length()));
                                        RelativeLayout.LayoutParams p1 =
                                                (RelativeLayout.LayoutParams) textBoxManager.textbox.getLayoutParams();
                                        p1.setMargins(x, y, 0, 0);
                                        textBoxManager.textbox.setLayoutParams(p1);
                                        switch (g){
                                            case "r":
                                                textBoxManager.editText.setGravity(Gravity.RIGHT);
                                                textBoxManager.g="r";
                                                break;
                                            case "l":
                                                textBoxManager.editText.setGravity(Gravity.LEFT);
                                                textBoxManager.g="l";
                                                break;
                                            case "c":
                                                textBoxManager.editText.setGravity(Gravity.CENTER);
                                                textBoxManager.g="c";
                                                break;
                                            default:
                                                break;
                                        }
                                        text_canvas.addView(textBoxManager.textbox);
                                        textBoxManager.controlButtonBox.bringToFront();
                                        textBoxManager.ClearFocus();
                                        break;
                                    case "image":
                                        String path=(content);
                                        Display display = getWindowManager().getDefaultDisplay();
                                        Point size = new Point();
                                        display.getSize(size);

                                        File imgFile = new  File(path);
                                        if(imgFile.exists()) {
                                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                            BitmapDrawable bitmapDrawable = new BitmapDrawable(myBitmap);

                                            imageBoxManager.CreateImageBox(bitmapDrawable, size.y / 4 + myScrollView.getScrollY(), path);
                                            imageBoxManager.imagebox.setId(id);
                                            RelativeLayout.LayoutParams p2 =
                                                    (RelativeLayout.LayoutParams) imageBoxManager.imagebox.getLayoutParams();
                                            p2.setMargins(x, y, 0, 0);
                                            imageBoxManager.imagebox.setLayoutParams(p2);
                                            imageBoxManager.imagebox.getLayoutParams().width = w;
                                            imageBoxManager.imagebox.getLayoutParams().height = h;
                                            imageBoxManager.controlButtonBox.bringToFront();
                                            image_canvas.addView(imageBoxManager.imagebox);
                                            imageBoxManager.ClearFocus();
                                        }
                                        break;
                                    case "video":

                                        videoBoxManager.CreateVideoBox( realPath, size2.y/4+myScrollView.getScrollY());
                                        videoBoxManager.videobox.setId(id);
                                        RelativeLayout.LayoutParams p3 =
                                                (RelativeLayout.LayoutParams) videoBoxManager.videobox.getLayoutParams();
                                        p3.setMargins(x, y, 0, 0);
                                        videoBoxManager.videobox.setLayoutParams(p3);
                                        videoBoxManager.videobox.getLayoutParams().width=w;
                                        videoBoxManager.controlButtonBox.bringToFront();
                                        image_canvas.addView(videoBoxManager.videobox);
                                        videoBoxManager.ClearFocus();
                                        break;
                                    case "audio":
                                        Uri uri= Uri.parse(content);
                                        EditorScreen.this.grantUriPermission(EditorScreen.this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        audioBoxManager.CreateAudiooBox(uri, dpToPixel(10));
                                        RelativeLayout.LayoutParams p4 =
                                                (RelativeLayout.LayoutParams) audioBoxManager.audiobox.getLayoutParams();
                                        p4.setMargins(x, y, 0, 0);

                                        audioBoxManager.audiobox.setLayoutParams(p4);
                                        audioBoxManager.audiobox.setId(id);
                                        image_canvas.addView(audioBoxManager.audiobox);
                                        audioBoxManager.stopPlaying();
                                        audioBoxManager.ClearFocus();

                                        break;
                                        default:
                                            break;
                                }

            }
            while (cursor.moveToNext()) ;
            cursor.close();
        }
        myScrollView.scrollTo(0, myScrollView.getBottom());
    }

    public void pickColor(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(0xFFffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(10)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {

                        ForegroundColorSpan[] spans = new SpannableString(textBoxManager.editText.getText()).getSpans(textBoxManager.selectionStart,  textBoxManager.selectionEnd, ForegroundColorSpan.class);
                        if (spans.length != 0) {
                            for (CharacterStyle span : spans) {
                                textBoxManager.editText.getEditableText().removeSpan(span);
                            }
                        }
                        textBoxManager.editText.getEditableText().setSpan(new ForegroundColorSpan(selectedColor),
                                textBoxManager.selectionStart,  textBoxManager.selectionEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textBoxManager.editText.post(new Runnable() {
                            @Override
                            public void run() {
                                textBoxManager.UpdateText();
                            }
                        });
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
    public void pickBackColor(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(0xFFffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(10)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {

                        BackgroundColorSpan[] spans = new SpannableString(textBoxManager.editText.getText()).getSpans(textBoxManager.selectionStart,  textBoxManager.selectionEnd, BackgroundColorSpan.class);
                        if (spans.length != 0) {
                            for (CharacterStyle span : spans) {
                                textBoxManager.editText.getEditableText().removeSpan(span);
                            }
                        }
                        textBoxManager.editText.getEditableText().setSpan(new BackgroundColorSpan(selectedColor),
                                textBoxManager.selectionStart,  textBoxManager.selectionEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textBoxManager.editText.post(new Runnable() {
                            @Override
                            public void run() {
                                textBoxManager.UpdateText();
                            }
                        });
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

}
