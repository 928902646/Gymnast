package com.gymnast.view;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnSystemUiVisibilityChangeListener;
import rx.subscriptions.CompositeSubscription;
/**
 * 沉浸式
 */
public class ImmersiveActivity extends AppCompatActivity implements OnSystemUiVisibilityChangeListener {
	public CompositeSubscription mCompositeSubscription;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompositeSubscription = new CompositeSubscription();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
		setStatusBarTransparent();
	}
	private void setStatusBarTransparent(){
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT){
	        //托盘重叠显示在Activity上
	        View decorView = getWindow().getDecorView();	        
	        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 
	        		|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
	        decorView.setSystemUiVisibility(uiOptions);  
	        decorView.setOnSystemUiVisibilityChangeListener(this);
	        // 设置托盘透明
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    	Log.d("CP_Common","VERSION.SDK_INT =" + VERSION.SDK_INT);
		}else{
	    	Log.d("CP_Common", "SDK 小于19不设置状态栏透明效果");
	    }
	}
	public void onSystemUiVisibilityChange(int visibility) {
	}
}
