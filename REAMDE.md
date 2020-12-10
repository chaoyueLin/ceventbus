#CEventBus消息总线
[Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus](https://tech.meituan.com/2018/07/26/android-livedatabus.html)
[Android组件化方案及组件消息总线modular-event实战](https://tech.meituan.com/2018/12/20/modular-event.html)

美团文章中组件化通信消息总线设计要注意的问题和实现的思路都已经讲得很清楚，modular-event没有开源代码，本示例是对齐方案的实现，需要提下原文中几点

## 消息总线约束
我们希望消息总线框架有以下约束：

* 只能订阅和发送在组件中预定义的消息。换句话说，使用者不能发送和订阅临时消息。
* 消息的类型需要在定义的时候指定。
* 定义消息的时候需要指定属于哪个组件。

## 如何实现这些约束
* 在消息定义文件上使用注解，定义消息的类型和消息所属Module。
* 定义注解处理器，在编译期间收集消息的相关信息。
* 在编译器根据消息的信息生成调用时需要的interface，用接口约束消息发送和订阅。
* 运行时构建基于两级HashMap的LiveData存储结构。
* 运行时采用interface+动态代理的方式实现真正的消息订阅和发送。

## 美团消息总线modular-event的结构
* modular-event-base：定义Anotation及其他基本类型
* modular-event-core：modular-event核心实现
* modular-event-compiler：注解处理器
* modular-event-plugin：Gradle Plugin

其中modular-event-plugin主要用于扫描所有module的自定义task，这个实现网上有很多示例，所以不在这里说明，示例中仍然有其他三个模块。也有使用module通信的示例。

## ceventbus的结构
* base：定义Anotation及其他基本类型
* core：event核心实现
* compiler：注解处理器

## 使用
使用前需要先make，生成接口类就可以直接使用,@EventType注解的常量内容直接翻译为接口方法名，内容因为可以重复，无法保证方法名不重复，需要用户自己注意，也可以自己改为使用常量，就可以避免

	@ModuleEvents(module = "common_second")
	public class SecondEvents {
	    @EventType(SecondTestBean.class)
	    public static final String TEST = "test";//test会变成方法名
	
	    @EventType()
	    public static final String TEST2 = "test2";
	}

生成后接口类如下
![](./eventDefineOfSecondEvents.png)

具体使用：

	SecondTestBean bean=new SecondTestBean();
    bean.v="ad";
    bean.id=2;
    CEventBus.of(EventsDefineOfSecondEvents.class).test().setValue(bean);