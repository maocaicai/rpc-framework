package github.maocaicai.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储并获取通道对象
 * 需要创建一个Channel的线程安全Map存储并获取
 * @author maocaicai
 * since 2022/8/17 10:55
 */
@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;

    public ChannelProvider() {channelMap = new ConcurrentHashMap<>();}

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            /**
             * 如果channel不为空且当前channel是激活可连接状态，返回当前channel
             */
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                //否则说明不是激活状态，删除此channel
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("当前ChannelMap的大小 : [{}]", channelMap.size());
    }
}
