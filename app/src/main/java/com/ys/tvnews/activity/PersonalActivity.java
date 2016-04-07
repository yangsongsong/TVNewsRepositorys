package com.ys.tvnews.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.BitmapUtils;
import com.ys.tvnews.R;
import com.ys.tvnews.application.MyApplication;
import com.ys.tvnews.httpurls.HttpUrl;
import com.ys.tvnews.receiver.UpdateVersionReceiver;
import com.ys.tvnews.utils.AppUtils;
import com.ys.tvnews.utils.Util;
import com.ys.tvnews.views.PersonalScrollView;
import com.ys.tvnews.views.TitleViews;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.jpush.android.api.JPushInterface;
import decoding.Intents;

/**
 * Created by sks on 2015/11/25.
 */
public class PersonalActivity extends BaseActivity implements View.OnClickListener,PersonalScrollView.OnScrollListener,CompoundButton.OnCheckedChangeListener{

    private PersonalScrollView personal_scrollview;
    private TextView title_textView;
    private ImageView title_back_imageView;
    private ImageView user_head_img;
    private Button btn_quick_login;
    private BitmapUtils bitmapUtils;
    private TextView scan_tv,collect_tv;
    private TitleViews titleViews;
    private ToggleButton push_news_toggle;
    private TextView tv_check_version;
    private String userPhone;     //用户名
    UpdateVersionReceiver updateVersionReceiver; //更新app的广播
    private Bitmap bitmap ; //  登录头像


    private final static int SCANNIN_GREQUEST_CODE = 1;

