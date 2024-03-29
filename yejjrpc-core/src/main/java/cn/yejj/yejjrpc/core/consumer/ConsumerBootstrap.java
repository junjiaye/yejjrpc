package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.annotation.YejjConsumer;
import cn.yejj.yejjrpc.core.api.*;
import cn.yejj.yejjrpc.core.mate.InstanceMata;
import cn.yejj.yejjrpc.core.mate.ServiceMeta;
import cn.yejj.yejjrpc.core.utils.MethodUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    ApplicationContext applicationContext;
    Environment environment;
    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;
    @Value("${app.retrys}")
    private String retrys;
    @Value("${app.timeout}")
    private String timeout;
    private Map<String, Object> stubMap = new HashMap<>();

    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoaderBalacer loaderBalacer = applicationContext.getBean(LoaderBalacer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();
        RpcContext rpcContext = new RpcContext();
        rpcContext.setLoaderBalacer(loaderBalacer);
        rpcContext.setRouter(router);
        rpcContext.setFilters(filters);
        rpcContext.getParameters().put("retrys",retrys);
        rpcContext.getParameters().put("timeout",timeout);
        //String[] providers = urls.split(",");
        //获取spring中所有bean的名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        //加了YejjConsumer注解的对象，创建动态代理类
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(),YejjConsumer.class);
            fields.forEach(field -> {
                try {
                    Class<?> service = field.getType();
                    //获取全限定名
                    String intfName = service.getCanonicalName();
                    Object consumer = stubMap.get(intfName);
                    if (consumer == null) {
                        consumer = createConsumerFromRc(service,rpcContext,rc);
                        stubMap.put(intfName,consumer);
                                //createConsumer(type,rpcContext,List.of(providers));
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception e) {
                    //TODO 异常处理
                    e.printStackTrace();
                }
            });
        }
    }

    private Object createConsumerFromRc(Class<?> service, RpcContext rpcContext, RegistryCenter rc) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service.getCanonicalName()).build();
        List<InstanceMata> providers = rc.fetchAll(serviceMeta);
        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service,rpcContext,providers);
    }

    private Object createConsumer(Class<?> service,
                                  RpcContext rpcContext,
                                  List<InstanceMata> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),new Class[]{service},new CustomInvocationHandler(service,rpcContext,providers));
    }

}
