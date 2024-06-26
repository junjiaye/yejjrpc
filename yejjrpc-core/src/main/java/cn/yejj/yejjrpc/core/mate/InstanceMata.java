package cn.yejj.yejjrpc.core.mate;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: yejjrpc
 * @ClassName: InstanceMate
 * @description:
 * 实例元数据，用来表示提供服务的示例的信息
 * @author: yejj
 * @create: 2024-03-22 14:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMata {

    private String scheme;
    private String host;
    private Integer port;
    private String context;
    private boolean status;
    private Map<String,String> parameters = new HashMap<>();

    public InstanceMata(String schema, String host, Integer port, String context) {
        this.scheme = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMata http(String host, Integer port) {
        return new InstanceMata("http",host,port,"yejjrpc");
    }
    public InstanceMata addParams(Map<String, String> params) {
        this.getParameters().putAll(params);
        return this;
    }
    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
    public String toPath() {
        return String.format("%s_%d",host,port);
    }

    public String toMeta(){
        return JSON.toJSONString(this.getParameters());
    }
}
