AsyncHandlerLibrary
===================

HTTP请求异步处理开源库，性能优于android-async-http开源库

使用方法：
1. 将AsyncHandlerLibrary作为依赖库导入工程
2. 统一唯一入口类com.dreamsun.sdk.asyncquery.HttpAsyncQueryEngine.java
3. 统一唯一入口调用方法executeAsync，根据入参不同，实现方法重载，以满足不同的入参业务处理
4. 在工程入口产生AsyncHandlerLibrary唯一实例调用，如：HttpAsyncQueryEngine.createInstance().initEngineWithHomeBase(“http://www.baidu.com”);
5. 在具体业务类及方法中调用executeAsync方法，如：
		Map<String,Object> params = new HashMap<String,Object>();  // 入口参数定义
    // this 为Context
    // Constants.INTERFACE_USER_ADD 为请求URI，可以直接跟在initEngineWithHomeBase中，即此处的URI可以为不写
    // params 入口参数，可以为写
    // HTTPMethod.POST 请求类型：GET、POST、GET, POST, OCTET_STREAM, XML，后两种分别为流文件处理、报文处理，默认不写，为GET请求
    // ProcessReceivedDataCallback 回调监听处理
		HttpAsyncQueryEngine.createInstance().executeAsync(this, Constants.INTERFACE_USER_ADD, params, HTTPMethod.POST, new ProcessReceivedDataCallback() {
			@Override
			public void onCompleted(String json) {
            // 响应结果处理
			}
		});


欢迎使用，并提供建议，QQ：94558792 梦阳
