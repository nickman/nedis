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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

import redis.clients.jedis.netty.jmx.ThreadPoolMonitor;

/**
 * <p>Title: OptimizedPubSubFactory</p>
 * <p>Description: The netty based factory for {@link OptimizedPubSub} instances</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.OptimizedPubSubFactory</code></p>
 */

public class OptimizedPubSubFactory {
	/** The singleton instance */
	private static volatile OptimizedPubSubFactory instance = null;
	/** The singleton instance ctor lock */
	private static final Object lock = new Object();
	/** The client bootstrap */
	private final ClientBootstrap bootstrap;
	/** The client channel factory */
	private final ChannelFactory channelFactory;
	/** The client boss thread pool */
	private final ThreadPoolExecutor bossPool;
	/** The client worker thread pool */
	private final ThreadPoolExecutor workerPool;
	/** The client socket options */
	private final Map<String, Object> socketOptions = new HashMap<String, Object>();
	/** The client pipeine factory */
	private final ChannelPipelineFactory pipelineFactory;
	/** An execution handler to hand off the metric submissions to */
	protected  final ExecutionHandler execHandler = new ExecutionHandler(Executors.newCachedThreadPool(			
			new ThreadFactory() {
				final AtomicInteger serial = new AtomicInteger(0);
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "ExecHandlerThread#" + serial.incrementAndGet());
					t.setDaemon(true);
					return t;
				}
			}
	), false, true);
	
	/** The upstream only exec handler wrapper */
	protected final ChannelHandler wrappedExecHandler = UnidirectionalChannelHandlerFactory.delegate(execHandler, true);
	
	/** The name of the execution handler */
	public static final String EXEC_HANDLER_NAME = "execHandler";	
	/** The name of the multi bulk decoder  */
	public static final String MULTI_DECODER_NAME = "multiBulkDecoder";
	/** The name of the request encoder  */
	public static final String REQ_ENCODER_NAME = "pubSubRequestEncoder";
	/** The name of the logging handler  */
	public static final String LOG_HANDLING_NAME = "loggingHandler";
	
	
	/**
	 * Returns the OptimizedPubSubFactory singleton instance
	 * @param socketOptions An optional map of socket options
	 * @return the OptimizedPubSubFactory singleton instance
	 */ 
	public static OptimizedPubSubFactory getInstance(Map<String, Object> socketOptions) {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new OptimizedPubSubFactory(socketOptions);
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * Creates a new OptimizedPubSubFactory
	 * @param socketOptions An optional map of socket options
	 */
	private OptimizedPubSubFactory(Map<String, Object> socketOptions) {
		ThreadFactory bossThreadFactory = new ThreadFactory(){
			final AtomicInteger serial = new AtomicInteger(0);
			final ThreadGroup threadGroup = new ThreadGroup("PubSubBossThreadGroup");
			public Thread newThread(Runnable r) {
				Thread t = new Thread(threadGroup, r, "PubSubBossThread#" + serial.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		};
		ThreadFactory workerThreadFactory = new ThreadFactory(){
			final AtomicInteger serial = new AtomicInteger(0);
			final ThreadGroup threadGroup = new ThreadGroup("PubSubWorkerThreadGroup");
			public Thread newThread(Runnable r) {
				Thread t = new Thread(threadGroup, r, "PubSubWorkerThread#" + serial.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		};			
		bossPool = new ThreadPoolExecutor(1, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), bossThreadFactory);
		workerPool = new ThreadPoolExecutor(5, 60, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), workerThreadFactory);
		ThreadPoolMonitor.registerMonitor(execHandler.getExecutor(), new StringBuilder(getClass().getPackage().getName()).append(":service=ThreadPool,name=ExecutionHandler"));
		ThreadPoolMonitor.registerMonitor(bossPool, new StringBuilder(getClass().getPackage().getName()).append(":service=ThreadPool,name=BossPool"));
		ThreadPoolMonitor.registerMonitor(workerPool, new StringBuilder(getClass().getPackage().getName()).append(":service=ThreadPool,name=WorkerPool"));
		channelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);
		pipelineFactory = new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {				
				ChannelPipeline pipeline = Channels.pipeline();
				//pipeline.addLast(LOG_HANDLING_NAME, new LoggingHandler(InternalLogLevel.INFO, false));
				pipeline.addLast(MULTI_DECODER_NAME, new RedisPubEventDecoder<RedisPubEvent>());
				pipeline.addLast(EXEC_HANDLER_NAME, wrappedExecHandler);
				pipeline.addLast(REQ_ENCODER_NAME, new PubSubRequestEncoder());
				
				
				return pipeline;
			}
		};
		
		bootstrap = new ClientBootstrap(channelFactory);
		bootstrap.setPipelineFactory(pipelineFactory);
		if(socketOptions!=null) {
			this.socketOptions.putAll(socketOptions);
		}
	}
	
	/**
	 *  Connects to the passed port at the passed host
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @return The ChannelFuture of the connection
	 */
	public ChannelFuture newChannel(String host, int port) {
		SocketAddress remoteAddress = new InetSocketAddress(host, port);
		return bootstrap.connect(remoteAddress);
	}
	
	/**
	 * Connects to the passed port at the passed host synchronously
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param timeout The connection timeout in ms. 
	 * @return A connected Channel
	 */
	public Channel newChannelSynch(String host, int port, long timeout) {
		SocketAddress remoteAddress = new InetSocketAddress(host, port);
		ChannelFuture cf = bootstrap.connect(remoteAddress);
		if(cf.awaitUninterruptibly(timeout)) {
			if(cf.isSuccess()) {
				return cf.getChannel();
			}
			throw new RuntimeException("Channel Connection to [" + remoteAddress + "] Failed", cf.getCause());
		} 
		throw new RuntimeException("Channel Connection to [" + remoteAddress + "] Timed Out After [" + timeout + "] ms");
	}

	
	/**
	 * Adds or sets a socket option 
	 * @param name The name of the socket option
	 * @param value The value of of the socket option
	 */
	public void addSocketOption(String name, Object value) {
		if(name==null) throw new IllegalArgumentException("The passed name was null", new Throwable());
		if(value==null) throw new IllegalArgumentException("The passed value was null", new Throwable());
		socketOptions.put(name, value);
	}
	
}
