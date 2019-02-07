package com.neliry.db_sql;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Edittext extends AppCompatActivity {
    ImageButton button_text, button_image;
    RelativeLayout text_canvast, image_canvast;
    private Toolbar toolbar;
    DBHelper dbHelper;
    Context context=this;
    RelativeLayout rl, box;
    ImageButton moveButton, resizeButton, removeButton, checkButton, back;
    CutCopyPasteEditText editText;
    CustomScrollViewHorizontal myHorizontalScrollView;
    int line;
    CustomScrollView myScrollView;
    float x, y, touchedX, touchedY;
   ImageView imageView;
    private static final int MY_PERMISSION_REQUEST_READ_CONTACTS = 10;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    Drawable drawable;
    StyleSpan[] spans;
    SpannableString texts;
    boolean isBold=false, isItalic=false, isUnderline=false, isStrike=false;
    int n=1;
    String parentPageId;
    boolean load=false;

    public void setId(String parentPageId) {
        this.parentPageId = parentPageId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittext);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            parentPageId = extras.getString("key");
            Log.i("size1","pid="+  parentPageId);
            //The key argument here must match that used in the other activity
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_READ_CONTACTS);
        }

        //Подключаю actionbar
        toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Задаю переменные
        myHorizontalScrollView=(CustomScrollViewHorizontal) findViewById(R.id.horizontalScroll);
        myScrollView = (CustomScrollView) findViewById(R.id.verticalscrol);
        dbHelper = new DBHelper(this);
        button_image=(ImageButton)findViewById(R.id.image);
        button_text=(ImageButton)findViewById(R.id.text);
        back=(ImageButton)findViewById(R.id.imabtnBack) ;
        text_canvast=(RelativeLayout)findViewById(R.id.text_canvas);
        image_canvast=(RelativeLayout)findViewById(R.id.image_canvas);
        LinearLayout boldButton, italicButton, underlineButton, strikeButton, colorButton, bColorButton;
        italicButton=(LinearLayout)findViewById(R.id.italic);
        underlineButton=(LinearLayout)findViewById(R.id.underline);
        strikeButton=(LinearLayout)findViewById(R.id.strikethrough);
        colorButton=(LinearLayout)findViewById(R.id.text_color);
        bColorButton=(LinearLayout)findViewById(R.id.b_color);
        boldButton=(LinearLayout)findViewById(R.id.bold);

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(!isBold)
                SetBold();
            else ClearBold();
            }
        });
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isItalic)
                    SetItalic();
                else ClearItalic();
            }
        });
        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isUnderline)
                    SetUnderline();
                else ClearUnderline();
            }
        });
        strikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isStrike)
                    SetStrike();
                else ClearStrike();
            }
        });
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetColor();
            }
        });
        bColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetBColor();
            }
        });

        RelativeLayout canvas=(RelativeLayout)findViewById(R.id.canvas);
        canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClearFocus();
            }
        });

        button_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
                Create();
            }
        });

        button_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
                loadImagefromGallery(view);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t=new Intent(Edittext.this, MainActivity.class);
                startActivity(t);
            }
        });
