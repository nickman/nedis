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

import org.jboss.netty.channel.ChannelFuture;


/**
 * <p>Title: PubSub</p>
 * <p>Description: PubSub interface to Redis</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.PubSub</code></p>
 */
public interface PubSub {
	/**
	 * Subscribes to the passed channels
	 * @param channels The channels to subscribe to
	 * @return A channelFuture for the completion of the operation
	 */
	public ChannelFuture subscribe(String...channels);
	/**
	 * Unsubscribes from the passed channels
	 * @param channels The channels to unsubscribe from
	 * @return A channelFuture for the completion of the operation
	 */
	public ChannelFuture unsubscribe(String...channels);
	/**
	 * Subscribes to the passed patterns
	 * @param patterns The patterns to subscribe to
	 * @return A channelFuture for the completion of the operation
	 */	
	public ChannelFuture psubscribe(String...patterns);
	/**
	 * Unsubscribes from the passed patterns
	 * @param patterns The patterns to unsubscribe from
	 * @return A channelFuture for the completion of the operation
	 */
	public ChannelFuture punsubscribe(String...patterns);
	
	/**
	 * Publishes the passed messages to the passed channel
	 * @param channel The channel to publish to
	 * @param messages The messages to publish
	 * @return A channel future reflecting the completion of the publishes
	 */
	public ChannelFuture  publish(String channel, String...messages);
	
	/**
	 * Registers a subscription listener
	 * @param listener The listener to register
	 */
	public void registerListener(SubListener listener);
	/**
	 * Unregisters a subscription listener
	 * @param listener The listener to unregister
	 */	
	public void unregisterListener(SubListener listener);

}
