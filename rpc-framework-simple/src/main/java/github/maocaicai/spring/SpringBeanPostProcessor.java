package github.maocaicai.spring;

import github.maocaicai.annotation.RpcReference;
import github.maocaicai.annotation.RpcService;
import github.maocaicai.config.RpcServiceConfig;
import github.maocaicai.extension.ExtensionLoader;
import github.maocaicai.factory.SingletonFactory;
import github.maocaicai.provider.ServiceProvider;
import github.maocaicai.provider.impl.ZkServiceProviderImpl;
import github.maocaicai.proxy.RpcClientProxy;
import github.maocaicai.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * call this method before creating the bean to see if the class is annotated
 *
 * @author shuang.kou
 * @createTime 2020年07月14日 16:42:00
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    /**
     * 重写before方法，在初始化前通过获得有注解的类及注解中的信息，在zookeeper中发布服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            /**
             * 发布服务到zookeeper
             */
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     * 重写After类，在初始化后获取存在RpcReference注解的类及注解中的信息，根据此通过反射创建客户端代理类
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            /**
             * 获取存在RpcReference注解的类，然后将注解中标注的版本和组号设置进RpcServiceConfig
             */
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                /**
                 * 通过反射为属性赋值
                 */
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    /**
                     * 这一步相当于创建了客户端代理类，把该类赋值到了bean里
                     */
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
