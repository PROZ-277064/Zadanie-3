package ttt;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;

public class Consumer {
	public String receiveWelcomeMessage() {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		JMSContext jmsContext = connectionFactory.createContext();
		String msg ="";
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory).setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,"localhost:7676/jms");
			
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
			System.out.println("Konsument czeka na wiadomość");
			
			//while (( msg = jmsConsumer.receiveBody(String.class,10)) != null) {
				msg = jmsConsumer.receiveBody(String.class,10);
				System.out.printf("Odebrano wiadomość: '%s'\n", msg);
			//}
			jmsConsumer.close();
			System.out.println("Konsument zakończył odbiór.");
		}
		catch (JMSException e) { e.printStackTrace(); }
		jmsContext.close();
		return msg;
	}
	
	public int receiveQueueMessages() {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		JMSContext jmsContext = connectionFactory.createContext();
		String msg ="";
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory).setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,"localhost:7676/jms");
			
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
			System.out.println("Konsument czeka na wiadomość");
			
			while( (msg = jmsConsumer.receiveBody(String.class,10)) == null );
				System.out.printf("Odebrano wiadomość: '%s'\n", msg);
			jmsConsumer.close();
			System.out.println("Konsument zakończył odbiór.");
		}
		catch (JMSException e) { e.printStackTrace(); }
		jmsContext.close();
		if( msg.toString().equals("Hello"))
			return 42;
		return Integer.parseInt(msg);
	}
}
