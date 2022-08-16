package entity;

import lombok.*;

/**
 * @author maocaicai
 * 客户端请求类
 * since 2022/8/14
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}
