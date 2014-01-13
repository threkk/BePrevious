package com.hva.boxlabapp.entities;

import java.util.Date;

import nl.boxlab.model.Message;


public class MessageItem extends Message {

	private static final long serialVersionUID = -5237822273341587422L;
	private int _id;
	
	public MessageItem(){
		super();
	}
	
	public MessageItem(Date date, String message) {
		super(message);
		this.setCreated(date);
		this.setUpdated(new Date());
	}

	public MessageItem(Date date, String message, boolean fromPatient) {
		super(message,fromPatient);
		this.setCreated(date);
		this.setUpdated(new Date());
	}

	public MessageItem(Message message) {
		super(message.getMessage(), message.isFromPatient());
		this.setIdentity(message.getId());
		this.setCreated(message.getCreated());
		this.setUpdated(new Date());
	}
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int id){
		this._id = id;
	}
	
	public static Message toMessage(MessageItem msg){
		Message message = new Message(msg.getMessage(), msg.isFromPatient());
		message.setCreated(msg.getCreated());
		message.setUpdated(new Date());
		message.setId(msg.getId());
		return message;
	}
}
