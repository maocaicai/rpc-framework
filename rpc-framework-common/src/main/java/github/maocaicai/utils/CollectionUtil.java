package github.maocaicai.utils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author maocaicai
 * @createTime 2022/8/17 16:00
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
