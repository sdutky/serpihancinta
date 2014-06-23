package edu.itb.twofishsms.model;

import edu.itb.twofishsms.provider.Contact;
import edu.itb.twofishsms.provider.Message;
import edu.itb.twofishsms.provider.Recipient;

public class RecipientThread {
	
	private Recipient recipient;
	private Message lastMessage;
	
	public RecipientThread(Recipient _recipient, Message _lastMessage){
		this.recipient = _recipient;
		this.lastMessage = _lastMessage;
	}
	
	public Recipient getRecipient(){
		return this.recipient;
	}
	
	public Message getLastMessage(){
		return this.lastMessage;
	}
}
