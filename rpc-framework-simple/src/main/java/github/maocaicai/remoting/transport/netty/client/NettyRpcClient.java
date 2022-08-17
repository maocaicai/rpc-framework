package github.maocaicai.remoting.transport.netty.client;

import github.maocaicai.enums.CompressTypeEnum;
import github.maocaicai.enums.SerializationTypeEnum;
import github.maocaicai.extention.ExtensionLoader;
import github.maocaicai.factory.SingletonFactory;
import github.maocaicai.registry.ServiceDiscovery;
import github.maocaicai.remoting.constants.RpcConstants;
import github.maocaicai.remoting.dto.RpcMessage;
import github.maocaicai.remoting.dto.RpcRequest;
import github.maocaicai.remoting.dto.RpcResponse;
import github.maocaicai.remoting.transport.RpcRequestTransport;
import github.maocaicai.remoting.transport.netty.codec.RpcMessageDecoder;
import github.maocaicai.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 初始化客户端
 * @author maocaicai
 * since 2022/8/16 11:01
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    /**
     * 1、未处理完的请求存储类
     * 2、提供通道的类
     * 3、netty核心设置类
     * 4、eventLoopGroup：相当于一个线程池，每一个EventLoop都相当于一个线程，Netty所做的就是将这个线程池有效的调度起来。
     * 5、服务发现类，用来发现存储在zk中的服务端地址
     */
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ServiceDiscovery serviceDiscovery;
    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);

    }

    /**
     * 连接服务器获得channel，从而发送rpc消息给服务器
     * @param inetSocketAddress
     * @return 返回channel通道
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接 [{}] 成功！", inetSocketAddress);
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    /**
     * 重写发送请求，实现逻辑
     * @param rpcRequest 消息请求
     * @return 返回服务器响应
     */
    @Override
    public Object senRpcRequset(RpcRequest rpcRequest) {
        /**
         * 构建返回的结果，即响应体
         */
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            /**
             * 因为在Netty中操作是异步的，ch.writeAndFlush(message)也是异步的，该方法只是把发送消息加入了任务队列，
             * 这时直接关闭连接会导致问题。所以我们需要在消息发送完毕后在去关闭连接。通过ChannelFuture我们可以添加Listener，
             * 那么在消息发送完成后会进行回调，我们再去处理关闭连接等业务逻辑。
             */
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                //Returns true if and only if the I/O operation was completed successfully.
                if (future.isSuccess()) {
                    log.info("客户端发送消息 : [{}]", rpcMessage);
                } else {
                    /**
                     * 请求关闭Channel，并在操作完成后通知ChannelFuture，
                     * 原因可能是操作成功，也可能是出现错误。关闭它之后，就不可能再重用它了。
                     */
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送失败:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            /**
             * 为空说明之前没连接得到过channel，那么先连接
             */
            channel = doConnect(inetSocketAddress);
            /**
             * 然后把channel设置进channelProvider中，供后面使用
             */
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
