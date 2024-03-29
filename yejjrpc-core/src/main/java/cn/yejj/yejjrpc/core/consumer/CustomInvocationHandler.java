package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.api.*;
import cn.yejj.yejjrpc.core.consumer.http.OkHttpInvoker;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import cn.yejj.yejjrpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Slf4j
public class CustomInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext rpcContext;
    List<InstanceMata> providers;
    HttpInvoker httpInvoker;


    public CustomInvocationHandler(Class<?> service,
                                   RpcContext rpcContext,
                                   List<InstanceMata> providers) {
        this.rpcContext = rpcContext;
        this.providers = providers;
        this.service = service;
        int timeout = Integer.parseInt(rpcContext.getParameters().getOrDefault("timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        //如果返回调用超时异常，则进行N-1次重试，
        int reTry = Integer.parseInt(rpcContext.getParameters().get("retrys"));
        Object result = null;

        while (reTry-- > 0) {
            log.debug("===============retry time :" +reTry);
            try {
                //执行前置filter
                for (Filter filter : rpcContext.getFilters()) {
                    //查询是否有缓存，如果有则直接返回结果
                    Object response = filter.prefilter(rpcRequest);
                    if (response != null) {
                        log.debug(filter.getClass().getName() + "======>prefilter" + response);
                        return response;
                    }
                }

                List<InstanceMata> nodes = rpcContext.getRouter().route(providers);
                InstanceMata node = rpcContext.getLoaderBalacer().choose(nodes);
                RpcResponse rpcResponse = httpInvoker.post(rpcRequest, node.toUrl());
                result = castResponseToReturnResult(method, rpcResponse);
                //执行后置filter
                for (Filter filter : rpcContext.getFilters()) {
                    //需要把cachefilter 放在最后，保证是最后执行的filter TODO
                    Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
                    if (result != null) {
                        return filterResult;
                    }

                }
                return result;

            } catch (Exception e) {
                if (!(e.getCause() instanceof SocketTimeoutException)){
                    throw e;
                }
            }
        }
        throw  new SocketTimeoutException("调用超时");
//        return result;
    }

    private static Object castResponseToReturnResult(Method method, RpcResponse rpcResponse) {
        if (rpcResponse.isStatus()) {
            Object result = rpcResponse.getResult();
            return TypeUtils.castResult(method, result, rpcResponse);
        } else {
//            Exception ex = rpcResponse.getEx();
            throw new RpcException(rpcResponse.getEx(), RpcExceptionEnum.NOMETHOD_EX);
        }
    }


}
