package z.studio.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitMqSender {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqSender.class);
    private final String queueName;

//    public RabbitMqSender() {
//        this.queueName = "hello";
//    }

    @Autowired
    public RabbitMqSender(RabbitMqProperties properties) {
        this.queueName = properties.getQueueName();
    }

    public void sendMsg(String message) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes());
            LOG.info(" [x] Sent '" + message + "'");
        }
    }

//    public static void main(String[] args) throws Exception {
//        RabbitMqSender instance = new RabbitMqSender();
//        instance.sendMsg("test message java client");
//    }
}
