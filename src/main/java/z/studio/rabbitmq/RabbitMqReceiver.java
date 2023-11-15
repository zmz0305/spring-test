package z.studio.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitMqReceiver {
    private final String queueName;
    private final String host;

    private final ConnectionFactory factory = new ConnectionFactory();

    public RabbitMqReceiver(String queuename, String host) throws Exception {
        this.queueName = queuename;
        this.host = host;
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
    }
}
