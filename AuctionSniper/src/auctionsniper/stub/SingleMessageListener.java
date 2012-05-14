package auctionsniper.stub;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class SingleMessageListener implements MessageListener {
	private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);
	
	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}
	
	public void receivesAMessage() throws InterruptedException {
		assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
	}
}
