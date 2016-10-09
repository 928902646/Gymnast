package com.gymnast.view.hotinfoactivity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.adapter.CalendarSearchAdapter;
import com.gymnast.view.live.customview.MyTextView;
import com.gymnast.view.home.HomeActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CalendarActivityActivity extends ImmersiveActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    View myCalendar;
    TableLayout tlCalendar;
    ImageView ivBefore,ivAfter,ivBack;
    TextView tvTimeNow,tvConfirm,tvStartTime,tvEndTime;
    MyTextView[] tvPos;
    private Calendar calendar=Calendar.getInstance(Locale.CHINA);;
    public static final int TYPE_BEFORE=1;
    public static final int TYPE_AFTER=2;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月");
    private int[] yAndM;
    boolean isBegin=false;
    private int maxDays;
    private AlertDialog builder;
    private RecyclerView rvCalendarSearch;
    SwipeRefreshLayout reflesh;
    long pointDaysBefore=0L,pointDaysAfter=0L;
    MyTextView myTV;
    private List<NewActivityItemDevas> activityList=new ArrayList<>();
    ArrayList<Integer> indexOutLeft=new ArrayList<>();
    ArrayList<Integer> indexOutRight=new ArrayList<>();
    CalendarSearchAdapter adapter;
    long startTimeInMillions,endTimeInMillions;
    private static final int HANDLE_NET_DATA=111;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_NET_DATA:
                    adapter=new CalendarSearchAdapter(CalendarActivityActivity.this,activityList);
                    rvCalendarSearch.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    reflesh.setRefreshing(false);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        myCalendar = getLayoutInflater().inflate(R.layout.my_calendar, null);
        builder = new AlertDialog.Builder(this).create();
        builder.setView(myCalendar);
        //去掉dialog四边的黑角
        builder.show();
        builder.getWindow().setBackgroundDrawable(new BitmapDrawable());
        Window window = builder.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int x = rect.width();
        wl.x = 0;
        wl.y = 110;
        wl.width =x*630/750;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        window.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.onWindowAttributesChanged(wl);
        // 设置点击外围消失
        builder.setCanceledOnTouchOutside(true);
        builder.dismiss();
        initViews();
        setBaseDate(calendar);
        addListeners();
    }
    private void addListeners() {
        ivBack.setOnClickListener(this);
        ivBefore.setOnClickListener(this);
        ivAfter.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        reflesh.setOnRefreshListener(this);
        for (int i=0;i<tvPos.length;i++){
            final MyTextView tvNow = tvPos[i];
            final int finalI = i;
            tvNow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                      int []yearMonth=yAndM;
                        clearBackgound(tvPos);
                        tvNow.setIsSelect(true);
                    View view=new View(CalendarActivityActivity.this);
                    String mon=yearMonth[1]+"";
                    int index= finalI;
                    if (isBegin){
                        view=tvStartTime;
                        outer1:
                        for (int j=0;j<indexOutLeft.size();j++){
                            if (indexOutLeft.get(j)==index)  {
                                mon=String.valueOf(Integer.valueOf(mon)-1);
                                break outer1;
                            }
                        }
                        if (tvNow.isOut()){
                            outer2:
                            for (int j=0;j<indexOutRight.size();j++){
                                if (indexOutRight.get(j)==index)  {
                                    mon=String.valueOf(Integer.valueOf(mon)+1);
                                    break outer2;
                                }
                            }
                        }
                    }else {
                        view=tvEndTime;
                        outer2:
                        for (int j=0;j<indexOutRight.size();j++){
                            if (indexOutRight.get(j)==index)  {
                                mon=String.valueOf(Integer.valueOf(mon)+1);
                                break outer2;
                            }
                        }
                        if (tvNow.isOut()){
                            outer1:
                            for (int j=0;j<indexOutLeft.size();j++){
                                if (indexOutLeft.get(j)==index)  {
                                    mon=String.valueOf(Integer.valueOf(mon)-1);
                                    break outer1;
                                }
                            }
                        }
                    }
                    if (Integer.valueOf(mon)<10){
                         mon="0"+mon;
                    }else {
                        mon=mon+"";
                    }
                    String year=String.valueOf(yearMonth[0]);
                    if (mon.equals("13")){
                       mon="01";
                       year=Integer.valueOf(year)+1+"";
                    }
                    if (mon.equals("0")|mon.equals("00")){
                        mon="12";
                        year=Integer.valueOf(year)-1+"";
                    }
                    String day=(tvNow.getText().length()==1)?("0"+tvNow.getText()):tvNow.getText().toString();
                    ((TextView) view).setText(year+"-" +mon+"-" + day);
                    view.invalidate();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            builder.dismiss();
                        }
                    }, 200);
                }
            });
        }
    }
    private int setPosInMyCalendar(Calendar cal,int day){
        int pos=0;
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH)+1;
        String day01=dayOneWeek(year, month, 1);
       int disTime=Integer.valueOf(day01);
        pos=day+disTime-1;
        return pos;
    }
    public static String dayOneWeek(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400+ 1;
        week = week % 7;
        String w = "7123456".substring(week, week + 1);
        return w;
    }
    private void setBaseDate(Calendar cal) {
        String nowYearMonth=sdf.format(cal.getTime());
        tvTimeNow.setText(nowYearMonth);//设置为当前时间的年月数
        //设置当前日期在日历中的显示位置
        int today=cal.get(Calendar.DAY_OF_MONTH);
        int todayPos=setPosInMyCalendar(cal, today);
        myTV=new MyTextView(this,null);
        int indexToday=0;
        outer:
        for (int i=0;i<tvPos.length;i++){
            if (tvPos[i].getPosInCalendar()==todayPos){
                myTV=tvPos[i];
                indexToday=i;//设置循环时跳过的下标
                myTV.setText(today + "");
                tvPos[i].setText(today + "");
                tvPos[i].setIsToday(true);
                tvPos[i].invalidate();
                break outer;
            }
        }
        int todayPosition=Integer.valueOf(myTV.getText().toString());//今天日期数
        //得到上月的最大天数
        yAndM=turnToTime(nowYearMonth);
        int year=yAndM[0];
        int month=yAndM[1];
        boolean isRunNian=checkRunNian(year);
        int lastMonthMaxNum=0;//上月最大天数
        if (month==1|month==2|month==4|month==6|month==9|month==11){
            lastMonthMaxNum=31;
        }else if (month==5|month==7|month==10|month==12){
            lastMonthMaxNum=30;
        }else {
            if (isRunNian){
                lastMonthMaxNum=29;
            }else {
                lastMonthMaxNum=28;
            }
        }
        maxDays=cal.getActualMaximum(Calendar.DAY_OF_MONTH);//本月最大天数
    for (int i=0;i<tvPos.length;i++){
        if (i==indexToday)continue;
        int indexDis=i-indexToday;//下标差值41-8=33
        int newDay=todayPosition+indexDis;//38
        if (indexDis<0){//左边
            if (newDay<=0){
                tvPos[i].setText(lastMonthMaxNum + newDay + "");
                tvPos[i].setIsOut(true);
                tvPos[i].invalidate();
            }else {
                tvPos[i].setText(newDay + "");
                tvPos[i].setIsOut(false);
                tvPos[i].invalidate();
            }
        }else{//右边
            if (newDay>maxDays){
                tvPos[i].setText(newDay - maxDays + "");
                tvPos[i].setIsOut(true);
                tvPos[i].invalidate();
            }else {
                tvPos[i].setText(newDay+"");
                tvPos[i].setIsOut(false);
                tvPos[i].invalidate();
            }
        }
    }
        if (indexOutLeft.size()!=0){
            indexOutLeft.clear();
        }
        if (indexOutRight.size()!=0){
            indexOutRight.clear();
        }
    for (int i=0;i<tvPos.length;i++){
        if (tvPos[i].isOut()){
            if (i<indexToday){
                indexOutLeft.add(i);
            }else {
                indexOutRight.add(i);
            }
        }
    }
    }
    private void initViews() {
        ivBack= (ImageView) findViewById(R.id.ivBack);
        tvConfirm= (TextView) findViewById(R.id.tvConfirm);
        tvStartTime= (TextView) findViewById(R.id.tvStartTime);
        tvEndTime= (TextView)findViewById(R.id.tvEndTime);
        rvCalendarSearch= (RecyclerView)findViewById(R.id.rvCalendarSearch);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCalendarSearch.setLayoutManager(layoutManager);
        reflesh= (SwipeRefreshLayout) findViewById(R.id.srlReflesh);
        RefreshUtil.refresh(reflesh, this);
        reflesh.setRefreshing(false);
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
        String time=sdf2.format(calendar.getTime());
        tvStartTime.setText(time);
        tvEndTime.setText(time);
        tlCalendar= (TableLayout) myCalendar.findViewById(R.id.tlCalendar);
        ivBefore= (ImageView) myCalendar.findViewById(R.id.ivBefore);
        ivAfter= (ImageView) myCalendar.findViewById(R.id.ivAfter);
        tvTimeNow= (TextView) myCalendar.findViewById(R.id.tvTimeNow);
        MyTextView tv1= (MyTextView) myCalendar.findViewById(R.id.tv1);
        MyTextView tv2= (MyTextView) myCalendar.findViewById(R.id.tv2);
        MyTextView  tv3= (MyTextView) myCalendar.findViewById(R.id.tv3);
        MyTextView tv4= (MyTextView) myCalendar.findViewById(R.id.tv4);
        MyTextView tv5= (MyTextView) myCalendar.findViewById(R.id.tv5);
        MyTextView tv6= (MyTextView) myCalendar.findViewById(R.id.tv6);
        MyTextView tv7= (MyTextView) myCalendar.findViewById(R.id.tv7);
        MyTextView tv8= (MyTextView) myCalendar.findViewById(R.id.tv8);
        MyTextView tv9= (MyTextView) myCalendar.findViewById(R.id.tv9);
        MyTextView tv10= (MyTextView) myCalendar.findViewById(R.id.tv10);
        MyTextView tv11= (MyTextView) myCalendar.findViewById(R.id.tv11);
        MyTextView tv12= (MyTextView) myCalendar.findViewById(R.id.tv12);
        MyTextView tv13= (MyTextView) myCalendar.findViewById(R.id.tv13);
        MyTextView tv14= (MyTextView) myCalendar.findViewById(R.id.tv14);
        MyTextView tv15= (MyTextView) myCalendar.findViewById(R.id.tv15);
        MyTextView tv16= (MyTextView) myCalendar.findViewById(R.id.tv16);
        MyTextView tv17= (MyTextView) myCalendar.findViewById(R.id.tv17);
        MyTextView tv18= (MyTextView) myCalendar.findViewById(R.id.tv18);
        MyTextView tv19= (MyTextView) myCalendar.findViewById(R.id.tv19);
        MyTextView  tv20= (MyTextView) myCalendar.findViewById(R.id.tv20);
        MyTextView tv21= (MyTextView) myCalendar.findViewById(R.id.tv21);
        MyTextView tv22= (MyTextView) myCalendar.findViewById(R.id.tv22);
        MyTextView tv23= (MyTextView) myCalendar.findViewById(R.id.tv23);
        MyTextView tv24= (MyTextView) myCalendar.findViewById(R.id.tv24);
        MyTextView tv25= (MyTextView) myCalendar.findViewById(R.id.tv25);
        MyTextView  tv26= (MyTextView) myCalendar.findViewById(R.id.tv26);
        MyTextView tv27= (MyTextView) myCalendar.findViewById(R.id.tv27);
        MyTextView  tv28= (MyTextView) myCalendar.findViewById(R.id.tv28);
        MyTextView tv29= (MyTextView) myCalendar.findViewById(R.id.tv29);
        MyTextView tv30= (MyTextView) myCalendar.findViewById(R.id.tv30);
        MyTextView tv31= (MyTextView) myCalendar.findViewById(R.id.tv31);
        MyTextView tv32= (MyTextView) myCalendar.findViewById(R.id.tv32);
        MyTextView tv33= (MyTextView) myCalendar.findViewById(R.id.tv33);
        MyTextView tv34= (MyTextView) myCalendar.findViewById(R.id.tv34);
        MyTextView tv35= (MyTextView) myCalendar.findViewById(R.id.tv35);
        MyTextView tv36= (MyTextView) myCalendar.findViewById(R.id.tv36);
        MyTextView tv37= (MyTextView) myCalendar.findViewById(R.id.tv37);
        MyTextView tv38= (MyTextView) myCalendar.findViewById(R.id.tv38);
        MyTextView tv39= (MyTextView) myCalendar.findViewById(R.id.tv39);
        MyTextView tv40= (MyTextView) myCalendar.findViewById(R.id.tv40);
        MyTextView tv41= (MyTextView) myCalendar.findViewById(R.id.tv41);
        MyTextView tv42= (MyTextView) myCalendar.findViewById(R.id.tv42);
        tvPos=new MyTextView[]{tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18,tv19,tv20,tv21,tv22,tv23,tv24,tv25,tv26,tv27,tv28,tv29,tv30,tv31,tv32,tv33,tv34,tv35,tv36,tv37,tv38,tv39,tv40,tv41,tv42};
        for (int i=0;i<tvPos.length;i++){
            tvPos[i].setPosInCalendar(i+1);
        }
    }
    private void clearAll(MyTextView[] myTextViews){
        for (int i=0;i<myTextViews.length;i++){
            myTextViews[i].setIsOut(false);
            myTextViews[i].setIsToday(false);
            myTextViews[i].setIsSelect(false);
            myTextViews[i].invalidate();
        }
    }
    private void clearBackgound(MyTextView[] myTextViews){
        for (int i=0;i<myTextViews.length;i++){
            myTextViews[i].setIsSelect(false);
            myTextViews[i].invalidate();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.ivBack: onBackPressed();break;
           case R.id.ivBefore:
               turnToBefore();
               break;
           case R.id.ivAfter:
               turnToAfter();
               break;
           case R.id.tvConfirm:
               boolean isSetCorrect=CheckTrue();
               if (isSetCorrect){
                   Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show();
                   sendToNetToGetData();
               }else {
                   Toast.makeText(this,"设置失败，请检查时间设置！",Toast.LENGTH_SHORT).show();
               }
               break;
           case R.id.tvStartTime:
               isBegin=true;
               builder.show();
               break;
           case R.id.tvEndTime:
               isBegin=false;
               builder.show();
               break;
       }
    }
    private void sendToNetToGetData() {
        new Thread(){
            @Override
            public void run() {
                try{
                    if (activityList.size() != 0) {
                        activityList.clear();
                    }
                   SharedPreferences share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    String userId = share.getString("UserId", "");//当
                    String uri= API.BASE_URL+"/v1/activity/query";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("startTime",startTimeInMillions+"");
                    params.put("endTime",endTimeInMillions+"");
                    params.put("createId",userId+"");
                    params.put("pageNumber",100+"");
                    params.put("page",1+"");
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("state")==200) {
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            JSONObject objectUser = object.getJSONObject("user");
                            int activeID=object.getInt("id");
                            String nickName = objectUser.getString("nickname");
                            //收藏人数
                            int return_collection = object.getInt("collection");
                            int isCollectedTmp=object.getInt("isCollection");
                            boolean isCollected=(isCollectedTmp==1)?true:false;
                            String return_title = object.getString("title");
                            String return_descContent = object.getString("descContent");
                            String return_address = object.getString("address");
                            String return_maxPeople = object.getString("maxPeople");
                            String return_phone = object.getString("phone");
                            String return_targetUrl = object.getString("targetUrl");
                            String return_memberTemplate = object.getString("memberTemplate");
                            int return_price = object.getInt("price");
                            int return_visible = object.getInt("visible");
                            int return_type = object.getInt("type");
                            // return_activityType= object.getString("activityType");
                            int return_memberCount = object.getInt("memberCount");
                            int return_examine = object.getInt("examine");
                            long return_startTime = object.getLong("startTime");
                            long return_endTime = object.getLong("endTime");
                            long return_lastTime = object.getLong("lastTime");
                            String imgUrls = API.IMAGE_URL + URI.create(object.getString("imgUrls")).getPath();
                            NewActivityItemDevas itemDevas = new NewActivityItemDevas();
                            itemDevas.setIsCollected(isCollected);
                            itemDevas.setActiveId(activeID);
                            itemDevas.setNickname(nickName);
                            itemDevas.setCollection(return_collection);
                            itemDevas.setTitle(return_title);
                            itemDevas.setAddress(return_address);
                            itemDevas.setPrice(return_price);
                            itemDevas.setStartTime(return_startTime);
                            itemDevas.setImgUrls(imgUrls);
                            itemDevas.setTargetUrl(return_targetUrl);
                            itemDevas.setMemberTemplate(return_memberTemplate);
                            itemDevas.setEndTime(return_endTime);
                            itemDevas.setLastTime(return_lastTime);
                            // itemDevas.setActivityType(return_activityType);
                            itemDevas.setMaxPeople(return_maxPeople);
                            itemDevas.setDescContent(return_descContent);
                            itemDevas.setPhone(return_phone);
                            itemDevas.setVisible(return_visible);
                            itemDevas.setType(return_type);
                            itemDevas.setMemberCount(return_memberCount);
                            itemDevas.setExamine(return_examine);
                            activityList.add(itemDevas);
                        }
                    }
                    handler.sendEmptyMessage(HANDLE_NET_DATA);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private boolean CheckTrue() {
        boolean isCorrect=false;
        String start=tvStartTime.getText().toString();
        int []yearMonthDayStart=turnToTime(start);
        String end=tvEndTime.getText().toString();
        int []yearMonthDayEnd=turnToTime(end);
        Calendar c1=Calendar.getInstance();
        c1.set(yearMonthDayStart[0],yearMonthDayStart[1]-1,yearMonthDayStart[2],0,0,0);
        Calendar c2=Calendar.getInstance();
        c2.set(yearMonthDayEnd[0],yearMonthDayEnd[1]-1,yearMonthDayEnd[2],0,0,0);
        startTimeInMillions=c1.getTimeInMillis();
        endTimeInMillions=c2.getTimeInMillis();
        if (c1.getTimeInMillis()+5000<c2.getTimeInMillis()){
            isCorrect=true;
        }
        return isCorrect;
    }
    private int[] turnToTime(String time) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(time);
        String num = m.replaceAll("").trim().toString();
        String year=num.substring(0, 4);
        String month;
        int[] yearAndMonth;
        int y=Integer.valueOf(year);
        if (num.length()<=6){
            if (num.length()==6){
                month=num.substring(4, 6);
            }else {
                month=num.substring(4, 5);
            }
            int mon=Integer.valueOf(month);
            yearAndMonth=new int[]{y,mon};
        }else {
            String day;
            if (num.length()==6){
                month=num.substring(4, 6);
                day=num.substring(6,num.length());
            }else {
                month=num.substring(4, 6);
                day=num.substring(6,num.length());
            }
            int mon=Integer.valueOf(month);
            int d=Integer.valueOf(day);
            yearAndMonth=new int[]{y,mon,d};
        }
        return yearAndMonth;
    }
    private void turnToBefore() {
        clearAll(tvPos);
        int disDay=disDayFromNow(TYPE_BEFORE,yAndM[0],yAndM[1]);
        long millions=System.currentTimeMillis()-(disDay+pointDaysBefore)*24L*60L*60L*1000L;
        pointDaysBefore+=disDay;
        pointDaysAfter-=disDay;
        Calendar calBefore=Calendar.getInstance();
        calBefore.setTimeInMillis(millions);
        setBaseDate(calBefore);
    }
    private int disDayFromNow(int type, int yearNum, int monthNum) {
        int distanceDays=0;
        boolean isRunNian=checkRunNian(yearNum);
        if (type==TYPE_BEFORE){
            if (monthNum==2||monthNum==4||monthNum==6||monthNum==8||monthNum==9||monthNum==11||monthNum==1){
                distanceDays=31;
            }else if (monthNum==5||monthNum==7||monthNum==10||monthNum==12){
                distanceDays=30;
            }else {
                if (isRunNian){
                    distanceDays=29;
                }else {
                    distanceDays=28;
                }
            }
        }
        if (type==TYPE_AFTER){
            if (monthNum==1||monthNum==3||monthNum==5||monthNum==7||monthNum==8||monthNum==10||monthNum==12){
                distanceDays=31;
            }else if (monthNum==4||monthNum==6||monthNum==9||monthNum==11){
                distanceDays=30;
            }else {
                if (isRunNian){
                    distanceDays=29;
                }else {
                    distanceDays=28;
                }
            }
        }
        return distanceDays;
    }
    private boolean checkRunNian(int yearNum) {
        boolean isRunNian;
        if (yearNum%400==0){
            isRunNian=true;
        }else if (yearNum%100!=0&yearNum%4==0){
            isRunNian=true;
        }else {
            isRunNian=false;
        }
       return isRunNian;
    }
    private void turnToAfter() {
        clearAll(tvPos);
        int disDay=disDayFromNow(TYPE_AFTER,yAndM[0],yAndM[1]);
        long millions=System.currentTimeMillis()+(disDay+pointDaysAfter)*24L*60L*60L*1000L;
        pointDaysAfter+=disDay;
        pointDaysBefore-=disDay;
        Calendar calAfter=Calendar.getInstance();
        calAfter.setTimeInMillis(millions);
        setBaseDate(calAfter);
    }
    @Override
    public void onRefresh() {
        sendToNetToGetData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 1000);
    }
}
