package cn.yejj.yejjrpc.core.consumer;

import cn.yejj.yejjrpc.core.annotation.YejjConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;

    private Map<String, Object> stubMap = new HashMap<>();

    public void start() {
        //Map<String, Object> providers = applicationContext.getBeansWithAnnotation(YejjConsumer.class);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.forEach(field -> {
                try {
                    Class<?> type = field.getType();
                    //获取全限定名
                    String intfName = type.getCanonicalName();
                    Object o = stubMap.get(intfName);
                    if (o == null) {
                        o = createConsumer(type);
                    }
                    field.setAccessible(true);
                    field.set(bean, o);
                } catch (Exception e) {
                    //TODO 异常处理
                    e.printStackTrace();
                }
            });
        }
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(),new Class[]{service},new YeJJInvocationHandler(service));
    }

    private List<Field> findAnnotatedField(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        while (clazz != null){
            Field[] fields = clazz.getDeclaredFields();
            //TODO 改成Stream
            for (Field field : fields) {
                if (field.isAnnotationPresent(YejjConsumer.class)) {
                    result.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return result;
    }

}
