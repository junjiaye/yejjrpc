package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.api.*;
import cn.yejj.yejjrpc.core.consumer.http.OkHttpInvoker;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.governance.SlidingTimeWindow;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import cn.yejj.yejjrpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Slf4j
public class CustomInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext rpcContext;
    final List<InstanceMata> providers;
    final List<InstanceMata> isolateProviders = new ArrayList<>();
    final List<InstanceMata> halfProviders = new ArrayList<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;

    Map<String, SlidingTimeWindow> windowMap = new HashMap<>();


    public CustomInvocationHandler(Class<?> service,
                                   RpcContext rpcContext,
                                   List<InstanceMata> providers) {
        this.rpcContext = rpcContext;
        this.providers = providers;
        this.service = service;
        int timeout = Integer.parseInt(rpcContext.getParameters().getOrDefault("consumer.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        //定时执行halfopen半开方法，第一次执行延迟10秒，后续没60秒执行一次
        int halfOpenInitialDelay = Integer.parseInt(rpcContext.getParameters()
                .getOrDefault("consumer.halfOpenInitialDelay", "10000"));
        int halfOpenDelay = Integer.parseInt(rpcContext.getParameters()
                .getOrDefault("consumer.halfOpenDelay", "60000"));
        this.executor.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay,
                halfOpenDelay, TimeUnit.MILLISECONDS);
    }

    //将关闭的节点信息放到半开节点数据中
    private void halfOpen() {
        log.debug("============>half open isolatedProviders" + isolateProviders);
        halfProviders.clear();
        halfProviders.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        //如果返回调用超时异常，则进行N-1次重试，
        int reTry = Integer.parseInt(rpcContext.getParameters().get("consumer.retries"));
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
                InstanceMata node;

                //如果半开节点中有数据，则进行探活操作：从半开节点中随机选择一个节点进行调用，如果调用成功，则证明该节点已恢复服务
                synchronized (halfProviders){
                    if(halfProviders.isEmpty()){
                        List<InstanceMata> nodes = rpcContext.getRouter().route(providers);
                        node = rpcContext.getLoaderBalancer().choose(nodes);
                        log.debug(" loadBalancer.choose(instances) ==> {}", node);
                    }else {
                        node = halfProviders.remove(0);
                        log.debug(" check alive instance ==> {}", node);
                    }
                }
                RpcResponse rpcResponse = null;
                String url = node.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = castResponseToReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    synchronized (windowMap) {
                        SlidingTimeWindow window = windowMap.computeIfAbsent(url,k->new SlidingTimeWindow());
//                        if (window == null){
//                            window = new SlidingTimeWindow();
//                            windowMap.put(url,window);
//                        }
                        window.record(System.currentTimeMillis());
                        log.error("instance {} in window with {}",url,window.getSum());
                        if (window.getSum() >10){
                            isolate(node);
                        }
                    }
                    throw e;
                }
                //从半开节点以及错误节点信息中移除当前节点信息
                synchronized (providers){
                    if(!providers.contains(node)){
                        isolateProviders.remove(node);
                        providers.add(node);
                        log.debug("instance {} is recovered, isolatedProviders={}, providers={}"
                                , node, isolateProviders, providers);
                    }
                }
                //执行后置filter
                for (Filter filter : rpcContext.getFilters()) {
                    //需要把cachefilter 放在最后，保证是最后执行的filter TODO
                    Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
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
        throw  new SocketTimeoutException("调用超时 ");
//        return result;
    }

    private void isolate(InstanceMata node) {
        providers.remove(node);
        isolateProviders.add(node);
    }

    private static Object castResponseToReturnResult(Method method, RpcResponse rpcResponse) {
        if (rpcResponse.isStatus()) {
            Object result = rpcResponse.getResult();
            return TypeUtils.castResult(method, result, rpcResponse);
        } else {
//            Exception ex = rpcResponse.getEx();
            RpcException ex = rpcResponse.getEx();
            if (ex != null){
                log.error("rpc  response  error. ",ex);
                throw ex;
            }
            return null;
        }
    }


}
