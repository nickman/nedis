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

/**
 * <p>Title: ConnectionListener</p>
 * <p>Description: Defines a listener that is notified of changes in the connectivity state of an {@link OptimizedPubSub}.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.ConnectionListener</code></p>
 */
public interface ConnectionListener {
	/**
	 * Callback when the pubSub connects
	 * @param pubSub the pubSub that connected 
	 */
	public void onConnect(OptimizedPubSub pubSub);
	/**
	 * Callback when the pubSub disconnects
	 * @param pubSub the pubSub that disconnected
	 * @param cause The exception that caused the unintended disconnect. Null if disconnect was requested 
	 */
	public void onDisconnect(OptimizedPubSub pubSub, Throwable cause);
} 