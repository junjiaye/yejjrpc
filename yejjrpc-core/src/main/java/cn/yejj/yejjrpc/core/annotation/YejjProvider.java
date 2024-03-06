package cn.yejj.yejjrpc.core.annotation;

import java.lang.annotation.*;

/**
 * @author: yejjr
 * @since: 2024-03-06
 * @description:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited   // 该注解可以被继承
public @interface YejjProvider {
}
