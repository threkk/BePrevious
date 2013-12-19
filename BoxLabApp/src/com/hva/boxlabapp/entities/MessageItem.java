package com.hva.boxlabapp.entities;

import java.util.Date;

import com.hva.boxlabapp.entities.client.Message;

public class MessageItem extends Message {

	private static final long serialVersionUID = -5237822273341587422L;
	private int _id;
	
	public MessageItem(){
		super();
	}
	
	public MessageItem(Date date, String message) {
		super(date,message);
	}

	public MessageItem(Date date, String message, boolean fromPatient) {
		super(date,message,fromPatient);
	}

	public MessageItem(Message message) {
		super(message.getDate(), message.getMessage(), message.isFromPatient());
	}
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int id){
		this._id = id;
	}
	
	public static Message toMessage(MessageItem msg){
		return new Message(msg.getDate(), msg.getMessage(), msg.isFromPatient());
	}
}
