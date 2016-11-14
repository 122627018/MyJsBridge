package com.wxxiaomi.ming.myjsbridge;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxxiaomi.ming.myjsbridge.action.forward.ForwardAction;
import com.wxxiaomi.ming.myjsbridge.action.forward.ForwardTypeAdapter;
import com.wxxiaomi.ming.myjsbridge.action.ui.UiAction;
import com.wxxiaomi.ming.myjsbridge.ui.SimpleWebActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected BridgeWebView mWebView;
    private Toolbar toolbar1;
    private TextView tvTitle;
    private Button btnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar1.findViewById(R.id.tv_toolbar);
        btnRight = (Button) toolbar1.findViewById(R.id.btnRight);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (BridgeWebView) findViewById(R.id.web_view);
//        Gson g = new GsonBuilder().registerTypeAdapter(BaseDomain.class, new JsonDeserializer<BaseDomain>() {
//            @Override
//            public BaseDomain deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                Log.i("wang","json.toString()="+json.toString());
//                String type = json.getAsJsonObject().get("type").getAsString();
//                if (type.equals("computer")) {
//                    Log.i("wang","type="+type);
////                    return context.deserialize(json, new TypeToken<BaseDomain<Computer>>(){}.getType());
//                    return context.deserialize(json,Computer.class);
//                } else if (type.equals("phone")) {
//                    Log.i("wang","type="+type);
////                    return context.deserialize(json, new TypeToken<BaseDomain<MobilePhone>>(){}.getType());
//                    return context.deserialize(json,MobilePhone.class);
//                }
//                return null;
//            };
//        }).create();
//        String json = "" +
//                "{\"type\":\"phone\",\"info\":{\"names\":\"55\",\"type\":\"phone\"}}";
//       Domain domain = g.fromJson(json, Domain.class);
////        String json = "" +
////                "{\"names\":\"55\",\"type\":\"phone\"}";
////        BaseDomain domain = g.fromJson(json, BaseDomain.class);
//        Log.i("wang","baseDomain.info:"+domain.info.type);
//        String json = "" +
//               "{\"type\":\"alert\",\"content\":\"我是弹出框\"}";
//        Gson g = new GsonBuilder().registerTypeAdapter(DialogACtion.class, new JsonDeserializer<DialogACtion>() {
//            @Override
//            public DialogACtion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                String type = json.getAsJsonObject().get("type").getAsString();
//                if(type.equals("loading")){
//                    return context.deserialize(json, LoadingAction.class);
//                }else if(type.equals("alert")){
//                    return context.deserialize(json, AlertAction.class);
//                }
//                return null;
//            }
//        }).create();
//        DialogACtion baseDialogACtion = g.fromJson(json, DialogACtion.class);
//        if(baseDialogACtion instanceof LoadingAction){
//            baseDialogACtion = (LoadingAction)baseDialogACtion;
//            Log.i("wang","LoadingAction.title:"+((LoadingAction) baseDialogACtion).title);
//        }else if(baseDialogACtion instanceof AlertAction){
//            baseDialogACtion = (AlertAction)baseDialogACtion;
//            Log.i("wang","AlertAction.content:"+((AlertAction) baseDialogACtion).content);
//        }
        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
        mWebView.loadUrl("file:///android_asset/edit.html");
        mWebView.send("ImInit", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.i("wang","初始化的data="+data);
                UiAction uiAction = new Gson()
                        .fromJson(data, UiAction.class);
                parseUiAction(uiAction);
            }
        });
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
    }
    Map<Integer,String> list = new HashMap<>();
    /**
     * 处理跳转
     * @param forwardAction
     */
    private void handleForward(ForwardAction forwardAction) {
        Log.i("wang","Thread.currentThread().getName()="+Thread.currentThread().getName());
        if(forwardAction.isH5){
            Intent intent = new Intent(MainActivity.this,SimpleWebActivity.class);
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
            String pars = data.getStringExtra("value");
            String callback = list.get(1);
            Log.i("wang","callback="+callback+"-value="+pars);
            list.remove(callback);
            Log.i("wang","Thread.currentThread().getName()="+Thread.currentThread().getName());
            mWebView.callHandler(callback,pars, null);
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void parseUiAction(final UiAction uiAction) {
        toolbar1.setTitle(uiAction.title);
        setToolbarRight("", getResource1(uiAction.right.icon), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.callHandler(uiAction.right.callback, "", null);
            }
        });
    }


    public int  getResource1(String imageName){
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(imageName.split("\\.")[2], "mipmap", ctx.getPackageName());
        return resId;
    }

    /**
     * 解析初始化数据
//     * @param data
     */
//    private void parseInitData(String data) {
        //var pas = "initView?title=asdasdsadright=R.id.callback=onRightBtnClick";
//        toolbar1.setTitle("sadasd");
//        setToolbarRight("", R.mipmap.ic_launcher, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //假装解析这个东西
//                mWebView.callHandler("onRightBtnClick","惦记啦",new CallBackFunction()
//                        {
//                            @Override
//                            public void onCallBack(String data) {
//
//                            }
//                        }
//                );
//            }
//        });

//        JsonParser parser = new JsonParser();
//        JsonObject object = (JsonObject) parser.parse(data);
//        String tagname = object.get("tagname").getAsString();
//        String pars = object.get("pars").toString();
//         switch (tagname){
//           case "showUI":
//               DialogAction action = new Gson().fromJson(pars,DialogAction.class);
//               switch (action.getType()){
//                   case DialogAction.SHOWLOADING:
//                       //显示loading
//                       break;
//               }
//               break;
//       }
//
//    }

    public  class MyWebViewClient extends BridgeWebViewClient {
        public MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }
    }

    protected void setTitleName(String name){
        tvTitle.setText(name);
    }
    protected void setToolbarRight(String text, @Nullable Integer icon, View.OnClickListener btnClick){
        if(text!=null)
        {
            btnRight.setText(text);
        }
        if (icon != null) {
            btnRight.setBackgroundResource(icon.intValue());
            ViewGroup.LayoutParams linearParams = btnRight.getLayoutParams();
//            linearParams.height= ToolUtils.dp2px(this,26);
//            linearParams.width=ToolUtils.dp2px(this,26);
            btnRight.setLayoutParams(linearParams);
        }
        btnRight.setOnClickListener(btnClick);
    }
}
