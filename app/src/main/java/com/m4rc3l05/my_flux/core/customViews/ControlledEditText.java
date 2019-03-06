package com.m4rc3l05.my_flux.core.customViews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class ControlledEditText extends android.support.v7.widget.AppCompatEditText {
    public interface IControlledInputTextChange {
        void call(String s);
    }

    public ControlledEditText(Context context) {
        super(context);
    }

    public ControlledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlledEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onControlledInputTextChange(IControlledInputTextChange cb) {

        this.addTextChangedListener(new TextWatcher() {
            private int prevPos;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (getTag() == "@@Controlled@@") {
                    prevPos = getSelectionStart();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getTag() != "@@Controlled@@") {
                    cb.call(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getTag() == "@@Controlled@@") {
                    setSelection(prevPos <= 0 ? 0 : prevPos >= s.length() ? s.length() : prevPos);
                }
            }
        });
    }

    public void setControlledText(String s) {
        setTag("@@Controlled@@");
        this.setText(s);
        setTag(null);
    }
}