//        Create();
//
//        getSupportActionBar().hide();

    }


    public void Create(){

        rl=new RelativeLayout(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = myScrollView.getScrollY();

        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(width,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params0.setMargins(0, height, 0, 0);
        rl.setLayoutParams(params0);
//        rl.setId(View.generateViewId());
//        Log.d("testID", "old id = " + rl.getId());
        box=new RelativeLayout(this);
        RelativeLayout.LayoutParams params10 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        box.setLayoutParams(params10);

        editText=new CutCopyPasteEditText (this);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(dpToPixel(18), dpToPixel(18), dpToPixel(18), 0);
        editText.setPadding(dpToPixel(4),dpToPixel(4),dpToPixel(4),dpToPixel(4));
        editText.setTag("edittext");
        editText.setLayoutParams(params1);
        editText.setBackground(ContextCompat.getDrawable(context, R.drawable.border_b));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        editText.setId(View.generateViewId());
//        editText.setBackgroundResource(0);


        moveButton= new ImageButton(this);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params2.setMargins(0,0,0,0);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        moveButton.setLayoutParams(params2);
        moveButton.setTag("moveButton");
        moveButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        moveButton.setImageResource(R.drawable.ic_icon_move);

        resizeButton= new ImageButton(this);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resizeButton.setLayoutParams(params3);
        resizeButton.setTag("resizeButton");
        resizeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        resizeButton.setImageResource(R.drawable.ic_icon_resize);

        removeButton= new ImageButton(this);
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        removeButton.setLayoutParams(params5);
        removeButton.setTag("closeButton");
        removeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        removeButton.setImageResource(R.drawable.ic_icon_close);

        checkButton= new ImageButton(this);
        RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params6.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        checkButton.setLayoutParams(params6);
        checkButton.setTag("checkButton");
        checkButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        checkButton.setImageResource(R.drawable.ic_icon_check);

////                RelativeLayout custom_rl=(RelativeLayout)findViewById(R.id.rl);
//
//



        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(editText);
                ViweMove(rl, motionEvent);
                return false;
            }
        });
////
        resizeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ResizeEdittext(rl, motionEvent);
                return false;
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
                RemoveEdittext();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                int parentId = ((View) view.getParent()).getId();
//                RelativeLayout b=(RelativeLayout)findViewById(parentId);
//                parentId=((View) b.getParent()).getId();
                View parent=(View) view.getParent();
                parent=(View) parent.getParent();

                int parentId = parent.getId();
                if(rl!=parent){
                    ClearFocus();
                }
                view.setFocusableInTouchMode(true);
                rl=(RelativeLayout) parent;
//                Log.d("testID", "parent id = " + parent.getId());
//                Log.d("testID", "rl id = " + rl.getId());
                view.requestFocus();
                showKeyboard(view);
                editText=(CutCopyPasteEditText)view;
                ViweOnFocus();

            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                    rl.getLayoutParams().height = editText.getLineCount() * editText.getLineHeight() + 2 * editText.getLineHeight() + dpToPixel(3f);
//                if(line<getCurrentCursorLine( editText)){
//
//                    int totalHeight = Math.round(((editText.getLineCount()+1) * (editText.getLineHeight() + editText.getLineSpacingExtra()) *
//                            editText.getLineSpacingMultiplier())) + editText.getCompoundPaddingTop() + editText.getCompoundPaddingBottom();
//
//                    rl.getLayoutParams().height=editText.getHeight()+2*editText.getLineHeight()+ dpToPixel(13f);
//                }
                    if(!load)
                    if (line > getCurrentCursorLine(editText)) {
                        rl.getLayoutParams().height = editText.getHeight() + dpToPixel(13f);
                        int totalHeight = Math.round(((editText.getLineCount()) * (editText.getLineHeight() + editText.getLineSpacingExtra()) *
                                editText.getLineSpacingMultiplier())) + editText.getCompoundPaddingTop() + editText.getCompoundPaddingBottom();


                }
                    texts = new SpannableString(editText.getText());

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                if(!load)
                line=getCurrentCursorLine(editText);
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                editText.getText().setSpan(watcher, 0, editText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        });

        editText.setOnCutCopyPasteListener(new CutCopyPasteEditText.OnCutCopyPasteListener() {
            @Override
            public void onCut() {
                // Do your onCut reactions
            }

            @Override
            public void onCopy() {
                // Do your onCopy reactions
            }

            @Override
            public void onPaste() {
                // Do your onPaste reactions
                editText.append(" ");

            }
        });
        editText.getText().setSpan(watcher, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        int totalHeight = Math.round((2* (editText.getLineHeight() + editText.getLineSpacingExtra()) *
                editText.getLineSpacingMultiplier())) + editText.getCompoundPaddingTop() + editText.getCompoundPaddingBottom();

        rl.getLayoutParams().height=totalHeight+ dpToPixel(13f);


        box.addView(editText);
        rl.addView(box);
        rl.addView(moveButton);
        rl.addView(removeButton);
        rl.addView(checkButton);
        rl.addView(resizeButton);
        text_canvast.addView(rl);
            showKeyboard(editText);
            ViweOnFocus();
            editText.requestFocus();

    }


    private void ImageCreate(){
        rl=new RelativeLayout(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = drawable.getIntrinsicWidth();
        int height = size.y/4+myScrollView.getScrollY();
        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params0.setMargins(dpToPixel(5),height, 0, 0);
        rl.setLayoutParams(params0);
        rl.setId(View.generateViewId());
        Log.d("testID", "r id = " + rl.getId());
//        rl.setBackgroundColor(Color.rgb(226, 11, 11));

        imageView=new ImageView(this);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        params1.setMargins(dpToPixel(13), dpToPixel(13), dpToPixel(13), dpToPixel(13));
        imageView.setLayoutParams(params1);
        imageView.setTag("imageView");
        imageView.setBackground(drawable);
        imageView.setId(View.generateViewId());

        moveButton= new ImageButton(this);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params2.setMargins(0,0,0,0);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        moveButton.setLayoutParams(params2);
        moveButton.setTag("moveButton");
        moveButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        moveButton.setImageResource(R.drawable.ic_icon_move);

        resizeButton= new ImageButton(this);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resizeButton.setLayoutParams(params3);
        resizeButton.setTag("resizeButton");
        resizeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        resizeButton.setImageResource(R.drawable.ic_icon_resize);

        removeButton= new ImageButton(this);
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        removeButton.setLayoutParams(params5);
        removeButton.setTag("closeButton");
        removeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        removeButton.setImageResource(R.drawable.ic_icon_close);

        checkButton= new ImageButton(this);
        RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(dpToPixel(27), dpToPixel(27));
        params6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params6.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        checkButton.setLayoutParams(params6);
        checkButton.setTag("checkButton");
        checkButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_bg_round));
        checkButton.setImageResource(R.drawable.ic_icon_check);

        moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ViweMove(rl, motionEvent);
                return false;
            }
        });
