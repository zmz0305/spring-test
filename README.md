# spring-test
To bring up docker instance for MySql
```
docker run --name c1 -p 3306:3306 -e MYSQL_USER=mzhao -e MYSQL_PASSWORD=password -e MYSQL_ROOT_PASSWORD=password -e
 MYSQL_DATABASE=mydb -d mysql
```

To bring up docker instance for RabbitMq management server
```
docker run -d --name rabbitmq-test -h my-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
```