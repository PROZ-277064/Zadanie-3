package ttt;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

public class Producer {

	private Game game;
	private Queue queue;
	private JMSContext jmsContext;
	private JMSProducer jmsProducer;

	Producer(Game game) {
		this.game = game;

		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");

			jmsContext = connectionFactory.createContext();
			jmsProducer = jmsContext.createProducer();
			queue = new com.sun.messaging.Queue("ATJQueue");

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	void closeConnection() {
		jmsContext.close();
	}

	void sendMove(int position) {
		try {
			Message msg = jmsContext.createMessage();
			msg.setStringProperty("SELECTOR", game.opponentSelector);
			msg.setStringProperty("MOVE", "" + position);
			
			jmsProducer.send(queue, msg);
			System.out.printf("Message '%s' has been send.\n", msg);
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Exception on: Producer::sendMove()");
		}
	}

	void sendNewGame() {
		try {
			Message msg = jmsContext.createMessage();
			msg.setStringProperty("ID", game.selector);
			msg.setStringProperty("TYPE", "newgame");
			
			jmsProducer.send(queue, msg);
			System.out.printf("Message '%s' has been send.\n", msg);
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Exception on: Producer::sendMove()");
		}
	}
	
	void sendStart() {
		try {
			Message msg = jmsContext.createMessage();
			msg.setStringProperty("SELECTOR", game.opponentSelector);
			msg.setStringProperty("START", "" + game.selector);
			
			jmsProducer.send(queue, msg);
			System.out.printf("Message '%s' has been send.\n", msg);
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Exception on: Producer::sendStart()");
		}
	}
	
	void sendClose() {
		if( game.opponentSelector.equals("") )
			return;
		
		try {
			Message msg = jmsContext.createMessage();
			msg.setStringProperty("SELECTOR", game.opponentSelector);
			msg.setStringProperty("CLOSE", "" + game.selector);
			
			jmsProducer.send(queue, msg);
			System.out.printf("Message '%s' has been send.\n", msg);
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Exception on: Producer::sendClose()");
		}
	}
}
