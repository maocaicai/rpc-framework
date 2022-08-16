package entity;

import lombok.*;

/**
 * @author maocaicai
 * 服务端响应类
 * since 2022/8/15
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class RpcResponse {
    private String message;
}
