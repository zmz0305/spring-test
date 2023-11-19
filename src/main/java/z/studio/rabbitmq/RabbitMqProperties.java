package z.studio.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("storage")
@Component
public class RabbitMqProperties {

    public String getQueueName() {
        return "hello";
    }
}
