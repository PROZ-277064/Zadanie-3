package ttt;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;

public class Producer {
	public void sendWelcomeMessage() {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");
			JMSContext jmsContext = connectionFactory.createContext();
			JMSProducer jmsProducer = jmsContext.createProducer();
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			String msg = "Hello";
			jmsProducer.send(queue, msg);
			System.out.printf("Wiadomość '%s' została wysłana.\n", msg);

			jmsContext.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendQueueMessage(int position) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");
			JMSContext jmsContext = connectionFactory.createContext();
			JMSProducer jmsProducer = jmsContext.createProducer();
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			String msg = "" + position;
			jmsProducer.send(queue, msg);
			System.out.printf("Wiadomość '%s' została wysłana.\n", msg);

			jmsContext.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
