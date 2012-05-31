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

import java.util.Set;

/**
 * <p>Title: PatternReply</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.PatternReply</code></p>
 */
public class PatternReply extends MessageReply {
	/** The pattern of the subscription */
	private final String pattern;
	
	/**
	 * Creates a new PatternReply
	 * @param message The received message
	 * @param channel The originating channel
	 * @param pattern The pattern of the subscription
	 */
	protected PatternReply(String message, String channel, String pattern) {
		super(message, channel);
		this.pattern = pattern;

	}
	
	/**
	 * Publishes the message to the passed listeners
	 * @param listeners The listeners to publish to
	 */
	@Override
	public void publish(Set<SubListener> listeners) {
		for(SubListener listener: listeners) {
			listener.onPatternMessage(pattern, channel, message);
		}
	}
	
	/**
	 * Returns the pattern of the subscription
	 * @return the pattern of the subscription
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Indicates if this reply is a pattern reply
	 * @return true if this reply is a pattern reply, false if it is a channel reply
	 */
	@Override
	public boolean isPattern() {
		return true;
	}
	
	/**
	 * Indicates if this reply is a channel reply
	 * @return true if this reply is a channel reply, false if it is a pattern reply
	 */
	@Override
	public boolean isChannelMessage() {
		return false;
	}
	
	/**
	 * Constructs a <code>String</code> with all attributes in <code>name:value</code> format.
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
	    final String TAB = "\n\t";
	    StringBuilder retValue = new StringBuilder();    
	    retValue.append("MessageReply [")
			.append(TAB).append("channel:").append(this.channel)    
			.append(TAB).append("pattern:").append(this.pattern)
			.append(TAB).append("message:").append(this.message)	
	    	.append("\n]");    
	    return retValue.toString();
	}
	
	
}
