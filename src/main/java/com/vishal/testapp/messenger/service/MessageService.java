package com.vishal.testapp.messenger.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.vishal.testapp.messenger.database.DatabaseClass;
import com.vishal.testapp.messenger.exception.DataNotFoundException;
import com.vishal.testapp.messenger.model.Comment;
import com.vishal.testapp.messenger.model.Message;

public class MessageService {
	
	private Map<Long, Message> messages = DatabaseClass.getMessages();
	
	public MessageService() {
		Message sampleMessage = new Message(1L,"Hello Message 1","Vishal");
		messages.put(1L, sampleMessage);
		messages.get(1L).getComments().put(1L, new Comment(1, "Sample Comments", "vishal"));
		messages.put(2L, new Message(2L,"Hello Message 2","Vishal"));
	}
	public List<Message> getAllMessages(){
		return new ArrayList<Message>(messages.values());
	}
	
	public List<Message> getAllMessagesForYear(int year){
		List<Message> messagesForYear = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		for(Message message:  messages.values()) {
			cal.setTime(message.getCreated());
			if(cal.get(Calendar.YEAR) == year){
				messagesForYear.add(message);
			}
		}
		return messagesForYear;
	}
	
	public List<Message> getAllMessagesPaginated(int start, int size){
		ArrayList<Message> messageList = new ArrayList<Message>(messages.values());
		if(start >= messageList.size())
			return new ArrayList<Message>();
		else
			return messageList.subList(start, Integer.min(start+size, messageList.size()));
	}
	
	public Message getMessage(long id) {
		Message message = messages.get(id);
		if(message == null) {
			throw new DataNotFoundException("Message with id " + id +" not found");
		}
		return message;
	}
	
	public Message addMessage(Message message) {
		message.setId(messages.size() + 1);
		messages.put(message.getId(), message);
		return message;
	}
	
	public Message updateMessage(Message message) {
		if(message.getId() <=0 ) {
			return null;
		}
		messages.put(message.getId(), message);
		return message;
	}
	
	public Message removeMessage(Long messageId) {
		return messages.remove(messageId);
	}

}