////
        resizeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ResizeImage(rl, motionEvent);
                return false;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int parentId = ((View) view.getParent()).getId();
                if(rl!=(RelativeLayout)findViewById(parentId)){
                    ClearFocus();
                }
                rl=(RelativeLayout)findViewById(parentId);
                ImageOnFocus();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_canvast.removeView(rl);
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearFocus();
            }
        });

        rl.addView(imageView);
        rl.addView(moveButton);
        rl.addView(removeButton);
        rl.addView(checkButton);
        rl.addView(resizeButton);
        image_canvast.addView(rl);
    }

    void RemoveEdittext(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = dbHelper.getWritableDatabase();
        int delCount = database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_ID + "= ?" , new String[] {String.valueOf(rl.getId())});
        text_canvast.removeView(rl);
    }

    protected void ClearFocus() {
        if (rl != null){
        LinearLayout tools_button = (LinearLayout) findViewById(R.id.tools_text);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.tools_on_b);
            linearLayout.setVisibility(View.VISIBLE);
        tools_button.setVisibility(View.GONE);
        resizeButton.setVisibility(View.GONE);
        moveButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);

            if (editText != null) {

//            getSupportActionBar().show();
                editText.clearFocus();
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
//            rl.setBackgroundResource(0);
                editText.setBackgroundResource(0);
                hideKeyboard(editText);
                if (editText.getText().length() == 0) {
                    RemoveEdittext();
                }
            }
    }
    }



    protected void ViweOnFocus() {
//        getSupportActionBar().hide();
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.tools_on_b);
        linearLayout.setVisibility(View.GONE);
        LinearLayout tools_button=(LinearLayout)findViewById(R.id.tools_text);
        tools_button.setVisibility(View.VISIBLE);

        rl.bringToFront();
