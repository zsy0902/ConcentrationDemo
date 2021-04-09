package com.example.concentrationdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Looper;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> viewList=new ArrayList<>();
    private ViewPager viewPager;
    Chronometer chronometer;
    private Button start;
    private Button stop;
    private ImageView next_mode;
    private ImageView last_mode;
    private long startTime;
    int second;
    //倒计时
    private String hourString = "0";
    private String minString = "0";
    private int leftTime;//剩余秒数
    private final List<DataBean> hourBeanList = new ArrayList<>();
    private final List<DataBean> minBeanList = new ArrayList<>();
    private TextView text;
    private View picker;
    private boolean isCounting=false;


    Handler handler = new Handler(Looper.myLooper());

    Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            leftTime--;
            if (leftTime > 0) {
                //倒计时效果展示
                String formatLongToTimeStr = formatLongToTimeStr(leftTime);
                text.setText(formatLongToTimeStr);
                //每一秒执行一次
                handler.postDelayed(this, 1000);
            } else {
                //倒计时结束,处理逻辑
                leftTime = 0;
                text.setVisibility(View.GONE);
                picker.setVisibility(View.VISIBLE);

                /**
                 * 倒计时结束的弹窗
                 */
                stop2Dialog();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.star_vp);
        chronometer = findViewById(R.id.chronometer);
        start = findViewById(R.id.start_button);
        stop = findViewById(R.id.stop_button);
        setListener();

        View v1 = getLayoutInflater().inflate(R.layout.star_vp_item1, null, false);
        View v2 =getLayoutInflater().inflate(R.layout.star_vp_item2,null,false);
        viewList.add(v1);
        viewList.add(v2);
        VpAdapter vpAdapter=new VpAdapter(); //星球滑动
        vpAdapter.setViewList(viewList);
        viewPager.setPageTransformer(false, new Transform()); //匿名对象
        viewPager.setAdapter(vpAdapter);
        next_mode=v1.findViewById(R.id.next_mode);
        last_mode=v2.findViewById(R.id.last_mode);

        //提示图片点击切换模式
        next_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.arrowScroll(View.FOCUS_RIGHT);
            }
        });
        last_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });
        initDataList();
