package github.maocaicai.remoting.transport.netty.codec;

import github.maocaicai.compress.Compress;
import github.maocaicai.enums.CompressTypeEnum;
import github.maocaicai.enums.SerializationTypeEnum;
import github.maocaicai.extention.ExtensionLoader;
import github.maocaicai.remoting.constants.RpcConstants;
import github.maocaicai.remoting.dto.RpcMessage;
import github.maocaicai.remoting.dto.RpcRequest;
import github.maocaicai.remoting.dto.RpcResponse;
import github.maocaicai.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p>
 *
 * @author maocaicai
 * since 2022/8/17 11:51
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */

/**
 * LengthFieldBasedFrameDecoder是一个基于长度的解码器，用于解决TCP拆包和粘包问题。
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder{
    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code是4B, version是1B，然后是full length。所以值是5
        // lengthFieldLength:全长为4B。所以值是4
        // lengthAdjustment: full length包括所有数据，之前读取9个字节，因此左侧长度为(fullLength-9)。所以值是-9
        // initialbyteststrip:我们将手动检查magic code和版本，所以不要剥离任何字节。所以值是0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }

        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        /**
         * 解码前先判断消息是否符合协议标准
         */
        // note: must read ByteBuf in order
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        // build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        /**
         * 生成请求消息体，用来存放解码后的消息
         */
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        /**
         * 如果是心跳检测请求体，data内容设为PING
         */
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        /**
         * 如果是心跳检测响应体，data内容设为PONG
         */
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        /**
         * 否则是请求体，且body长度>0，此时要解码并读取内容，并返回一个消息体
         */
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // decompress the bytes
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            /**
             * 解压内容
             */
            bs = compress.decompress(bs);
            // deserialize the object
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}] ", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            /**
             * 如果是请求类型
             */
            if (messageType == RpcConstants.REQUEST_TYPE) {
                /**
                 * 对内容反序列化
                 */
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                /**
                 * 把反序列化后内容设置进消息体中
                 */
                rpcMessage.setData(tmpValue);
            } else {
                /**
                 * 如果是响应类型
                 */
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
