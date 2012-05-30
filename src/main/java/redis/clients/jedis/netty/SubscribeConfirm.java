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
import java.util.Arrays;

/**
 * <p>Title: SubscribeConfirm</p>
 * <p>Description: A confirmation event for subscription and psubscription events</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.SubscribeConfirm</code></p>
 */
public class SubscribeConfirm {
	protected final String name;
	protected final boolean channel;
	
	/** The bytes sequence of the first array for a subscribe confirm */
	public static final byte[] SUBSCRIBE = "subscribe".getBytes();
	/** The bytes sequence of the first array for a psubscribe confirm */
	public static final byte[] PSUBSCRIBE = "psubscribe".getBytes();
	
	/**
	 * Creates a new SubscribeConfirm
	 * @param decodedReply The decoded byte array list
	 * @return the new SubscribeConfirm 
	 */
	public static SubscribeConfirm create(ArrayList<byte[]> decodedReply) {
		return new SubscribeConfirm(decodedReply);
	}
	
	/**
	 * Creates a new SubscribeConfirm
	 * @param decodedReply The decoded byte array list
	 */
	protected SubscribeConfirm(ArrayList<byte[]> decodedReply) {
		name = new String(decodedReply.get(1));
		if(Arrays.equals(decodedReply.get(0), SUBSCRIBE)) {
			channel = true;
		} else if(Arrays.equals(decodedReply.get(0), PSUBSCRIBE)) {
			channel = false;
		} else {
			throw new RuntimeException("Invalid first array value of [" + new String() + "] for Subscribe Confirm");
		}
	}
	
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the channel
	 */
	public boolean isChannel() {
		return channel;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in <code>name:value</code> format.
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
	    final String TAB = "\n\t";
	    StringBuilder retValue = new StringBuilder();    
	    retValue.append("SubscribeConfirm [")
		    .append(TAB).append("name:").append(this.name)
		    .append(TAB).append("channel:").append(this.channel)
	    	.append("\n]");    
	    return retValue.toString();
	}
}