//        relativeLayout.bringToFront();
//        rl.setBackground(ContextCompat.getDrawable(context, R.drawable.border_b));
//
//        editText=(EditText)rl.findViewWithTag("edittext");
        editText.setBackground(ContextCompat.getDrawable(context, R.drawable.border_b));
        editText.requestFocus();
        showKeyboard(editText);
        moveButton.setVisibility(View.GONE);
        moveButton=(ImageButton)rl.findViewWithTag("moveButton");
        moveButton.setVisibility(View.VISIBLE);
        resizeButton.setVisibility(View.GONE);
        resizeButton=(ImageButton)rl.findViewWithTag("resizeButton");
        resizeButton.setVisibility(View.VISIBLE);
        removeButton.setVisibility(View.GONE);
        removeButton=(ImageButton)rl.findViewWithTag("closeButton");
        removeButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.GONE);
        checkButton=(ImageButton)rl.findViewWithTag("checkButton");
        checkButton.setVisibility(View.VISIBLE);
        Log.d("testID", "on clic id = " + rl.getId());

    }
    protected void ImageOnFocus() {
//        getSupportActionBar().hide();
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.tools_on_b);
        linearLayout.setVisibility(View.VISIBLE);
        LinearLayout tools_button=(LinearLayout)findViewById(R.id.tools_text);
        tools_button.setVisibility(View.GONE);
        rl.bringToFront();
