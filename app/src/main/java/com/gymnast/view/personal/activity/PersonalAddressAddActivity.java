package com.gymnast.view.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by Cymbi on 2016/8/18.
 */
public class PersonalAddressAddActivity extends ImmersiveActivity implements View.OnClickListener{
    private RelativeLayout name,rlAddPhone,rlAddAddress,rlAddStreet;
    private EditText etAddDetailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address_edit);
        setview();
    }
    private void setview() {
        name=(RelativeLayout)findViewById(R.id.name);
        rlAddPhone=(RelativeLayout)findViewById(R.id.rlAddPhone);
        rlAddAddress=(RelativeLayout)findViewById(R.id.rlAddAddress);
        rlAddStreet=(RelativeLayout)findViewById(R.id.rlAddStreet);
        etAddDetailAddress=(EditText)findViewById(R.id.etAddDetailAddress);
        name.setOnClickListener(this);
        rlAddPhone.setOnClickListener(this);
        rlAddAddress.setOnClickListener(this);
        rlAddStreet.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
    }
}
