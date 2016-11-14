package com.wxxiaomi.ming.myjsbridge.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxxiaomi.ming.myjsbridge.action.forward.ForwardAction;
import com.wxxiaomi.ming.myjsbridge.action.forward.ForwardTypeAdapter;
import com.wxxiaomi.ming.myjsbridge.action.dialog.AlertAction;
import com.wxxiaomi.ming.myjsbridge.action.dialog.DialogACtion;
import com.wxxiaomi.ming.myjsbridge.action.dialog.DialogTypeAdapter;
import com.wxxiaomi.ming.myjsbridge.action.dialog.LoadingAction;
import com.wxxiaomi.ming.myjsbridge.bean.UserCommonInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 12262 on 2016/11/12.
 * webview的基础activity，用于webview的初始化
 * 不能含有toolbar，toolbar的初始化应该是在simpleActivity中
 */

public abstract class BaseWebActivity extends AppCompatActivity {
    protected BridgeWebView mWebView;

    protected final int SHOW_LOADING_DIALOG = 1;
    protected final int CLOSE_LOADING_DIALOG = 2;
    protected final int SHOW_MSG_DIALOG = 3;
    protected final int CLOSE_MSG_DIALOG = 4;

    ProgressDialog dialog;
    AlertDialog alertDialog;
    private String message = "未知错误";
    Map<Integer,String> list = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        int webViewId = initViewAndReutrnWebViewId(savedInstanceState);
        mWebView = (BridgeWebView) findViewById(webViewId);
        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
        dialog = new ProgressDialog(BaseWebActivity.this);
        dialog.setTitle("请等待");//设置标题
        dialog.setMessage("正在加载");
        dialog.show();
        initWebView();
        //这里send的应该别的activity带过来的数据
        String data = getIntent().getStringExtra("data");

        mWebView.send(data==null?"":data, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.i("wang","从js返回回来的初始化toolbar的data="+data);
               initToolBar(data);
            }
        });
        initCommonMethod();

    }

    private void initCommonMethod() {
        //跳转
        mWebView.registerHandler("forward", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("wang","forward");
                Gson gson = new GsonBuilder().registerTypeAdapter(ForwardAction.class, new ForwardTypeAdapter()).create();
                ForwardAction forwardAction = gson.fromJson(data, ForwardAction.class);
                Log.i("wang","forwardAction="+forwardAction.toString());
                handleForward(forwardAction);
            }
        });

        mWebView.registerHandler("dialog", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("wang","dialog,data:"+data);
                Gson gson = new GsonBuilder().registerTypeAdapter(DialogACtion.class, new DialogTypeAdapter()).create();
                DialogACtion dialogAction = gson.fromJson(data, DialogACtion.class);
                Log.i("wang","dialogAction="+dialogAction.toString());
//                handleForward(forwardAction);
                handlerDialogAction(dialogAction);
            }
        });

        mWebView.registerHandler("getUser", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                UserCommonInfo userInfo = new UserCommonInfo();
                userInfo.emname = "122627018";
                userInfo.head = "/asd.jpg";
                userInfo.id = 25;
                userInfo.name = "王浩明";
                String json = new Gson().toJson(userInfo);
                function.onCallBack(json);
            }
        });

        mWebView.registerHandler("finish", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Map<String, String> map = parseData(data);
                if(map.get("isReturn").equals("true")){
                    String pars = map.get("data");
                    Intent intent = new Intent();
                    intent.putExtra("value",pars);
                    setResult(1,intent);
                    finish();
                }else{
                    finish();
                }
            }
        });

        mWebView.registerHandler("loadComplete", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("wang","native->loadComplete");
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

    }

    /**
     * 处理弹框事件
     * @param dialogAction
     */
    private void handlerDialogAction(DialogACtion dialogAction) {
        if(dialogAction instanceof LoadingAction){
            LoadingAction action = (LoadingAction)dialogAction;
            String title = action.title;
            String content = action.content;
            dialog.setTitle(title);
            dialog.setMessage(content);
            dialog.show();
        }else if(dialogAction instanceof AlertAction){
            final AlertAction action = (AlertAction)dialogAction;
            Log.i("wang","AlertAction.toString()="+action.toString());
            if(action.okCallback!=""){
                alertDialog = new AlertDialog.Builder(BaseWebActivity.this)
                        .setPositiveButton(action.okMsg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mWebView.callHandler(action.okCallback,"",null);
                            }
                        }).create();
            }else{
                alertDialog = new AlertDialog.Builder(BaseWebActivity.this)
                        .setPositiveButton(action.okMsg,null).create();

            }
            alertDialog.setTitle(action.title);
            alertDialog.setMessage(action.content);
            alertDialog.show();
        }
    }


    protected Map<String, String> parseData(String data) {
        Map<String, String> datas = new HashMap<>();
        String[] split = data.split("&");
        for (String item : split) {
            datas.put(item.substring(0, item.indexOf("=")), item.substring(item.indexOf("=") + 1, item.length()));
        }
        return datas;
    }

    /**
     * 自定义的WebViewClient
     */
    protected class MyWebViewClient extends BridgeWebViewClient {
        public MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }
    }

    protected abstract int initViewAndReutrnWebViewId(Bundle savedInstanceState);
    protected abstract void initWebView();
    protected abstract void initToolBar(String data);

    /**
     * 处理跳转
     * @param forwardAction
     */
    private void handleForward(ForwardAction forwardAction) {
        Log.i("wang","Thread.currentThread().getName()="+Thread.currentThread().getName());
        if(forwardAction.isH5){
            Intent intent = new Intent(BaseWebActivity.this,SimpleWebActivity.class);
            if(forwardAction.data!=""){
                intent.putExtra("data",forwardAction.data);
            }
            if(forwardAction.page!=null){
                intent.putExtra("url",forwardAction.page);
            }
            if(forwardAction.isReturn) {
                String callBack = forwardAction.callBack;
                list.put(1,callBack);
                startActivityForResult(intent, 1);
            }else{
                startActivity(intent);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            Log.i("wang","startActivityForResult->requestCode="+requestCode);
            if(data!=null) {
                String pars = data.getStringExtra("value");
                Log.i("wang", "pars=" + pars);
                if (pars != "") {

                    String callback = list.get(1);
                    Log.i("wang", "callback=" + callback + "-value=" + pars);
                    list.remove(callback);
                    mWebView.callHandler(callback, pars, null);
                }
            }
        }
    }
}
