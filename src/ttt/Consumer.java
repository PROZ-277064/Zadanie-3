package ttt;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

public class Consumer implements MessageListener {
	private Game game;
	private Queue queue;
	private JMSContext jmsContext;
	private JMSConsumer jmsConsumer;

	Consumer(Game game) {
		this.game = game;
		init();
	}

	@Override
	public void onMessage(Message message) {
		if (message == null)
			return;

		System.out.println("Recived message!");
		try {
			String textMessage = message.getStringProperty("MOVE");
			if (textMessage != null) {
				System.out.println(game.selector + " MOVE " + textMessage);
				game.makeMove(Integer.parseInt(textMessage));
			} else {
				textMessage = message.getStringProperty("START");
				if (textMessage != null) {
					game.setOpponent(textMessage);
				}
				else {
					textMessage = message.getStringProperty("CLOSE");
					if (textMessage != null) {
						game.setOpponent("");
					}
				}
			}

		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("EXCEPTION: Consumer::onMessage()");
		}
	}

	public boolean checkNewGameMessage() {
		JMSConsumer consumer = jmsContext.createConsumer(queue, "TYPE = 'newgame'");
		Message message = consumer.receive(1000);
		if (message == null) {
			System.out.println("There is no New Game message!");
			// System.out.println(message);
			consumer.close();
			return false;
		}
		try {
			String textMessage = message.getStringProperty("ID");
			if (textMessage == null) {
				System.out.println("ERROR: There is no ID in New Game message!");
				consumer.close();
				return false;
			}
			// game.opponentSelector = textMessage;
			game.setOpponent(textMessage);
		} catch (JMSException e) {
			e.printStackTrace();
			consumer.close();
			return false;
		}
		consumer.close();
		return true;
	}

	public void removeNewGameMessage() {
		JMSConsumer consumer = jmsContext.createConsumer(queue, "ID = '" + game.selector + "'");
		consumer.receive(1000);
		consumer.close();
	}

	public void init() {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		jmsContext = connectionFactory.createContext();
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory)
					.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");

			queue = new com.sun.messaging.Queue("ATJQueue");
			jmsConsumer = jmsContext.createConsumer(queue, "SELECTOR = '" + game.selector + "'");

			jmsConsumer.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		jmsConsumer.close();
		jmsContext.close();
	}
}
