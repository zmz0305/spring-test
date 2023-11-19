package z.studio.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class SimpleRabbitMqService implements IRabbitMqService {

private static final Logger LOG = LoggerFactory.getLogger(SimpleRabbitMqService.class);
    private final RabbitMqSender sender;
    private final RabbitMqReceiver receiver;

    @Autowired
    public SimpleRabbitMqService(RabbitMqSender sender, RabbitMqReceiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void init() {

    }

    @Override
    public void send(String message) {
        try {
            this.sender.sendMsg(message);
        } catch (IOException | TimeoutException e) {
            LOG.error("Failed to send message {} to RabbitMq", message, e);
        }
    }

    @Override
    public String receive() {
        return null;
    }
}
