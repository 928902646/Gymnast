package com.gymnast.view.personal.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gymnast.R;

/**
 * Created by zzqybyb19860112 on 2016/9/21.
 */
public class MyTextWatcher implements TextWatcher {
    TextView tvSendTieZi;
    ImageView ivSendTiezi;
    EditText etCallBackTieZi;
    public MyTextWatcher(TextView tvSendTieZi, ImageView ivSendTiezi,EditText etCallBackTieZi) {
        this.tvSendTieZi = tvSendTieZi;
        this.ivSendTiezi = ivSendTiezi;
        this.etCallBackTieZi = etCallBackTieZi;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void afterTextChanged(Editable editable) {
        if (!editable.toString().equals("")){
            tvSendTieZi.setVisibility(View.VISIBLE);
            ivSendTiezi.setVisibility(View.GONE);
        }else {
            tvSendTieZi.setVisibility(View.GONE);
            ivSendTiezi.setVisibility(View.VISIBLE);
        }
    }
}
