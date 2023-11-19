package z.studio.rabbitmq;

public interface IRabbitMqService {
    void init();
    void send(String message);
    String receive();
}
