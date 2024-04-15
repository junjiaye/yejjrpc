package cn.yejj.yejjrpc.core.mate;

import lombok.Builder;
import lombok.Data;

/**
 * @program: yejjrpc
 * @ClassName: ServiceMate
 * @description:
 * 描述服务的元数据（暴露的接口类的信息，没有到方法级）
 * @author: yejj
 * @create: 2024-03-22 15:14
 */
@Data
@Builder
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;



    public String toPath() {
        return String.format("%s_%s_%s_%s",app,namespace,env,name);
    }
}
