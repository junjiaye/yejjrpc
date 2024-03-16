package cn.yejj.yejjrpc.core.annotation;

import java.lang.annotation.*;

/**
 * @author: yejjr
 * @since: 2024-03-10
 * @description:
 */
@Documented
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface YejjConsumer {
}
