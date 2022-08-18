package github.maocaicai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author maocaicai
 * @createTime on 2022/8/17 11:35
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    /**
     * 目前使用的Kyro序列化
     */
    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    /**
     * 不要用HESSIAN序列化，用不了，序列化失败的
     */
    HESSIAN((byte) 0X03, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
