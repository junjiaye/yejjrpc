package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
public class YeJJInvocationHandler implements InvocationHandler {
    private final static MediaType JSON_TYPE = MediaType.get("application/json;charset=utf-8");
    Class<?> service;

    public YeJJInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        //rpcRequest.setMethod(method.getName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);
        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.isStatus()){

            Object result = rpcResponse.getResult();
            if(result instanceof JSONObject){
                JSONObject jsonrResult = (JSONObject) rpcResponse.getResult();
                return jsonrResult.toJavaObject(method.getReturnType());
            }
            Class<?> returnType = method.getReturnType();
            if(returnType.isPrimitive()){

            }
            return result;

        }else {
            Exception ex = rpcResponse.getEx();
            ex.printStackTrace();
        }
        return null;
    }

    //三种方法：httpclient OkHttpClient  urlconnection
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.MINUTES))
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        String url = "http://localhost:8080/";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON.toJSONString(rpcRequest), JSON_TYPE))
                .build();
        String respJson = "";
        try {
            respJson = okHttpClient.newCall(request).execute().body().string();
            System.out.println(respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
