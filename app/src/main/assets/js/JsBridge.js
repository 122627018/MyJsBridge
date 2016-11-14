function connectWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) {
        callback(WebViewJavascriptBridge)
    } else {
        document.addEventListener(
            'WebViewJavascriptBridgeReady'
            , function() {
                callback(WebViewJavascriptBridge)
            },
            false
        );
    }
}

function registerBridge(MyInitCallback,registerCallBack){
    connectWebViewJavascriptBridge(function(bridge){
        bridge.init(MyInitCallback);
        registerCallBack(bridge);

    });
}

function toPage(type_tag,page,data,isReturn,callback){
    var datas = {
    	        "type_tag":type_tag,
    	        "page":page,
    	        "data":data,
    	        "isReturn":isReturn,
    	        "callback":callback
    };
    window.WebViewJavascriptBridge.callHandler('forward', datas, null);
}

function loadComplete(){
    window.WebViewJavascriptBridge.callHandler('loadComplete', "", null);
}

function showLoading(title,content){
    var data =
    {
        "type":"loading",
        "title":title,
        "content":content
    };
    window.WebViewJavascriptBridge.callHandler('dialog', data, null);
}


function showAlert(title,content,okMsg,cancelMsg,okCallback,cancelCallback){
        var data =
        {
	        "type":"alert",
	        "title":title,
	        "content":content,
	        "okMsg":okMsg,
            "cancelMsg":cancelMsg,
            "okCallback":okCallback,
            "cancelCallback":cancelCallback
        };
        window.WebViewJavascriptBridge.callHandler('dialog', data, null);
}

function showSimpleDialog(title,content,okCallback){
        var data =
        {
	        "type":"alert",
	        "title":title,
	        "content":content,
	        "okMsg":"确定",
            "cancelMsg":"取消",
            "okCallback":okCallback,
            "cancelCallback":""
        };
        window.WebViewJavascriptBridge.callHandler('dialog', data, null);
}

