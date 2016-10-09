package com.gymnast.view.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.utils.InputWatcherUtil;
import com.gymnast.view.home.HomeActivity;

public class HomeSearchActivity extends ImmersiveActivity implements View.OnClickListener{
    EditText etSearch;
    TextView tvConfirm,tvCancel;
    Button btnCancel,btnNBA,btnCBA,btnWTA,btnWNBA,btnStar,btnBall,btnGame,btnTieZi;
    int type=9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);
        setViews();
        setListeners();
    }
    private void setListeners() {
        etSearch.addTextChangedListener(new InputWatcherUtil(btnCancel,etSearch,tvConfirm,tvCancel));
        btnCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        btnNBA.setOnClickListener(this);
        btnCBA.setOnClickListener(this);
        btnWTA.setOnClickListener(this);
        btnWNBA.setOnClickListener(this);
        btnStar.setOnClickListener(this);
        btnBall.setOnClickListener(this);
        btnGame.setOnClickListener(this);
        btnTieZi.setOnClickListener(this);
        final Button [] buttons=new Button[]{btnNBA,btnCBA,btnWTA,btnWNBA,btnStar,btnBall,btnGame,btnTieZi};
        for (int i=0;i<buttons.length;i++){
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttons[finalI].setBackgroundResource(R.drawable.border_radius_cornner_selected);
                    type=finalI;
                    Intent intent=new Intent(HomeSearchActivity.this,HomeSearchResultAcitivity.class);
                    intent.putExtra("searchText",etSearch.getText().toString());
                    intent.putExtra("type", type);
                    HomeSearchActivity.this.startActivity(intent);
                    int j=finalI;
                    for ( int k=0;k<buttons.length;k++){
                        if (k!=j){
                            buttons[k].setBackgroundResource(R.drawable.border_radius_cornner_black);
                        }
                    }
                }
            });
        }
    }
    private void setViews() {
        etSearch= (EditText) findViewById(R.id.etSearch);
        btnCancel= (Button) findViewById(R.id.btnCancel);
        tvConfirm= (TextView) findViewById(R.id.tvConfirm);
        tvCancel= (TextView) findViewById(R.id.tvCancel);
        btnNBA= (Button) findViewById(R.id.btnNBA);
        btnCBA= (Button) findViewById(R.id.btnCBA);
        btnWTA= (Button) findViewById(R.id.btnWTA);
        btnWNBA= (Button) findViewById(R.id.btnWNBA);
        btnStar= (Button) findViewById(R.id.btnStar);
        btnBall= (Button) findViewById(R.id.btnBall);
        btnGame= (Button) findViewById(R.id.btnGame);
        btnTieZi= (Button) findViewById(R.id.btnTieZi);
    }
    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btnCancel:
               etSearch.setText("");
               btnCancel.setVisibility(View.GONE);
               tvConfirm.setVisibility(View.GONE);
               tvCancel.setVisibility(View.VISIBLE);
               break;
           case R.id.tvConfirm:
               Intent intent=new Intent(HomeSearchActivity.this,HomeSearchResultAcitivity.class);
               intent.putExtra("searchText",etSearch.getText().toString());
               intent.putExtra("type",type);
               HomeSearchActivity.this.startActivity(intent);
               HomeSearchActivity.this.finish();
               break;
           case R.id.tvCancel:
               Intent intent1=new Intent(HomeSearchActivity.this,HomeActivity.class);
               HomeSearchActivity.this.startActivity(intent1);
               HomeSearchActivity.this.finish();
               break;
       }
    }
}
