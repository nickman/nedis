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
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.internal.QueueFactory;

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
	 * Returns a pipelining version of this pubsub instance
	 * @return a pipelining version of this pubsub instance
	 */
	public PipelinedOptimizedPubSub getPipelinedPubSub() {
		return new PipelinedOptimizedPubSub(host, port, auth, timeout);
	}
	
	public static class PipelinedOptimizedPubSub extends OptimizedPubSub {
		/** The subscriber channel pipeline buffer */
		private final ConfirmingBufferedWriteHandler subBufferingHandler;
		/** The sub pipeline queue */
		private final Queue<MessageEvent> subQueue = QueueFactory.createQueue(MessageEvent.class);
		/** The publisher channel pipeline buffer */
		private volatile ConfirmingBufferedWriteHandler pubBufferingHandler;
		/** The pub pipeline queue */
		private  Queue<MessageEvent> pubQueue = null;
		
		/**
		 * Flushes the publisher pipeline
		 */
		public void flushPub() {
			if(pubChannel!=null) {
				if(pubQueue.size()>0) {
					pubBufferingHandler.flush(true);
				}
			}
		}
		
		/**
		 * Flushes the subscriber pipeline
		 */
		public void flushSub() {
			if(subQueue.size()>0) {
				subBufferingHandler.flush(true);
			}
		}
		
		/**
		 * Flushes the subscriber and publisher pipelines
		 */
		public void flush() {
			flushSub();
			flushPub();
		}
		
		
		
		/**
		 * Initializes a non-pipelined pub channel
		 */
		@Override
		protected void initPublishChannel() {
			if(pubChannel==null) {
				synchronized(this) {
					if(pubChannel==null) {
						pubChannel =  OptimizedPubSubFactory.getInstance(null).newChannelSynch(host, port, timeout);
						pubChannel.getPipeline().addLast("PubListener", this);
						pubQueue = QueueFactory.createQueue(MessageEvent.class);
						pubBufferingHandler = new ConfirmingBufferedWriteHandler(pubQueue, false);
						pubChannel.getPipeline().addAfter(OptimizedPubSubFactory.REQ_ENCODER_NAME, "pubPipelineBuffer", UnidirectionalChannelHandlerFactory.delegate(pubBufferingHandler, false));						
					}
				}
			}
		}
		
		
		/**
		 * Creates a new PipelinedOptimizedPubSub
		 * @param host The redis host
		 * @param port The redis port
		 * @param auth The redis auth password
		 * @param timeout The timeout in ms.
		 */
		protected PipelinedOptimizedPubSub(String host, int port, String auth, long timeout) {
			super(host, port, auth, timeout);
			subBufferingHandler = new ConfirmingBufferedWriteHandler(subQueue, false);
			subChannel.getPipeline().addAfter(OptimizedPubSubFactory.REQ_ENCODER_NAME, "subPipelineBuffer", UnidirectionalChannelHandlerFactory.delegate(subBufferingHandler, false));
		}



	}
	
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Object msg = e.getMessage();
		
		if(msg instanceof MessageReply) {
			MessageReply mr = (MessageReply)msg;
			mr.publish(listeners);
		} else if(msg instanceof SubscribeConfirm) {
			log(msg);
		} else if(msg instanceof Integer) {
			//log("[" + msg + "] Clients Received Published Message");
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
	 * Flushes a pipelined request batch
	 * @param channel The channel to flush
	 * @return the ChannelFuture of this write
	 */
	protected ChannelFuture  pipelinedFlush(Channel channel) {
		ChannelFuture cf = channel.write(true);
		return cf;		
	}
	
	/**
	 * Cancels a pipelined request batch
	 * @param channel The channel to cancel a pipelined request in
	 * @return the ChannelFuture of this cancel
	 */
	protected ChannelFuture  pipelinedCancel(Channel channel) {
		ChannelFuture cf = channel.write(false);
		return cf;		
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
	 * Initializes a non-pipelined pub channel
	 */
	protected void initPublishChannel() {
		if(pubChannel==null) {
			synchronized(this) {
				if(pubChannel==null) {
					pubChannel =  OptimizedPubSubFactory.getInstance(null).newChannelSynch(host, port, timeout);
					pubChannel.getPipeline().addLast("PubListener", this);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.PubSub#publish(java.lang.String, java.lang.String[])
	 */
	public ChannelFuture publish(String channel, String...messages) {
		initPublishChannel();		
		ChannelFuture cf = null;
		if(messages!=null && messages.length>0) {
			if(messages.length !=0) {
				for(String message: messages) {
					if(message==null) continue;
					message = message.trim();
					if(message.length()<1) continue;
					cf = pubChannel.write(PubSubRequest.newRequest(PubSubCommand.PUBLISH, channel, message));
				}				
			}
		}
		return cf;
	}
	
	
	
	
	public static void main(String[] args) {
		log("OPubSub Test");
		OptimizedPubSub pubsub = OptimizedPubSub.getInstance("ub", 6379);
		pubsub.subscribe("foo.bar");
		pubsub.psubscribe("foo*").awaitUninterruptibly();
		final Set<String> execThreadNames = new HashSet<String>();
		pubsub.registerListener(new SubListener(){
			
			public void onChannelMessage(String channel, String message) {
				execThreadNames.add(Thread.currentThread().getName());
				//log("[" + Thread.currentThread().getName() + "] Channel Message\n\tChannel:" + channel + "\n\tMessage:" + message);
			}
			public void onPatternMessage(String pattern, String channel, String message) {
				execThreadNames.add(Thread.currentThread().getName());
				//log("[" + Thread.currentThread().getName() + "]  Channel Message\n\tPattern:" + pattern + "\n\tChannel:" + channel + "\n\tMessage:" + message);
			}
			
		});
		pubsub.publish("foo.bar", "Hello Venus");
		String[] props = System.getProperties().stringPropertyNames().toArray(new String[0]);		
		long start = System.currentTimeMillis();
		pubsub.publish("foo.bar", props).awaitUninterruptibly();
		long elapsed = System.currentTimeMillis()-start;
		log("\n\t======================================\n\t[" + props.length + "] Messages Sent In [" + elapsed + "] ms.\n\t======================================\n");
		
		PipelinedOptimizedPubSub pipePubSub = pubsub.getPipelinedPubSub();
		for(int i = 0; i < 100; i++) {
			boolean pipeline = i%2==0;
			start = System.currentTimeMillis();
			if(pipeline) {
				ChannelFuture cf = pipePubSub.publish("foo.bar", props);
				while(true) { if(cf.isDone()) break; else Thread.yield(); }
				pipePubSub.flushPub();				
			} else {
				pubsub.publish("foo.bar", props).awaitUninterruptibly();
			}
			elapsed = System.currentTimeMillis()-start;
			if(pipeline) {
				log("\n\t======================================\n\t[" + props.length + "] Pipelined Messages Sent In [" + elapsed + "] ms.\n\t======================================\n");
			} else {
				log("\n\t======================================\n\t[" + props.length + "] Messages Sent In [" + elapsed + "] ms.\n\t======================================\n");
			}
			try { Thread.currentThread().join(1000); } catch (Exception ex) {}
		}
		
		try {
			Thread.currentThread().join(2000);
			log(execThreadNames);
			Thread.currentThread().join();
			pubsub.close();
			pipePubSub.close();
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
