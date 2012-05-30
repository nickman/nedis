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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: OptimizedPubSub</p>
 * <p>Description: A Netty NIO based pub sub subscriber.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.OptimizedPubSub</code></p>
 */

public class OptimizedPubSub {
	/** The redis host or IP Address */
	protected final String host;
	/** The redis listening port */
	protected final int port;
	/** The redis auth */
	protected final String auth;
	
	/** A map of pubsub instances */
	private static final Map<String, OptimizedPubSub> instances = new ConcurrentHashMap<String, OptimizedPubSub>();
	
	/**
	 * Returns an OptimizedPubSub for the passed host and port
	 * @param host The redis host
	 * @param port The redis port
	 * @param auth The redis auth password
	 * @return An {@link OptimizedPubSub} instance
	 */
	
	public static OptimizedPubSub getInstance(String host, int port, String auth) {
		String key = host + ":" + port;
		OptimizedPubSub ops = instances.get(key);
		if(ops==null) {
			synchronized(instances) {
				ops = instances.get(key);
				if(ops==null) {
					ops = new OptimizedPubSub(host, port, auth);
					instances.put(key, ops);
				}
			}
		}
		return ops;
	}
	
	/**
	 * Returns an OptimizedPubSub for the passed host and port
	 * @param host The redis host
	 * @param port The redis port
	 * @return An {@link OptimizedPubSub} instance
	 */
	
	public static OptimizedPubSub getInstance(String host, int port) {
		return getInstance(host, port, null);
	}
	
	
	
	/**
	 * Creates a new OptimizedPubSub
	 * @param host The redis host
	 * @param port The redis port
	 * @param auth The redis auth password
	 */
	private OptimizedPubSub (String host, int port, String auth) {
		this.host = host;
		this.port = port;
		this.auth = auth;
	}
	
	/**
	 * Creates a new OptimizedPubSub
	 * @param host The redis host
	 * @param port The redis port
	 */
	private OptimizedPubSub (String host, int port) {
		this(host, port, null);
	}
	
}
