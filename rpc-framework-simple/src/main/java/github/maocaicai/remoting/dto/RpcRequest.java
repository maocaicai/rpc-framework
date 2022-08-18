package github.maocaicai.remoting.dto;

import lombok.*;

/**
 * @author maocaicai
 * since 2022/8/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest {
    private static final long serialVersionUID = 1905122041950251207L;
    //请求id
    private String requestId;
    //接口名
    private String interfaceName;
    //方法名
    private String methodName;
    //参数值
    private Object[] parameters;
    //参数类型
    private Class<?>[] paramTypes;
    //当前版本
    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

}
