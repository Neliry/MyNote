package com.neliry.db_sql;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Neliry on 14.04.2018.
 */

public class TextBoxManager  {

    RelativeLayout textbox,  borderBox;
    static RelativeLayout controlButtonBox;
    float x, y, touchedX, touchedY;
    EditText editText;
    EditorScreen editorScreen;
    Context context;
    int selectionStart, selectionEnd;
    private static WeakReference<Activity> mActivityRef;
    String g="l";
    Boolean isBold=false, isItalic=false, isUnderline=false, isStrikethrough=false;

    public static void updateAktivity(Activity activity){
        mActivityRef=new WeakReference<Activity>(activity);
    }
    public TextBoxManager(Context context){
        this.context=context;
        editorScreen=(EditorScreen) mActivityRef.get();
    }
    void CreateTextBox(int width){
        textbox=new RelativeLayout(context);
        RelativeLayout.LayoutParams params0 =
                new RelativeLayout.LayoutParams(width,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params0.setMargins(0, editorScreen.dpToPixel(10), 0, 0);
        textbox.setLayoutParams(params0);
        textbox.setPadding(editorScreen.dpToPixel(26),editorScreen.dpToPixel(24),editorScreen.dpToPixel(26),editorScreen.dpToPixel(26));
        textbox.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                textbox.post(new Runnable() {
                    @Override
                    public void run() {
                        controlButtonBox.getLayoutParams().height=textbox.getHeight();
                        controlButtonBox.getLayoutParams().width=textbox.getWidth();
                        controlButtonBox.requestLayout();
                        UpdateTextBox();
                    }
                });

            }
        });
        editText=new EditText (context);
        RelativeLayout.LayoutParams params1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0,0,0, 0);
        editText.setPadding(0,0,0,0);
        editText.setTag("edittext");
        editText.setLayoutParams(params1);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        editText.setBackground(null);
        editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent=(View) view.getParent();
                if(textbox!=parent){
                    ClearFocus();
                }
                if(controlButtonBox.getVisibility()==View.GONE){
                view.setFocusableInTouchMode(true);
                textbox=(RelativeLayout) parent;
                view.requestFocus();
                editorScreen.showKeyboard(view);
                editText=(EditText)view;
                SetFocus();}

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            }
            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                editText.getText().setSpan(watcher, 0, editText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateText();
                    }
                }).start();
            }
        });
        textbox.addView(editText);
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

                ViweMove(textbox, motionEvent);
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
                ResizeEdittext(textbox, motionEvent);
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
                RemoveEdittext();
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

    void SetFocus()
    {
        if(editorScreen.imageBoxManager.imagebox!=null)
            editorScreen.imageBoxManager.ClearFocus();
        if(editorScreen.videoBoxManager.videobox!=null)
            editorScreen.videoBoxManager.ClearFocus();
        if(editorScreen.audioBoxManager.controlButtonBox.getVisibility()==View.VISIBLE&&editorScreen.audioBoxManager.audiobox!=null)
            editorScreen.audioBoxManager.ClearFocus();
        editorScreen.bottomTollBar.setVisibility(View.GONE);
        editorScreen.textEditBar.setVisibility(View.VISIBLE);
        controlButtonBox.setVisibility(View.VISIBLE);
        controlButtonBox.getLayoutParams().height=textbox.getHeight();
        controlButtonBox.getLayoutParams().width=textbox.getWidth();
        controlButtonBox.requestLayout();
        RelativeLayout.LayoutParams p1=(RelativeLayout.LayoutParams) controlButtonBox.getLayoutParams();
        p1.setMargins((int)textbox.getX(), (int)textbox.getY(), 0, 0);
        controlButtonBox.setLayoutParams(p1);
        controlButtonBox.bringToFront();

    }

    void ClearFocus()
    {
        editorScreen.bottomTollBar.setVisibility(View.VISIBLE);
        editorScreen.textEditBar.setVisibility(View.GONE);
        controlButtonBox.setVisibility(View.GONE);
        editText.clearFocus();
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        UpdateText();
        editorScreen.hideKeyboard(editText);
        if (editText.getText().length() == 0) {
            editorScreen.text_canvas.removeView(textbox);
        }
    }


    void ViweMove(View v, MotionEvent event){
        editorScreen.SetScroll(false);
        editorScreen.hideKeyboard(editText);
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
                UpdateTextBoxСoordinates();
                break;

            default:
                break;
        }
    }

    void ResizeEdittext(View v, MotionEvent event){

        editorScreen.SetScroll(false);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x= v.getX();
                y= v.getY();
                if(event.getRawX()<(v.getWidth()+v.getX())){
                    touchedX=(v.getWidth()+v.getX())-event.getRawX();
                    x= v.getX()-touchedX;
                }
                touchedY=editorScreen.myScrollView.getScrollY();
                break;
            case  MotionEvent.ACTION_MOVE:
                float width=event.getRawX()-x;
                v.getLayoutParams().width=Math.round(width);

                v.requestLayout();
                break;

            case  MotionEvent.ACTION_UP:
                editorScreen.SetScroll(true);
                UpdateTextBox();
                break;

            default:
                break;


        }
    }

    void RemoveEdittext(){
        if(textbox!=null) {
            RemoveTextbox();
            ClearFocus();
            editorScreen.text_canvas.removeView(textbox);
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
                selectionStart=nstart;
                // Selection start changed from ostart to nstart.
            } else if (what == Selection.SELECTION_END) {
                selectionEnd=nstart;
                // Selection end changed from ostart to nstart.
            }
            textbox.post(new Runnable() {
                @Override
                public void run() {
                    SelectionCheck();

                }
            });


        }
    };

    void SelectionCheck(){

        int start=selectionStart;
        int end=selectionEnd;
        if(start==end){
            if(start>0){
                start--;
            }
        }
        isBold=false;
        isItalic=false;
        isUnderline=false;
        isStrikethrough=false;

        Editable edit = editText.getEditableText();
        StyleSpan[] styleSpans = edit.getSpans(start,
                end, StyleSpan.class);
        for (CharacterStyle span : styleSpans) {
            if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.BOLD)
                isBold=true;
            if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.ITALIC)
                isItalic=true;
        }

        StrikethroughSpan[] strikethroughSpan = edit.getSpans(start,
                end, StrikethroughSpan.class);
        for (CharacterStyle span : strikethroughSpan) {
            if (span instanceof StrikethroughSpan)
                isStrikethrough=true;
        }
        ButtonColorChange();

    }

    void ButtonColorChange(){

        if(isBold)
        {
            editorScreen.bold_button.setImageResource(R.drawable.ic_bold1);
        }
        else{
            editorScreen.bold_button.setImageResource(R.drawable.ic_bold);
        }

        if(isItalic)
        {
            editorScreen.italic_button.setImageResource(R.drawable.ic_italic1);
        }
        else{
            editorScreen.italic_button.setImageResource(R.drawable.ic_italic);
        }
        if(isUnderline)
        {
            editorScreen.underline_button.setImageResource(R.drawable.ic_underline1);
        }
        else{
            editorScreen.underline_button.setImageResource(R.drawable.ic_underline);
        }
        if(isStrikethrough)
        {
            editorScreen.strikethrough_button.setImageResource(R.drawable.ic_strikethrough1);
        }
        else{
            editorScreen.strikethrough_button.setImageResource(R.drawable.ic_strikethrough);
        }

    }


    void RemoveSpan(StyleSpan[]styleSpans,  int s ,int e, byte t)
    {
        int s1=s, e1=e;

        switch (t) {
            case 2:
                if(s==e)
                    return;
                if (styleSpans.length != 0) {
                    for (CharacterStyle span : styleSpans) {
                        if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.ITALIC) {

                            if (s > new SpannableString(editText.getText()).getSpanStart(span))
                                s=new SpannableString(editText.getText()).getSpanStart(span);

                            if (e < new SpannableString(editText.getText()).getSpanEnd(span))
                                e = new SpannableString(editText.getText()).getSpanEnd(span);

                            editText.getEditableText().removeSpan(span);


                        }

                    }
                }

                if(s1!=s)
                    editText.getEditableText().setSpan(new StyleSpan(Typeface.ITALIC),
                            s,
                            s1,
                            Typeface.ITALIC);
                if(e1!=e)
                    editText.getEditableText().setSpan(new StyleSpan(Typeface.ITALIC),
                            e1,
                            e,
                            Typeface.ITALIC);
                break;
            case 1:
                if (styleSpans.length != 0) {
                    for (CharacterStyle span : styleSpans) {
                        if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.BOLD) {

                            if (s > new SpannableString(editText.getText()).getSpanStart(span))
                                s=new SpannableString(editText.getText()).getSpanStart(span);

                            if (e < new SpannableString(editText.getText()).getSpanEnd(span))
                                e = new SpannableString(editText.getText()).getSpanEnd(span);

//                            Log.d("myLog3", "s1 = " + s1);
//                            Log.d("myLog3", "s = " + s);
//                            Log.d("myLog3", "e1 = " + e1);
//                            Log.d("myLog3", "e = " + e);
                                editText.getEditableText().removeSpan(span);
                        }
                    }
                }


                if(s1!=s)
                    editText.getEditableText().setSpan(new StyleSpan(Typeface.BOLD),
                            s,
                            s1,
                            Typeface.BOLD);

                if(e1!=e)
                    editText.getEditableText().setSpan(new StyleSpan(Typeface.BOLD),
                            e1,
                            e,
                            Typeface.BOLD);
                break;
        }

    }

    void RemoveSpan(UnderlineSpan[]styleSpans, int s, int e)
    {
        int s1=s, e1=e;

        if (styleSpans.length != 0) {
            for (CharacterStyle span : styleSpans) {
                if (span instanceof UnderlineSpan) {
                    if (s > new SpannableString(editText.getText()).getSpanStart(span))
                        s=new SpannableString(editText.getText()).getSpanStart(span);

                    if (e < new SpannableString(editText.getText()).getSpanEnd(span))
                        e = new SpannableString(editText.getText()).getSpanEnd(span);

                    editText.getEditableText().removeSpan(span);
                }
            }
        }
        if(s1!=s)
        editText.getEditableText().setSpan(new UnderlineSpan(),
                s,
                s1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if(e1!=e)
            editText.getEditableText().setSpan(new UnderlineSpan(),
                    e1,
                    e,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
    void RemoveStrikethroughSpan(StrikethroughSpan []styleSpans, int s, int e)
    {
        int s1=s, e1=e;

        if (styleSpans.length != 0) {
            for (CharacterStyle span : styleSpans) {
                if (span instanceof StrikethroughSpan) {
                    if (s > new SpannableString(editText.getText()).getSpanStart(span))
                        s=new SpannableString(editText.getText()).getSpanStart(span);

                    if (e < new SpannableString(editText.getText()).getSpanEnd(span))
                        e = new SpannableString(editText.getText()).getSpanEnd(span);

                    editText.getEditableText().removeSpan(span);
                }
            }
        }
        if(s1!=s)
            editText.getEditableText().setSpan(new UnderlineSpan(),
                    s,
                    s1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if(e1!=e)
            editText.getEditableText().setSpan(new UnderlineSpan(),
                    e1,
                    e,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
    void CreateNew(){
        long lastId;
        lastId=editorScreen.dbHelper.createView(editorScreen.parentPageId, "left", "",textbox.getHeight(), textbox.getWidth(),(int)textbox.getX(), (int)textbox.getY(),"text");
        textbox.setId((int)lastId);
        Log.i("lastId","textbox id (CreateNew)="+  textbox.getId());
        editorScreen.dbHelper.close();
    }

    void UpdateTextBoxСoordinates(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_X, textbox.getX() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(textbox.getId())});
        contentValues.put(DBHelper.KEY_Y, textbox.getY() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(textbox.getId())});
    }

    void UpdateTextBox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_WIDTH, textbox.getWidth() );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(textbox.getId())});
    }

    void UpdateText(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String text= Html.toHtml(editText.getText());
        contentValues.put(DBHelper.KEY_CONTENT, text );
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(textbox.getId())});

    }

    void UpdateGravity(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE, g);
        database[0].update(DBHelper.TABLE_VIEWS, contentValues, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(textbox.getId())});
    }
    void RemoveTextbox(){
        SQLiteDatabase[] database = new SQLiteDatabase[1];
        database[0] = editorScreen.dbHelper.getWritableDatabase();
        database[0].delete(DBHelper.TABLE_VIEWS, DBHelper.KEY_ID + "= ?" , new String[] {String.valueOf(textbox.getId())});
    }
}
