package github.maocaicai.remoting.transport.netty.client;

import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Customize the client ChannelHandler to process the data sent by the server
 *
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。详见《Netty进阶之路 跟着案例学 Netty》
 *
 * @author maocaicai
 * since 2022/8/17 11:52
 */
public class NettyRpcClientHandler {
}
