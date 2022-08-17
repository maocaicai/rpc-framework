package github.maocaicai.serialize;

import github.maocaicai.extention.SPI;

/**
 * 序列化接口，所有序列化类都要实现此接口
 * @author maocaicai
 * since 2022/8/17 14:16
 */
@SPI
public interface Serializer {
    /**
     *序列化
     *@param obj 被序列化对象
     *@return  字节数组
     */
    byte[] serialize(Object obj);

    /**
     *反序列化
     *@param bytes, clazz 序列化后的字节数组 目标类
     * @param <T>   类的类型。举个例子,  {@code String.class} 的类型是 {@code Class<String>}.
     *              如果不知道类的类型的话，使用 {@code Class<?>}
     *@return 反序列化对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
