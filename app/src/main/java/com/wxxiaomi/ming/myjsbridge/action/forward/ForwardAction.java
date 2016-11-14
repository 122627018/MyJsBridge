package com.wxxiaomi.ming.myjsbridge.action.forward;

/**
 * Created by 12262 on 2016/11/11.
 * native跳转的动作
 */

public class ForwardAction {
    public boolean isH5;
    public String page;
    public String data;
    public boolean isReturn;
    public String callBack;

    public ForwardAction(boolean isH5, String page, String data, boolean isReturn, String callBack) {
        this.isH5 = isH5;
        this.page = page;
        this.data = data;
        this.isReturn = isReturn;
        this.callBack = callBack;
    }

    @Override
    public String toString() {
        return "ForwardAction{" +
                "isH5=" + isH5 +
                ", page='" + page + '\'' +
                ", data='" + data + '\'' +
                ", isReturn=" + isReturn +
                ", callBack='" + callBack + '\'' +
                '}';
    }
}
