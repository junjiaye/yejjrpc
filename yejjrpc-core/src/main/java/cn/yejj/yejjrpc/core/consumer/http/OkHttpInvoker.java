package cn.yejj.yejjrpc.core.consumer.http;

import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @program: yejjrpc
 * @ClassName: OkHttpInvoker
 * @description:
 * @author: yejj
 * @create: 2024-03-22 10:48
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {
    private final static MediaType JSON_TYPE = MediaType.get("application/json;charset=utf-8");

    OkHttpClient okHttpClient = null;
    public OkHttpInvoker(int timeout){
         okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.MINUTES))
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }
    @Override
    public RpcResponse post(RpcRequest rpcRequest,String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        //String url = "http://localhost:8080/";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON.toJSONString(rpcRequest), JSON_TYPE))
                .build();
        String respJson = "";
        try {
            respJson = okHttpClient.newCall(request).execute().body().string();
            log.debug(respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RpcException(e, RpcExceptionEnum.NOMETHOD_EX);
        }

    }
}
