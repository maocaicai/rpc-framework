# Introduction 
**本人学习Netty后决定自己写1个基于Netty、Zookeeper、Spring的轻量级RPC框架，收获颇丰，不过本人才疏学浅，难免有所疏漏，若有批评和建议请发到邮箱2239801473@qq.com**


# Features
- **支持长连接**
- **支持异步调用**
- **支持心跳检测**
- **支持多种序列化**
- **接近零配置，基于注解实现调用**
- **基于Zookeeper实现服务注册中心**
- **支持客户端连接动态管理**
- **支持客户端服务监听、发现功能**
- **支持服务端服务注册功能**
- **基于Netty实现**

# Quick Start
### 服务端开发
- **在服务端的Service下添加你自己的Service,并加上@RpcService注解**
	<pre>
	@RpcService(group = "test1", version = "version1")
	public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建");
    }
  @Override
  public String hello(Hello hello) {
    log.info("HelloServiceImpl收到: {}.", hello.getMessage());
    String result = "Hello description is " + hello.getDescription();
    log.info("HelloServiceImpl返回: {}.", result);
    return result;
    }
  }
	</pre>

- **创建服务端main类，添加注解@RpcScan，并启动rpc**
	###### 启动类如下
	<pre>
  @RpcScan(basePackage = {"github.maocaicai"})
	public class NettyServerMain {
	  public static void main(String[] args) {
	    // Register service via annotation
	    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
	    NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
	    // Register service manually
	    HelloService helloService2 = new HelloServiceImpl2();
	    RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
	    .group("test2").version("version2").service(helloService2).build();
	    nettyRpcServer.registerService(rpcServiceConfig);
	    nettyRpcServer.start();
	  }
	}
### 客户端开发
- **在客户端控制类中添加注解@RpcReference并使用**
	<pre>
  @Component
	public class HelloController {
		@RpcReference(version = "version1", group = "test1")
		private HelloService helloService;
		public void test() throws InterruptedException {
			String hello = this.helloService.hello(new Hello("111", "222"));
			//如需使用 assert 断言，需要在 VM options 添加参数：-ea
			assert "Hello description is 222".equals(hello);
			Thread.sleep(12000);
			for (int i = 0; i < 10; i++) {
				System.out.println(helloService.hello(new Hello("111", "222")));
			}
		}
	}
	</pre>

### 使用
- **创建客户端main类，添加@RpcScan注解指定扫描包，并远程调用方法**
- ###### 客户端类如下
    <pre>
  @RpcScan(basePackage = {"github.maocaicai"})
      public class NettyClientMain {
        public static void main(String[] args) throws InterruptedException {
          AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
          HelloController helloController = (HelloController) applicationContext.getBean("helloController");
          helloController.test();
        }
      }
    </pre>
### 结果
- **一万次调用结果**
![Markdown](https://s1.ax1x.com/2018/07/06/PZMMBF.png)

- **十万次调用结果**
![Markdown](https://s1.ax1x.com/2018/07/06/PZM3N9.png)

- **一百万次调用结果**
![Markdown](https://s1.ax1x.com/2018/07/06/PZMY1x.png)



# Overview

![Markdown](https://s1.ax1x.com/2018/07/06/PZK3SP.png)
