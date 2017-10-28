package com.vishal.testapp.messenger.resources;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.vishal.testapp.messenger.model.Message;
import com.vishal.testapp.messenger.resources.beans.MessageFilterBean;
import com.vishal.testapp.messenger.service.MessageService;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(value = {MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class MessageResource {
	
	private MessageService messageService = new MessageService();
	
	@GET
	public List<Message> getMessages(@BeanParam MessageFilterBean filterBean) {
		if(filterBean.getYear() > 0) {
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if(filterBean.getStart() >0 && filterBean.getSize() >0) {
			// Subtracting 1 from start indext to convert to zero based indexing 
			return messageService.getAllMessagesPaginated(filterBean.getStart()-1, filterBean.getSize());
		}
		return messageService.getAllMessages();
	}
	
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long messageId, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(messageId);
		message.addLink(getUriForSelf(uriInfo, messageId), "self");
		message.addLink(getUriForAuthor(uriInfo, message.getAuthor()), "profile");
		message.addLink(getUriForComments(uriInfo, messageId), "comments");
		return message;
	}

	private String getUriForComments(UriInfo uriInfo, long messageId) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(MessageResource.class, "getCommentResource")
				.path(CommentResource.class)
				.resolveTemplate("messageId", messageId)
				.build()
				.toString();
	return uri;
	}

	private String getUriForAuthor(UriInfo uriInfo, String author) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(ProfileResource.class)
				.path(author)
				.build()
				.toString();
	return uri;
	}

	private String getUriForSelf(UriInfo uriInfo, long messageId) {
		String uri = uriInfo.getBaseUriBuilder()
					.path(MessageResource.class)
					.path(Long.toString(messageId))
					.build()
					.toString();
		return uri;
	}
	
	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		Message newMessage =  messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
						.entity(newMessage)
						.build();
	}
	
	@PUT
	@Path("/{messageId}")
	public Message updateMessage(@PathParam("messageId") long messageId, Message message) {
		message.setId(messageId);
		return messageService.updateMessage(message);
	}
	
	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long messageId) {
		messageService.removeMessage(messageId);
	}
	
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
}
