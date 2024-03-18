package cn.yejj.yejjrpc.core.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import cn.yejj.yejjrpc.core.api.RpcRequest;
import cn.yejj.yejjrpc.core.api.RpcResponse;
import cn.yejj.yejjrpc.core.mate.ProviderMate;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import cn.yejj.yejjrpc.core.utils.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    //字段set方法，相当于实现了接口的set....方法
    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMate> skeletonMap = new LinkedMultiValueMap<>();

    @PostConstruct  //bean创建后。未初始化之前执行 相当于init-method 对应的销毁方法是@PreDestroy
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(YejjProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k));
        providers.values().forEach(x -> {
            genInterface(x);
        });
    }

    private void genInterface(Object x){
        Arrays.stream(x.getClass().getInterfaces()).forEach(intf -> {
            Method[] methods = intf.getMethods();
            for (Method method : methods){
                if(MethodUtils.checkLocalMethod(method)){
                    continue;
                }
                createProviderMeta(intf,x,method);
            }
        });
    }

    private void createProviderMeta(Class<?> intf, Object x, Method method) {
        ProviderMate providerMate = new ProviderMate();
        providerMate.setMethod(method);
        providerMate.setMethodSign(MethodUtils.methodSign(method));
        providerMate.setServiceImpl(x);
        System.out.println(providerMate);
        skeletonMap.add(intf.getCanonicalName(),providerMate);
    }

    public RpcResponse invokeRequest(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMate> providerMates = skeletonMap.get(request.getService());
        try {
            ProviderMate meta = findProviderMate(providerMates,request.getMethodSign());
            Method method =meta.getMethod();
            Object[] args = processArgs(request.getArgs(),method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setResult(result);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }catch (InvocationTargetException e) {
            // throw new RuntimeException(e);
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
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

    private ProviderMate findProviderMate(List<ProviderMate> providerMates,String methodSign) {
        Optional<ProviderMate> first = providerMates.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return first.orElse(null);
    }


}