//
//
        PickerScrollView hourPicker = findViewById(R.id.hour_picker);
        PickerScrollView minPicker = findViewById(R.id.min_picker);
        Button btn = (Button) findViewById(R.id.btn);
        text = findViewById(R.id.text);
        picker = findViewById(R.id.picker);

        //设置数据，默认选择第一条
        hourPicker.setData(hourBeanList);
        hourPicker.setSelected(0);
        //滚动监听
        hourPicker.setOnSelectListener(pickers -> hourString = pickers.getCategoryName());


        //设置数据，默认选择第一条
        minPicker.setData(minBeanList);
        minPicker.setSelected(0);
        //滚动监听
        minPicker.setOnSelectListener(pickers -> minString = pickers.getCategoryName());


        //确认按钮
        btn.setOnClickListener(v -> {
            int hour = Integer.parseInt(hourString);
//            int min = Integer.parseInt(minString);
            float min = Float.parseFloat(minString);
            if(hour == 0 && min == 0){
                Toast.makeText(MainActivity.this,"尚未选择时间",Toast.LENGTH_SHORT).show();
            }else {
//                leftTime = (hour * 60 + min) * 60;
                leftTime = (int)((hour * 60 + min) * 60);
                handler.postDelayed(update_thread, 1000);
                String s = formatLongToTimeStr(leftTime);
                text.setText(s);
                picker.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //正倒计时可见设置
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    findViewById(R.id.count_up).setVisibility(View.VISIBLE);
                    findViewById(R.id.count_down).setVisibility(View.GONE);
                    findViewById(R.id.t_count_up).setVisibility(View.VISIBLE);
                    findViewById(R.id.t_count_down).setVisibility(View.INVISIBLE);
                    findViewById(R.id.next_mode).setVisibility(View.VISIBLE);
                    findViewById(R.id.last_mode).setVisibility(View.GONE);
                } else if (position == 1) {
                    findViewById(R.id.count_down).setVisibility(View.VISIBLE);
                    findViewById(R.id.count_up).setVisibility(View.GONE);
                    findViewById(R.id.t_count_up).setVisibility(View.INVISIBLE);
                    findViewById(R.id.t_count_down).setVisibility(View.VISIBLE);
                    findViewById(R.id.last_mode).setVisibility(View.VISIBLE);
                    findViewById(R.id.next_mode).setVisibility(View.GONE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state){

            }
        });

    }
    private void initDataList() {
        DataBean hourBean0 = new DataBean("0","0","0");
        DataBean hourBean1 = new DataBean("1","1","0");
        DataBean hourBean2 = new DataBean("2","2","0");
        DataBean hourBean3 = new DataBean("3","3","0");
        DataBean hourBean4 = new DataBean("4","4","0");
        hourBeanList.add(hourBean0);
        hourBeanList.add(hourBean1);
        hourBeanList.add(hourBean2);
        hourBeanList.add(hourBean3);
        hourBeanList.add(hourBean4);

        DataBean minBean0 = new DataBean("0","0","0");
        DataBean minBean1 = new DataBean("1","10","0");
        DataBean minBean2 = new DataBean("2","20","0");
        DataBean minBean3 = new DataBean("3","30","0");
        DataBean minBean4 = new DataBean("4","40","0");
        DataBean minBean5 = new DataBean("4","50","0");
        DataBean minBean6 = new DataBean("4","59","0");
        minBeanList.add(minBean0);
        minBeanList.add(minBean1);
        minBeanList.add(minBean2);
        minBeanList.add(minBean3);
        minBeanList.add(minBean4);
        minBeanList.add(minBean5);
        minBeanList.add(minBean6);
    }




    private String formatLongToTimeStr(int s) {
        int hour = 0;
        int minute = 0;
        int second = s;

        if (second > 60) {
            minute = second / 60;   //取整
            second = second % 60;   //取余
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return "剩余："+ hour +":"+minute+":"+second;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        leftTime = 0;
        handler.removeCallbacks(update_thread);
    }
    public void setListener(){
        OnClick onClick=new OnClick();
        start.setOnClickListener(onClick);
        stop.setOnClickListener(onClick);
    }

    //活动暂停弹出toast/弹窗/重置
    @Override
    protected void onPause() {
        super.onPause();
        chronometer.stop();
        if(Integer.parseInt(chronometer.getText().toString().split(":")[0])>0
                ||Integer.parseInt(chronometer.getText().toString().split(":")[1])>0) {
            Toast.makeText(MainActivity.this, "退出了？？干啥去了？！", Toast.LENGTH_SHORT).show();  //正计时中断
            suspendDialog();
            findViewById(R.id.stop_button).setVisibility(View.GONE);
            findViewById(R.id.start_button).setVisibility(View.VISIBLE);
            isCounting=false;
        }
        chronometer.setBase(SystemClock.elapsedRealtime()-startTime);//复位为0
        if(leftTime!=0) {
            Toast.makeText(MainActivity.this, "倒计时还没结束啊喂！", Toast.LENGTH_SHORT).show();  //倒计时中断
            handler.removeCallbacks(update_thread);
            leftTime=0;
            suspend2Dialog();
            text.setVisibility(View.GONE);
            picker.setVisibility(View.VISIBLE);
        }
    }


    protected void start(){
        chronometer.setBase(SystemClock.elapsedRealtime()-startTime);
        chronometer.start();
    }
    protected void stop(){
        stopDialog();
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime()-startTime);
    }


    public class OnClick implements View.OnClickListener{
        //动画效果
        Animation alphaDownAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha_down_animation);
        Animation alphaUpAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha_up_animation);
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View view){
            switch (view.getId()){
                case R.id.start_button:
                    if(!isCounting){
                        alphaDownAnimation.setFillAfter(false);
                        alphaUpAnimation.setFillAfter(false);
                        view.startAnimation(alphaDownAnimation);
                        findViewById(R.id.stop_button).startAnimation(alphaUpAnimation);

                        findViewById(R.id.start_button).setVisibility(View.GONE);
                        findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
                        start();
                        isCounting = true;


                    }
                    break;
                case R.id.stop_button:
                    if(isCounting){
                        alphaDownAnimation.setFillAfter(false);
                        alphaUpAnimation.setFillAfter(false);
                        view.startAnimation(alphaDownAnimation);
                        findViewById(R.id.start_button).startAnimation(alphaUpAnimation);



                        findViewById(R.id.stop_button).setVisibility(View.GONE);
                        findViewById(R.id.start_button).setVisibility(View.VISIBLE);
                        stop();
                        isCounting = false;


                    }
                    break;
            }
        }
    }

    //正计时手动结束弹窗
    public void stopDialog(){
        //前两排是关于正计时的
        int t1= Integer.parseInt(chronometer.getText().toString().split(":")[0]);
        int t2= Integer.parseInt(chronometer.getText().toString().split(":")[1]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        final Window window=dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String message;
        View stopView = getLayoutInflater().inflate(R.layout.stop_dialog,null,false);
        //加入到布局中textview的内容
        if(t1==0)
            message="才专注了"+t2+"秒？就这还想要气球？给爷爬！！";
        else if(t1>0&&t1<3)
            message="本次仅专注了"+t2+"秒，啊这...专注达到3分钟以上才会获得气球啊bugyellow！";
        else message="本次您专注了"+t1+"分"+t2+"秒，获得"+(int)(t1/3)+"个气球奖励！";

        dialog.setView(stopView);
        dialog.show();

        //布局中的小控件
        TextView textView=(TextView)dialog.findViewById(R.id.textView);
        TextView absorbTime=(TextView)dialog.findViewById(R.id.absorb_time);
        absorbTime.setText(message);
        //“完成”按钮跳出AlertDialog
        Button finish=(Button)dialog.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //倒计时结束弹窗
    public void stop2Dialog(){
        int hour = Integer.parseInt(hourString);
        float min = Float.parseFloat(minString);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog1=builder.create();
        final Window window=dialog1.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String message;
        View stopView = getLayoutInflater().inflate(R.layout.stop_dialog,null,false);
        if(hour==0&&min<3)
            message="本次仅专注了一分钟不到，啊这...专注达到3分钟以上才会获得气球啊混蛋！";
        else if(hour==0&&min>=3)
            message="本次仅专注了"+min+"分钟，掉落"+(int)(min/3)+"个气球";
        else message="本次您专注了"+hour+"个小时"+min+"分,获得"+(int)((hour*60+min)/3)+"个气球奖励！";
        dialog1.setView(stopView);
        dialog1.show();
        TextView textView=(TextView)dialog1.findViewById(R.id.textView);
        TextView absorbTime=(TextView)dialog1.findViewById(R.id.absorb_time);
        absorbTime.setText(message);
        Button finish=(Button)dialog1.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
    }

    //正计时切出自动结束弹窗
    public void suspendDialog(){
        int t1= Integer.parseInt(chronometer.getText().toString().split(":")[0]);
        int t2= Integer.parseInt(chronometer.getText().toString().split(":")[1]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog2=builder.create();
        final Window window=dialog2.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String message2;
        View suspendView = getLayoutInflater().inflate(R.layout.suspend_dialog,null,false);
        if(t1==0)
            message2="才专注了"+t2+"秒，还中途退出了？不是吧？就这？没有气球掉落！";
        else if(t1>0&&t1<3)
            message2="本次仅专注了"+t1+"分"+t2+"秒，专注达到3分钟以上才会获得气球啊混蛋！";
        else message2="本次您专注了"+t1+"分"+t2+"秒，但是中途退出了，仅获得"+(int)((t1*60)/5)+"个气球奖励。";
        dialog2.setView(suspendView);
        dialog2.show();
        TextView textView2=(TextView)dialog2.findViewById(R.id.textView2);
        TextView absorbTime2=(TextView)dialog2.findViewById(R.id.absorb_time2);
        absorbTime2.setText(message2);
        Button define=(Button)dialog2.findViewById(R.id.define);
        define.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }

    //倒计时切出自动结束弹窗
    public void suspend2Dialog(){
        int hour = Integer.parseInt(hourString);
        float min = Float.parseFloat(minString);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog3=builder.create();
        final Window window=dialog3.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String message3;
        View suspendView = getLayoutInflater().inflate(R.layout.suspend_dialog,null,false);
        if(hour==0&&min<3)
            message3="不是吧？就这？还中途退出？你在得意什么？？";
        else if(hour==0&&min>=3)
            message3="啊这...就"+(int)min+"分钟还中途退出，"+(int)(min/3)+"个气球，不能再多了！";
        else message3="针不戳！专注长达"+hour+"小时"+(int)min+"分钟！获得"+(int)((hour*60+min)/5)+"个气球！";
        dialog3.setView(suspendView);
        dialog3.show();
        TextView textView2=(TextView)dialog3.findViewById(R.id.textView2);
        TextView absorbTime2=(TextView)dialog3.findViewById(R.id.absorb_time2);
        absorbTime2.setText(message3);
        Button define=(Button)dialog3.findViewById(R.id.define);
        define.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        });
    }

    //重写沉浸式布局方法
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}


