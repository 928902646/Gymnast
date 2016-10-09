package com.gymnast.view.user;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import butterknife.BindView;
import com.gymnast.R;
import com.gymnast.view.BaseToolbarActivity;
import com.gymnast.view.ImmersiveActivity;

public class RegisterProtocolActivity extends ImmersiveActivity {
  WebView registerProtocol;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_protocol);
    registerProtocol =(WebView) findViewById(R.id.register_protocol);
    ImageView personal_back=(ImageView)findViewById(R.id.personal_back);
    personal_back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    registerProtocol.loadUrl("file:///android_asset/protocol.html");
  }
  }