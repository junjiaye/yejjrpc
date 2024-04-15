package cn.yejj.yejjrpc.core.provider;

import cn.yejj.yejjrpc.core.api.RpcContext;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.exception.RpcExceptionEnum;
import cn.yejj.yejjrpc.core.exception.RpcException;
import cn.yejj.yejjrpc.core.mate.ProviderMata;
import cn.yejj.yejjrpc.core.utils.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @program: yejjrpc
 * @ClassName: ProvideInvoker
 * @description:
 * @author: yejj
 * @create: 2024-03-21 13:51
 */
public class ProviderInvoker {
    private MultiValueMap<String, ProviderMata> skeletonMap;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeletonMap = providerBootstrap.getSkeletonMap();
    }

    public RpcResponse invokeRequest(RpcRequest request) {

        if (!request.getParams().isEmpty()){
            request.getParams().forEach(RpcContext::setContextParameter);
        }
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMata> providerMates = skeletonMap.get(request.getService());
        try {
            ProviderMata meta = findProviderMate(providerMates,request.getMethodSign());
            Method method =meta.getMethod();
            Object[] args = processArgs(request.getArgs(),method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setResult(result);
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RpcException(e, RpcExceptionEnum.SOUCKET_TIMEOUT_EX));
        }catch (InvocationTargetException e) {
            rpcResponse.setEx(new RpcException(e, RpcExceptionEnum.SOUCKET_TIMEOUT_EX));
        }
        return rpcResponse;

    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0){
            return args;
        }
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i],parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMata findProviderMate(List<ProviderMata> providerMatas, String methodSign) {
        Optional<ProviderMata> first = providerMatas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return first.orElse(null);
    }
}
