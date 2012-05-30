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

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * <p>Title: OptimizedPubSub</p>
 * <p>Description: A Netty NIO based pub sub subscriber.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.OptimizedPubSub</code></p>
 */

public class OptimizedPubSub extends SimpleChannelUpstreamHandler implements PubSub, Closeable {
	/** The redis host or IP Address */
	protected final String host;
	/** The redis listening port */
	protected final int port;
	/** The timeout in ms. */
	protected final long timeout;
	
	/** The redis auth */
	protected final String auth;
	/** The comm channel for subbing */
	protected final Channel subChannel;
	/** The comm channel for pubbing */
	protected volatile Channel pubChannel;
	
	
	/**
	 * Returns an OptimizedPubSub for the passed host and port
	 * @param host The redis host
	 * @param port The redis port
	 * @param auth The redis auth password
	 * @param timeout The timeout in ms.
	 * @return An {@link OptimizedPubSub} instance
	 */
	
	public static OptimizedPubSub getInstance(String host, int port, String auth, long timeout) {
		return new OptimizedPubSub(host, port, auth, timeout);
	}
	
	/**
	 * Returns an OptimizedPubSub for the passed host and port
	 * @param host The redis host
	 * @param port The redis port
	 * @param timeout The timeout in ms.
	 * @return An {@link OptimizedPubSub} instance
	 */
	
	public static OptimizedPubSub getInstance(String host, int port, long timeout) {
		return getInstance(host, port, null, timeout);
	}
	
	/**
	 * Returns an OptimizedPubSub for the passed host and port
	 * @param host The redis host
	 * @param port The redis port
	 * @return An {@link OptimizedPubSub} instance
	 */
	
	public static OptimizedPubSub getInstance(String host, int port) {
		return getInstance(host, port, null, 2000);
	}
	
	
	
	
	/**
	 * Creates a new OptimizedPubSub
	 * @param host The redis host
	 * @param port The redis port
	 * @param auth The redis auth password
	 * @param timeout The timeout in ms.
	 */
	private OptimizedPubSub (String host, int port, String auth, long timeout) {
		this.host = host;
		this.port = port;
		this.auth = auth;
		this.timeout = timeout;
		subChannel = OptimizedPubSubFactory.getInstance(null).newChannelSynch(host, port, timeout);
		subChannel.getPipeline().addLast("SubListener", this);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Object msg = e.getMessage();
		
		if(msg instanceof MessageReply) {
			MessageReply mr = (MessageReply)msg; 
			log(mr);
		} else if(msg instanceof SubscribeConfirm) {
			log(msg);
		} else if(msg instanceof Integer) {
			log("[" + msg + "] Clients Received Published Message");
		}
	}
	
	/**
	 * Closes this PubSub
	 */
	public void close() throws IOException {
		subChannel.close();
		if(pubChannel!=null) {
			pubChannel.close();
		}		
	}

	
	/**
	 * Creates a new OptimizedPubSub
	 * @param host The redis host
	 * @param port The redis port
	 * @param timeout The timeout in ms.
	 */
	private OptimizedPubSub (String host, int port, int timeout) {
		this(host, port, null, timeout);
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#subscribe(java.lang.String[])
	 */
	public ChannelFuture subscribe(String... channels) {
		return subChannel.write(PubSubRequest.newRequest(PubSubCommand.SUBSCRIBE, channels));
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#unsubscribe(java.lang.String[])
	 */
	public ChannelFuture unsubscribe(String... channels) {
		return subChannel.write(PubSubRequest.newRequest(PubSubCommand.UNSUBSCRIBE, channels));
		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#psubscribe(java.lang.String[])
	 */
	public ChannelFuture psubscribe(String... patterns) {
		return subChannel.write(PubSubRequest.newRequest(PubSubCommand.PSUBSCRIBE, patterns));
		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#punsubscribe(java.lang.String[])
	 */
	public ChannelFuture punsubscribe(String... patterns) {
		return subChannel.write(PubSubRequest.newRequest(PubSubCommand.PUNSUBSCRIBE, patterns));		
	}
	
	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#publish(java.lang.String, java.lang.String[])
	 */
	public void publish(String channel, String...messages) {
		if(pubChannel==null) {
			pubChannel =  OptimizedPubSubFactory.getInstance(null).newChannelSynch(host, port, timeout);
			pubChannel.getPipeline().addLast("PubListener", this);
		}
		for(String message: messages) {
			if(message!=null && message.trim().length()>0) {
				this.pubChannel.write(PubSubRequest.newRequest(PubSubCommand.PUBLISH, channel, message.trim()));
			}
		}
	}
	
	
	
	
	public static void main(String[] args) {
		log("OPubSub Test");
		OptimizedPubSub pubsub = OptimizedPubSub.getInstance("ub", 6379);
		pubsub.subscribe("foo.bar");
		pubsub.psubscribe("foo*").awaitUninterruptibly();
		pubsub.publish("foo.bar", "Hello Venus");
		pubsub.publish("foo.bar", System.getProperties().stringPropertyNames().toArray(new String[0]));
		try {
			//Thread.currentThread().join(1000);
			Thread.currentThread().join();
			pubsub.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	/** A set of registered listeners */
	protected final Set<SubListener> listeners = new CopyOnWriteArraySet<SubListener>();

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#registerListener(redis.clients.jedis.netty.SubListener)
	 */
	public void registerListener(SubListener listener) {
		if(listener!=null) {
			listeners.add(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#unregisterListener(redis.clients.jedis.netty.SubListener)
	 */
	public void unregisterListener(SubListener listener) {
		if(listener!=null) {
			listeners.remove(listener);
		}		
	}

	
}