    private Handler image_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
              //  user_head_img.setImageBitmap((Bitmap)msg.obj);
                user_head_img.setImageBitmap(toRoundBitmap((Bitmap)msg.obj));
            }else if(msg.what == 1){
                user_head_img.setImageResource(R.mipmap.touxiang);
                titleViews.setRightText("登录");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int loadLayout() {
        return R.layout.activity_personal;
    }

    @Override
    protected void findView() {
        AppUtils.settingStatus(PersonalActivity.this);
        tv_check_version = (TextView) findViewById(R.id.tv_check_version);
        title_textView = (TextView) findViewById(R.id.title_title_textview);
        title_back_imageView = (ImageView) findViewById(R.id.title_back_imageview);
        user_head_img = (ImageView) findViewById(R.id.user_head_img);
        btn_quick_login = (Button) findViewById(R.id.btn_quick_login);
        scan_tv = (TextView) findViewById(R.id.scan_tv);
        collect_tv = (TextView) findViewById(R.id.collect_tv);
        personal_scrollview = (PersonalScrollView) findViewById(R.id.person_scroll_view);
        push_news_toggle = (ToggleButton) findViewById(R.id.push_news_toggle);
        titleViews = (TitleViews) findViewById(R.id.title_personal);
    }

    @Override
    protected void regListener() {
        tv_check_version.setOnClickListener(this);
        title_back_imageView.setOnClickListener(this);
        btn_quick_login.setOnClickListener(this);
        scan_tv.setOnClickListener(this);
        collect_tv.setOnClickListener(this);
        personal_scrollview.setOnScrollListener(this);
        push_news_toggle.setOnCheckedChangeListener(this);
    }

    @Override
    protected void loadData() {

        titleViews.setTitleTextColor(Color.RED);

        userPhone = getIntent().getStringExtra("phone");
        if(userPhone!=null){
            btn_quick_login.setText("用户:"+userPhone);
            btn_quick_login.setBackgroundColor(Color.parseColor("#14d212"));
            btn_quick_login.setBackgroundResource(R.drawable.btn_experience_shape);
            btn_quick_login.setTextColor(Color.parseColor("#1699d9"));
        }

       // titleViews.setBackgroundColor(Color.RED);
        titleViews.getBackground().setAlpha(0);
        bitmapUtils = new BitmapUtils(PersonalActivity.this);
        title_textView.setText("个人中心");
        Intent intent = getIntent();
        if(intent.getStringExtra("nickname")!=null) {
            btn_quick_login.setText(intent.getStringExtra("nickname"));
            btn_quick_login.setTextColor(Color.RED);
            btn_quick_login.setBackgroundResource(R.drawable.btn_experience_shape);
        }else{
             btn_quick_login.setBackgroundResource(R.mipmap.setting_login);
        }
//        String head_img_url = intent.getStringExtra("head_img_url");
//        if(head_img_url!=null){
//            btn_quick_login.setBackgroundResource(R.mipmap.tuichu_denglu);
//            bitmapUtils.display(user_head_img,head_img_url);
//        }


    }

    @Override
    protected void reqServer() {

        Log.e("info","执行了server");
        if(getIntent()!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                   // Log.e("head_image_url", getIntent().getStringExtra("head_img_url"));
                    if(getIntent().getStringExtra("head_img_url")!=null) {
                        bitmap = Util.getbitmap(getIntent().getStringExtra("head_img_url"));
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        msg.what = 0;
                        image_handler.sendMessage(msg);
                    }
                }
            }).start();
        }else{
            user_head_img.setImageResource(R.mipmap.touxiang);
            titleViews.setRightText("登录");
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back_imageview:
                finish();
                break;
            case R.id.btn_quick_login:
//                if(btn_quick_login.getText()!=null){
//                    Log.e("info",btn_quick_login.getText().toString());
//                    showToast("您已经登录了哦!");
//                }else {
                    Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                    startActivity(intent);
                    this.finish();
//                }
                break;
            case R.id.scan_tv:
                Intent scan_intent = new Intent();
                scan_intent.setClass(PersonalActivity.this, MipcaActivityCapture.class);
                scan_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(scan_intent, SCANNIN_GREQUEST_CODE);
                break;
            case R.id.collect_tv:
                Intent collect_intent = new Intent(PersonalActivity.this,CollectActivity.class);
                startActivity(collect_intent);
                break;
            case R.id.tv_check_version:
                sendBroadcast(new Intent(UpdateVersionReceiver.UPDATEACTION));
                break;
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode== SCANNIN_GREQUEST_CODE){ //扫描之后的回调处理
            if(resultCode==RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Log.e("result", bundle.getString("result"));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onScroll(int scrollY) {
        int height = title_back_imageView.getHeight();
        Log.e("info", "height" + height+"titleViews"+titleViews.getHeight());
        Log.e("info",scrollY+"");
        double alpha = ((double) scrollY /(double) (height)) *255;
        if(scrollY <height) {
            titleViews.getBackground().setAlpha((int) alpha);
        }else{
          //  titleViews.setBackground(Color.WHITE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       switch (buttonView.getId()){
           case R.id.push_news_toggle:
               if(isChecked){
                   Toast.makeText(PersonalActivity.this,"您开启了新闻推送功能哦",Toast.LENGTH_SHORT).show();
                   //回复极光推送服务
                   JPushInterface.resumePush(getApplicationContext());
               }else{
                   //if(JPushInterface.isPushStopped(getApplicationContext())) {
                   Toast.makeText(PersonalActivity.this,"您关闭了新闻推送功能哦",Toast.LENGTH_SHORT).show();
                   //停止极光推送服务
                       JPushInterface.stopPush(getApplicationContext());
                   //}
               }
               break;
           default: break;
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateVersionReceiver = new UpdateVersionReceiver();
        IntentFilter filter = new IntentFilter(UpdateVersionReceiver.UPDATEACTION);
        this.registerReceiver(updateVersionReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(updateVersionReceiver);
    }


    /**
     * 把bitmap转成圆形
     * */
    public Bitmap toRoundBitmap(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        //取最短边做边长
        if(width<height){
            r=width;
        }else{
            r=height;
        }
        //构建一个bitmap
        Bitmap backgroundBm=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }

}