//        relativeLayout.bringToFront();
//        rl.setBackground(ContextCompat.getDrawable(context, R.drawable.border_b));
        moveButton.setVisibility(View.GONE);
        moveButton=(ImageButton)rl.findViewWithTag("moveButton");
        moveButton.setVisibility(View.VISIBLE);
        resizeButton.setVisibility(View.GONE);
        resizeButton=(ImageButton)rl.findViewWithTag("resizeButton");
        resizeButton.setVisibility(View.VISIBLE);
        removeButton.setVisibility(View.GONE);
        removeButton=(ImageButton)rl.findViewWithTag("closeButton");
        removeButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.GONE);
        checkButton=(ImageButton)rl.findViewWithTag("checkButton");
        checkButton.setVisibility(View.VISIBLE);
    }

    void ViweMove(View v, MotionEvent event){
//        v.getLayoutParams().height=editText.getHeight()+dpToPixel(11f);
        myHorizontalScrollView.setEnableScrolling(false);
        myScrollView.setEnableScrolling(false);
        //long startTime = System.currentTimeMillis();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x= v.getX();
                y= v.getY();

                touchedX=event.getRawX();
                touchedY=event.getRawY();

                break;

            case  MotionEvent.ACTION_MOVE:
//                v.getLayoutParams().height=editText.getHeight()+dpToPixel(11f);
//                v.setX((int)(x+(event.getRawX()-touchedX)));
//                    v.setY((int)(y+(event.getRawY()-touchedY)));
                RelativeLayout.LayoutParams p=(RelativeLayout.LayoutParams) v.getLayoutParams();
                p.setMargins((int)(x+(event.getRawX()-touchedX)), (int) (y+(event.getRawY()-touchedY)), 0, 0);
                v.setLayoutParams(p);

                break;

            case  MotionEvent.ACTION_UP:
                myHorizontalScrollView.setEnableScrolling(true);
                myScrollView.setEnableScrolling(true);
                break;

            default:
                break;



        }
    }

    void ResizeEdittext(View v, MotionEvent event){
//        v.getLayoutParams().height=editText.getHeight()+ dpToPixel(11f);
        myHorizontalScrollView.setEnableScrolling(false);
        myScrollView.setEnableScrolling(false);
//
//        editText=(EditText) v.findViewWithTag("edittext");

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x= v.getX();
                y= v.getY();
                if(event.getRawX()<(v.getWidth()+v.getX())){
                    touchedX=(v.getWidth()+v.getX())-event.getRawX();
                    x= v.getX()-touchedX;
                }

                touchedY=myScrollView.getScrollY();

                break;

            case  MotionEvent.ACTION_MOVE:
//                    v.setX((int)(x+(event.getRawX()-touchedX)));
//                    v.setY((int)(y+(event.getRawY()-touchedY)));
                float width=event.getRawX()-x;
//                    float height=event.getRawY()-y-200+touchedY;
                v.getLayoutParams().width=Math.round(width);
                v.getLayoutParams().height=editText.getHeight()+100;
//                    imageView.getLayoutParams().height=Math.round(height);
//                    imageView.requestLayout();
                v.requestLayout();
//                    v.getLayoutParams().height=imageView.getHeight();
//                    v.requestLayout();
                break;

            case  MotionEvent.ACTION_UP:
                myHorizontalScrollView.setEnableScrolling(true);
                myScrollView.setEnableScrolling(true);
                break;

            default:
                break;


        }
    }

    void ResizeImage(View v, MotionEvent event) {
        myHorizontalScrollView.setEnableScrolling(false);
        myScrollView.setEnableScrolling(false);
        //long startTime = System.currentTimeMillis();
//            relativeLayout=(RelativeLayout)findViewById(v.getId());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = v.getX();
                y = v.getY();
                if (event.getRawX() < (v.getWidth() + v.getX())) {
                    touchedX = (v.getWidth() + v.getX()) - event.getRawX();
                    x = v.getX() - touchedX;
                }

                touchedY = myScrollView.getScrollY();

                break;

            case MotionEvent.ACTION_MOVE:
//                    v.setX((int)(x+(event.getRawX()-touchedX)));
//                    v.setY((int)(y+(event.getRawY()-touchedY)));
                float width = event.getRawX() - x;
                float height = event.getRawY() - y - 200 + touchedY;
                v.getLayoutParams().width = Math.round(width);
                v.getLayoutParams().height = Math.round(height);
//                    imageView.getLayoutParams().height=Math.round(height);
//                    imageView.requestLayout();
                v.requestLayout();
//                    v.getLayoutParams().height=imageView.getHeight();
//                    v.requestLayout();
                break;

            case MotionEvent.ACTION_UP:
                myHorizontalScrollView.setEnableScrolling(true);
                myScrollView.setEnableScrolling(true);
                break;
            default:
                break;

        }
    }

    void SetBold(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
        editText.setSelection(end);
        isBold=false;
    }
    void SetItalic(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
    }
    void SetUnderline(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
    }
    void SetStrike(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
    }
    void SetColor(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new ForegroundColorSpan(Color.parseColor("#0f70f7")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
    }
    void SetBColor(){
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start=editText.getSelectionStart();
        int end=editText.getSelectionEnd();
        texts.setSpan(new BackgroundColorSpan(Color.parseColor("#A600ffb7")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setTextKeepState(texts);
        rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
    }

    void ClearBold() {
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        int s = 0, e = 0, k = 0;
        int[] b = {-10, -10};


        StyleSpan[] spans = texts.getSpans(start, end, StyleSpan.class);
        if (spans.length != 0) {
            for (CharacterStyle span : spans) {
                s = texts.getSpanStart(span);
                e = texts.getSpanEnd(span);

                if (b[0] > s || b[0] == -10)
                    b[0] = s;

                if (b[1] < e || b[0] == -10)
                    b[1] = e;
            }


            for (int i = 0; i < spans.length; i++) {
                if (spans[i].getStyle() == Typeface.BOLD) {
                    texts.removeSpan(spans[i]);
                    k++;
                }

            }

            if (b[0] < start)
                texts.setSpan(new StyleSpan(Typeface.BOLD), b[0], start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (b[1] > end)
                texts.setSpan(new StyleSpan(Typeface.BOLD), end, b[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            editText.setTextKeepState(texts);
            editText.setSelection(end);
            isBold=true;
            rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
        }
    }
    void ClearItalic() {
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        int s = 0, e = 0, k = 0;
        int[] b = {-10, -10};


        StyleSpan[] spans = texts.getSpans(start, end, StyleSpan.class);
        if (spans.length != 0) {
            for (CharacterStyle span : spans) {

                s = texts.getSpanStart(span);
                e = texts.getSpanEnd(span);
                if (b[0] > s || b[0] == -10)
                    b[0] = s;
                if (b[1] < e || b[0] == -10)
                    b[1] = e;

            }

            for (int i = 0; i < spans.length; i++) {
                if (spans[i].getStyle() == Typeface.ITALIC) {
                    texts.removeSpan(spans[i]);
                    k++;
                }

            }
            if (b[0] < start)
                texts.setSpan(new StyleSpan(Typeface.ITALIC), b[0], start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (b[1] > end)
                texts.setSpan(new StyleSpan(Typeface.ITALIC), end, b[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast.makeText(Edittext.this, "Start:" + b[0] + ", end:" + b[1] + ", t=" + k, Toast.LENGTH_SHORT).show();

            editText.setTextKeepState(texts);
            editText.setSelection(end);
            isBold=true;
            rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
        }
    }
    void ClearUnderline() {
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        int s = 0, e = 0, k = 0;
        int[] b = {-10, -10};


        UnderlineSpan[] spans = texts.getSpans(start, end, UnderlineSpan.class);
        if (spans.length != 0) {
            for (CharacterStyle span : spans) {

                s = texts.getSpanStart(span);
                e = texts.getSpanEnd(span);
                if (b[0] > s || b[0] == -10)
                    b[0] = s;
                if (b[1] < e || b[0] == -10)
                    b[1] = e;

            }

            for (int i = 0; i < spans.length; i++) {
                if (spans[i].getSpanTypeId() == 6) {
                    texts.removeSpan(spans[i]);
                    k++;
                }

            }
            if (b[0] < start)
                texts.setSpan(new  UnderlineSpan(), b[0], start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (b[1] > end)
                texts.setSpan(new  UnderlineSpan(), end, b[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast.makeText(Edittext.this, "Start:" + b[0] + ", end:" + b[1] + ", t=" + k, Toast.LENGTH_SHORT).show();

            editText.setTextKeepState(texts);
            editText.setSelection(end);
            isBold=true;
            rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
        }
    }

    void ClearStrike() {
        Spannable texts;
        texts = new SpannableString(editText.getText());
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        int s = 0, e = 0, k = 0;
        int[] b = {-10, -10};


        StrikethroughSpan[] spans = texts.getSpans(start, end, StrikethroughSpan.class);
        if (spans.length != 0) {
            for (CharacterStyle span : spans) {

                s = texts.getSpanStart(span);
                e = texts.getSpanEnd(span);
                if (b[0] > s || b[0] == -10)
                    b[0] = s;
                if (b[1] < e || b[0] == -10)
                    b[1] = e;

            }

            for (int i = 0; i < spans.length; i++) {
                if (spans[i].getSpanTypeId() == 5) {
                    texts.removeSpan(spans[i]);
                    k++;
                }

            }
            if (b[0] < start)
                texts.setSpan(new  StrikethroughSpan(), b[0], start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (b[1] > end)
                texts.setSpan(new  StrikethroughSpan(), end, b[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast.makeText(Edittext.this, "Start:" + b[0] + ", end:" + b[1] + ", t=" + k, Toast.LENGTH_SHORT).show();

            editText.setTextKeepState(texts);
            editText.setSelection(end);
            isBold=true;
            rl.getLayoutParams().height=box.getHeight()+dpToPixel(15f);
        }
    }
    public void showKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public int getCurrentCursorLine(EditText editText)
    {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (!(selectionStart == -1)) {
            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    public int dpToPixel(float dp)  {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edittext_menu, menu);
        return true;
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                drawable = new BitmapDrawable(BitmapFactory
                        .decodeFile(imgDecodableString));

                ImageCreate();


            } else {
            }
        } catch (Exception e) {
            Toast.makeText(this, "Помилка завантаження рисунку", Toast.LENGTH_LONG)
                    .show();
        }
    }

    final SpanWatcher watcher = new SpanWatcher() {
        @Override
        public void onSpanAdded(final Spannable text, final Object what,
                                final int start, final int end) {
            // Nothing here.
        }

        @Override
        public void onSpanRemoved(final Spannable text, final Object what,
                                  final int start, final int end) {
            // Nothing here.
        }

        @Override
        public void onSpanChanged(final Spannable text, final Object what,
                                  final int ostart, final int oend, final int nstart, final int nend) {
            if (what == Selection.SELECTION_START) {
                Log.i("size1","s="+ editText.getSelectionStart());
                Log.i("size1","e="+  editText.getSelectionEnd());
//                texts = new SpannableString(" ");
                isBold=false; isItalic=false; isUnderline=false; isStrike=false;
                ButtonColorChange();
               IsSpan();
                // Selection start changed from ostart to nstart.
            } else if (what == Selection.SELECTION_END) {
                // Selection end changed from ostart to nstart.
            }
        }
    };

    void IsSpan(){

    isBold=false; isItalic=false; isUnderline=false; isStrike=false;
//        SpannableString texts = new SpannableString(editText.getText());
//        StyleSpan[] spans = new SpannableString(editText.getText()).getSpans(editText.getSelectionStart(), editText.getSelectionEnd(), StyleSpan.class);
    int start, end;
    if(editText.getSelectionStart()<editText.getSelectionEnd()){
        if((editText.getSelectionEnd()-editText.getSelectionStart())==1){
        start=editText.getSelectionStart();
        end=editText.getSelectionEnd();
        }
        else {
            start=editText.getSelectionStart();
            end=editText.getSelectionStart()+1;
            if(end>editText.length()){
                start--;
                end--;
            }
        }
    }
    else {
        if((editText.getSelectionStart()-editText.getSelectionEnd())==1){
            end=editText.getSelectionStart();
            start=editText.getSelectionEnd();
        }
        else {
            start=editText.getSelectionStart();
            end=editText.getSelectionStart()+1;
            if(end>editText.length()){
                start--;
                end--;
            }
        }
    }
        StyleSpan[] spans=texts.getSpans(start, end, StyleSpan.class);
                    for (int i = 0; i < spans.length; i++) {
                        if (spans[i].getStyle() == Typeface.BOLD) {
                            Log.i("size1", "bold=" + editText.getSelectionStart());
                            isBold=true;
                        }

                        if (spans[i].getStyle() == Typeface.ITALIC) {
                            Log.i("size1", "italic=" + editText.getSelectionStart());
                            isItalic=true;
                        }
//
                    }

        UnderlineSpan[] underlineSpans=texts.getSpans(start, end, UnderlineSpan.class);
                    for (int i = 0; i < underlineSpans.length; i++) {

                            Log.i("size1", "id=" + underlineSpans[i].getSpanTypeId());
                            Log.i("size1", "underline=" + editText.getSelectionStart());
                        isUnderline=true;
                    }

        StrikethroughSpan[] strikethroughSpans=texts.getSpans(start, end, StrikethroughSpan.class);
                    for (int i = 0; i < strikethroughSpans.length; i++) {

                        Log.i("size1", "id=" + strikethroughSpans[i].getSpanTypeId());
                        Log.i("size1", "strikethroughSpans=" + editText.getSelectionStart());
                        isStrike=true;
                    }
    ButtonColorChange();
    }

    void ButtonColorChange(){

        if(isBold)
        {
            ImageButton button=(ImageButton) findViewById(R.id.bold_button);
            button.setImageResource(R.drawable.ic_bold1);
        }
        else{
            ImageButton button=(ImageButton) findViewById(R.id.bold_button);
            button.setImageResource(R.drawable.ic_bold);
        }
        if(isItalic)
        {
            ImageButton button=(ImageButton) findViewById(R.id.italic_button);
            button.setImageResource(R.drawable.ic_italic1);
        }
        else{
            ImageButton button=(ImageButton) findViewById(R.id.italic_button);
            button.setImageResource(R.drawable.ic_italic);
        }
        if(isUnderline)
        {
            ImageButton button=(ImageButton) findViewById(R.id.underline_button);
            button.setImageResource(R.drawable.ic_underline1);
        }
        else{
            ImageButton button=(ImageButton) findViewById(R.id.underline_button);
            button.setImageResource(R.drawable.ic_underline);
        }
        if(isStrike)
        {
            ImageButton button=(ImageButton) findViewById(R.id.strikethrough_button);
            button.setImageResource(R.drawable.ic_strikethrough1);
        }
        else{
            ImageButton button=(ImageButton) findViewById(R.id.strikethrough_button);
            button.setImageResource(R.drawable.ic_strikethrough);
        }
    }


}
