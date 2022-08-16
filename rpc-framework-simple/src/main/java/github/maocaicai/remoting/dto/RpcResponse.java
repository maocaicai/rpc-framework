package github.maocaicai.remoting.dto;

import github.maocaicai.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @author maocaicai
 * since 2022/8/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;
    //响应代码，成功or失败
    private Integer code;
    //响应消息
    private String message;
    //响应内容
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setRequestId(requestId);
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        if (data != null) response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
