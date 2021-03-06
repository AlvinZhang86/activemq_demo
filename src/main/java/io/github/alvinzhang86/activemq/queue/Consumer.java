package io.github.alvinzhang86.activemq.queue;

import io.github.alvinzhang86.activemq.Config;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * Created by zhangshuang on 16/3/22.
 */
@SuppressWarnings("Duplicates")
public class Consumer {

    private static final Boolean NON_TRANSACTED = false;

    private static final long TIMEOUT = 200000;


    public static void main(String[] args) {
        String url = Config.BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        System.out.println("waiting... timeout after(s) " + TIMEOUT / 1000);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PASSWORD, url);

        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
//            Session session = connection.createSession(NON_TRANSACTED,Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue("test-queue");
            MessageConsumer consumer = session.createConsumer(destination);

            int i = 0;

            while (true) {
                Message message = consumer.receive(TIMEOUT);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message: " + text);
                    }
                } else {
                    break;
                }

            }
            consumer.close();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
