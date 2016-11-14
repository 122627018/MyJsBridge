



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
