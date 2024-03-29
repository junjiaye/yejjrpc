package cn.yejj.yejjrpc.core.provider;

import cn.yejj.yejjrpc.core.annotation.YejjProvider;
import cn.yejj.yejjrpc.core.api.RegistryCenter;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.mate.ProviderMata;
import cn.yejj.yejjrpc.core.mate.ServiceMeta;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Slf4j
@Data
//bean注册完成后进行处理
public class ProviderBootstrap implements ApplicationContextAware {
    //字段set方法，相当于实现了接口的set....方法
    ApplicationContext applicationContext;

    private InstanceMata instance;
    RegistryCenter registry;

    ServiceMeta serviceMeta;

    @Value("${server.port}")
    private String port;
    @Value("${app.env}")
    private String env;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.id}")
    private String app;
    //MultiValueMap:spring提供的，key可以重复，可以用来存储类似爱好:篮球，爱好：看书 这样的结构，表示我的爱好是看书和篮球
    private MultiValueMap<String, ProviderMata> skeletonMap = new LinkedMultiValueMap<>();

    @PostConstruct  //bean创建后。未初始化之前执行 相当于init-method 对应的销毁方法是@PreDestroy
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(YejjProvider.class);
        registry = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((k, v) -> log.info("provider: " + k));
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMata.http(ip,Integer.valueOf(port));
        registry.start();
        skeletonMap.keySet().forEach(this::registService);
    }

    //销毁前反注册zk里的节点
    @PreDestroy
    public void stop(){
        skeletonMap.keySet().forEach(this::unRegisterService);
        registry.stop();
    }

    private void unRegisterService(String service) {
        RegistryCenter registry = applicationContext.getBean(RegistryCenter.class);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        registry.unRegister(serviceMeta,instance);
    }

    private void registService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        registry.register(serviceMeta,instance);
    }


    private void genInterface(Object x){
        Arrays.stream(x.getClass().getInterfaces()).forEach(intf -> {
            Method[] methods = intf.getMethods();
            for (Method method : methods){
                //检查是否是Object的默认方法
                if(MethodUtils.checkLocalMethod(method)){
                    continue;
                }
                createProviderMeta(intf,x,method);
            }
        });
    }

    private void createProviderMeta(Class<?> service, Object impl, Method method) {
        ProviderMata providerMata = new ProviderMata();
        providerMata.setMethod(method);
        providerMata.setMethodSign(MethodUtils.methodSign(method));
        providerMata.setServiceImpl(impl);
        log.info(providerMata.toString());
        //以接口的全限定类名作为key，缓存接口中的方法签名信息
        skeletonMap.add(service.getCanonicalName(), providerMata);
    }


}
