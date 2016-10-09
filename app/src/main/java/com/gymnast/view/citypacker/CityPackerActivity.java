package com.gymnast.view.citypacker;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CityPackerActivity extends CityPackerBaseActivity implements OnClickListener, OnWheelChangedListener {
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm;
	private SharedPreferences share;
	private String token;
	private String id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_citypicker);
		share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		token = share.getString("Token","");
		id = share.getString("UserId","");
		setUpViews();
		setUpListener();
		setUpData();
	}
	private void setUpViews() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
		mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
	}
	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
		// 添加onclick事件
		mBtnConfirm.setOnClickListener(this);
	}
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(CityPackerActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}
	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);
		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		//mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
	}
	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_confirm:
				showSelectedResult();
				break;
			default:
				break;
		}
	}
	private void showSelectedResult() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String uri= API.BASE_URL+"/v1/account/info/edit";
					Map<String, String> params=new HashMap<>();
					params.put("token",token);
					params.put("account_id",id);
					if(mCurrentProviceName.equals(mCurrentCityName)){
						String	CityName=(mCurrentCityName+" "+mCurrentDistrictName).toString();
						params.put("address",CityName);
					}else {
						String	ProviceName =	(mCurrentProviceName+" "+mCurrentCityName+" "+mCurrentDistrictName).toString();
						params.put("address",ProviceName);
					}
					String result= PostUtil.sendPostMessage(uri,params);
					JSONObject json =  new JSONObject(result);

					if(json.getInt("state")==200){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								finish();
								Toast.makeText(CityPackerActivity.this,"地址修改成功",Toast.LENGTH_SHORT).show();
							}
						});
					}else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								finish();
								Toast.makeText(CityPackerActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
