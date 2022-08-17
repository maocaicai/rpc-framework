package github.maocaicai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author maocaicai
 * @createTime on 2022/8/17 12:00
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
