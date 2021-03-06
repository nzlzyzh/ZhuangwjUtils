package com.zwj.zhuangwjutils.activity.network;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zwj.zhuangwjutils.R;
import com.zwj.zhuangwjutils.bean.ParseBean;
import com.zwj.zhuangwjutils.constant.UrlConstant;
import com.zwj.zwjutils.JsonUtil;
import com.zwj.zwjutils.LogUtils;
import com.zwj.zwjutils.ToastUtil;
import com.zwj.zwjutils.net.NetManager;
import com.zwj.zwjutils.net.bean.RequestBean;
import com.zwj.zwjutils.net.bean.ResponseBean;
import com.zwj.zwjutils.net.callback.ParseBeanCallBack;
import com.zwj.zwjutils.net.callback.SimpleCallBack;
import com.zwj.zwjutils.net.callback.SimpleCommonCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NetManager网络工具的使用demo
 */
public class NetMangerDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_manger_demo);
    }

    public void testCookies(View view) {
//        new RequestBean("http://10.111.24.21:8080/hch/debug/testRequest", RequestBean.METHOD_GET)
//                .setCallback(new SimpleCallBack() {
//                    @Override
//                    public void onSuccess(ResponseBean responseBean) {
//                        LogUtils.sysout("result ---> "+responseBean.getResult());
//                        DbCookieStore dbCookieStore = DbCookieStore.INSTANCE;
//                        List<HttpCookie> cookies = dbCookieStore.getCookies();
//                        for(HttpCookie cookie : cookies) {
//                            LogUtils.sysout("name ---> "+cookie.getName());
//                            LogUtils.sysout("coment ---> "+cookie.getComment());
//                            LogUtils.sysout("comenturl ---> "+cookie.getCommentURL());
//                            LogUtils.sysout("domain ---> "+cookie.getDomain());
//                            LogUtils.sysout("path ---> "+cookie.getPath());
//                        }
//                    }
//                }).request(this);

        new RequestBean("http://139.224.27.154:8080/iqCloud/api/account/getValidCode", RequestBean.METHOD_GET)
                .addParam("phone", "18650187931")
                .setCallback(new SimpleCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        LogUtils.d(TAG, responseBean.getMessage());
                    }
                }).request(this);
    }

    public void testParseBeanCallback(View view) {
        new RequestBean("http://10.111.24.21:8080/hch/debug/testParseBean", RequestBean.METHOD_GET)
                .setCallback(new ParseBeanCallBack<ParseBean>(ParseBean.class) {
                    @Override
                    public void onSuccess(ResponseBean responseBean, ParseBean obj) {
                        LogUtils.sysout(obj.getName());
                        LogUtils.sysout(obj.getTestParse());
                    }
                }).request(this);
    }

    public void testUpload(View view) {

        File sdcard = Environment.getExternalStorageDirectory();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("code", "fsfdsfdf");

        List<File> uplodFileList = new ArrayList<>();
        uplodFileList.add(new File(sdcard+File.separator+"a.jpg"));
        uplodFileList.add(new File(sdcard+File.separator+"70BA32A6658B.jpg"));

        NetManager.uploadFile("http://10.111.24.21:8080/hch/uploadfile/uploadQuasiNewImages", uplodFileList, paramMap, new SimpleCommonCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtils.sysout("reslut ---> "+result);
            }
        });
    }

    /**
     * 测试json和token一起传递
     * @param view
     */
    public void testJsonAndParamRequest(View view) {
        ParseBean parseBean = new ParseBean();
        parseBean.setName("aaa");
        parseBean.setTestParse("bbb");
        new RequestBean(UrlConstant.BASE_URL+"/debug/testJsonAndParamRequest", RequestBean.METHOD_POST)
                .setBodyContent(JsonUtil.toJSONString(parseBean)).request(this);
    }

    public void testMuldParamRequest(View view) {
        List<String> values = new ArrayList<>();
        values.add("a");
        values.add("c");
        values.add("b");

        new RequestBean(UrlConstant.BASE_URL+"/debug/testMuldParamRequest", RequestBean.METHOD_POST)
                .addParamArray("mul", values)
                .request(this);
    }

    public void getCar(View view) {
        new RequestBean("http://120.24.6.87/ChetongxiangHCH/product/rental/getInfoByID", RequestBean.METHOD_GET)
                .addParam("id", "1")
                .setCallback(new SimpleCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                    }
                }).request(this);
    }

    public void testUnlogin(View view) {
        RequestBean.callbackUnlogin = true;
        RequestBean.addGlobalHead("deviceTag", "1");
        RequestBean.addGlobalHead("token", "aad5cebe86ee4db6b30a5e724c8b883b00dc0275");
        new RequestBean("http://192.168.31.180:8080/iqCloud/debug2/testCheckRole", RequestBean.METHOD_GET)
                .setCallback(new SimpleCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        LogUtils.sysout("result ---> "+responseBean.getResult());
                    }

                    @Override
                    public void onUnlogin(String msg) {
                        super.onUnlogin(msg);
                        ToastUtil.toast(NetMangerDemoActivity.this, msg);
                    }
                }).request(this);
    }

    public void testHttps(View view) {
        new RequestBean("https://139.224.27.154:8443/MeetingRoomManagement/api/qrCode/generate", RequestBean.METHOD_GET)
                .addParam("deviceId", "fdfsfdsfds")
//                .addParam("tk", "aa")
//                .addParam("uid", "xxx")
                .setCallback(new SimpleCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        LogUtils.sysout("result ---> "+responseBean.getResult());
                    }
                }).request(this);
    }
}
