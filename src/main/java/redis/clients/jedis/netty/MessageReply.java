/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package redis.clients.jedis.netty;

import java.util.ArrayList;
import java.util.Set;

/**
 * <p>Title: MessageReply</p>
 * <p>Description: A Redis PubSub event</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.MessageReply</code></p>
 */
public class MessageReply {
	/** The message */
	protected final String message;
	/** The originating channel */
	protected final String channel;
	
	/** The leading message indicating a channel message */
	public static final String MESSAGE = "message";
	/** The leading message indicating a pattern message */
	public static final String PMESSAGE = "pmessage";
	
	/**
	 * Creates a new MessageReply from the passed array list of bytes
	 * @param decodedReply An array of byte arrays
	 * @return a MessageReply
	 */
	public static MessageReply create(ArrayList<byte[]> decodedReply) {
		
		switch (decodedReply.size()) {
			case 4:
				return new PatternReply(new String(decodedReply.get(3)), new String(decodedReply.get(2)), new String(decodedReply.get(1)));
			case 3:
				return new MessageReply(new String(decodedReply.get(2)), new String(decodedReply.get(1)));				
			default:
				throw new RuntimeException("Invalid numnber of byte arrays in decoded response [" + decodedReply.size() + "]");
		}
	}

	/**
	 * Creates a new MessageReply
	 * @param message The received message
	 * @param channel The originating channel
	 */
	protected MessageReply(String message, String channel) {
		this.message = message;
		this.channel = channel;
	}
	
	/**
	 * Publishes the message to the passed listeners
	 * @param listeners The listeners to publish to
	 */
	public void publish(Set<SubListener> listeners) {
		for(SubListener listener: listeners) {
			listener.onChannelMessage(channel, message);
		}
	}

	/**
	 * Returns the received message
	 * @return the received message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the originating channel
	 * @return the originating channel
	 */
	public String getChannel() {
		return channel;
	}
	
	/**
	 * Indicates if this reply is a pattern reply
	 * @return true if this reply is a pattern reply, false if it is a channel reply
	 */
	public boolean isPattern() {
		return false;
	}
	
	/**
	 * Indicates if this reply is a channel reply
	 * @return true if this reply is a channel reply, false if it is a pattern reply
	 */
	public boolean isChannelMessage() {
		return true;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in <code>name:value</code> format.
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
	    final String TAB = "\n\t";
	    StringBuilder retValue = new StringBuilder();    
	    retValue.append("MessageReply [")
			.append(TAB).append("channel:").append(this.channel)    
			.append(TAB).append("message:").append(this.message)		    
	    	.append("\n]");    
	    return retValue.toString();
	}
	
	
}
