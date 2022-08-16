package github.maocaicai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author maocaicai
 * since 2022/8/16 10:31
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    SUCCESS(200, "远程调用成功！"),
    FAIL(500,"远程调用失败！");
    private final int code;
    private final String message;
}
