package com.gymnast.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class InputWatcherUtil implements TextWatcher {
    private Button mBtnClear;
    private EditText mEtContainer ;
    private TextView tvConfirm ;
    private TextView tvCancel ;
    /**
     *
     * @param btnClear 清空按钮 可以是button的子类
     * @param etContainer edittext
     */
    public InputWatcherUtil(Button btnClear, EditText etContainer, TextView tvConfirm, TextView tvCancel) {
        if (btnClear == null || etContainer == null||tvConfirm==null||tvCancel==null) {
            throw new IllegalArgumentException("请确保btnClear和etContainer不为空");
        }
        this.mBtnClear = btnClear;
        this.mEtContainer = etContainer;
        this.tvConfirm=tvConfirm;
        this.tvCancel=tvCancel;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s) && !s.toString().trim().equals("")) {
            if (mBtnClear != null) {
                mBtnClear.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.GONE);
                mBtnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEtContainer != null) {
                            mEtContainer.setText("");
                            mBtnClear.setVisibility(View.GONE);
                            tvConfirm.setVisibility(View.GONE);
                            tvCancel.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        } else {
            if (mBtnClear != null) {
                mBtnClear.setVisibility(View.GONE);
                tvConfirm.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
    }
}
