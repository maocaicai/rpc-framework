package github.maocaicai.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取单例对象的工厂类
 * 这里使用了concurrentHashMap保证了线程安全
 * @author maocaicai
 * since 2022/8/17 11:18
 */
public final class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {}

    public static <T> T getInstance(Class<T> c) {
        if (c == null) throw new IllegalArgumentException();
        String key = c.toString();
        if (OBJECT_MAP.containsKey(key)) {
            //将对象强制转换为由这个class对象表示的类或接口
            return c.cast(OBJECT_MAP.get(key));
        }else {
            /**
             *不存在，则返回一个实例，computeIfAbsent()方法表示如果存在，直接返回值，不存在则创建一个新的
             */
            return c.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
