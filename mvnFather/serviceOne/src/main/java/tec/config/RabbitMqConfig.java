//package tec.config;
//
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
//import org.springframework.amqp.support.converter.MessageConversionException;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.ByteArrayInputStream;
//import java.io.ObjectInputStream;
//
//@Configuration
//@RefreshScope
//public class RabbitMqConfig {
//    @Bean("directQueue")
//    public Queue directQueue(){
//        // 参数介绍
//        // 1.队列名 2.是否持久化 3.是否独占 4.自动删除 5.其他参数
//        return new Queue("directQueue-One",true,false,false,null);
//    }
//
//    @Bean("directQueue2")
//    public Queue directQueue2(){
//        return new Queue("directQueue-Two",true,false,false,null);
//    }
//
//    @Bean
//    public DirectExchange directExchange(){
//        // 参数介绍
//        // 1.交换器名 2.是否持久化 3.自动删除 4.其他参数
//        return new DirectExchange("DirectExchange-One",true,false,null);
//    }
//
//    @Bean
//    public Binding bingExchange(DirectExchange directExchange,Queue directQueue){
//        // 绑定队列
//        return BindingBuilder.bind(directQueue)
//                // 队列绑定到哪个交换器
//                .to(directExchange)
//                // 绑定路由key,必须指定
//                .with("One");
//    }
//    @Bean
//    public Binding bingExchange2(DirectExchange directExchange,Queue directQueue2){
//        // 绑定队列
//        return BindingBuilder.bind(directQueue2)
//                // 队列绑定到哪个交换器
//                .to(directExchange)
//                // 绑定路由key,必须指定
//                .with("Two");
//    }
//
//    @Bean
//    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new MessageConverter() {
//            @Override
//            public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
//                return null;
//            }
//
//            @Override
//            public Object fromMessage(Message message) throws MessageConversionException {
//                try{
//                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(message.getBody()));
//                    return (JSONObject)ois.readObject();
//                }catch (Exception e){
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        });
//
//        return factory;
//    }
//
//}
