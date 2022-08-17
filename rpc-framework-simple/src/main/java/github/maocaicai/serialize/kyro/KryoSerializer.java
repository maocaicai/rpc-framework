package github.maocaicai.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.maocaicai.exception.SerializeException;
import github.maocaicai.remoting.dto.RpcRequest;
import github.maocaicai.remoting.dto.RpcResponse;
import github.maocaicai.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author maocaicai
 * since 2022/8/17 14:21
 */
public class KryoSerializer implements Serializer {
    /**
     *Kryo线程不安全，因此需要使用一个ThreadLocal存储Kryo对象
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    /**
     * 序列化：将Object类型转化为字节，输出方向，需要写出去
     * @param obj 被序列化对象
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            //将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    /**
     * 反序列化：将字节转化为目标类的类型，输入方向，需要读入
     * @param bytes, clazz 序列化后的字节数组 目标类
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            /**
             * 将Object类强转为需要的类
             */
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
