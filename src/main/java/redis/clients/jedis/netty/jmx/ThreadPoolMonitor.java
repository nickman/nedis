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
package redis.clients.jedis.netty.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * <p>Title: ThreadPoolMonitor</p>
 * <p>Description: JMX monitor for {@link ThreadPoolExecutor}s.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.jmx.ThreadPoolMonitor</code></p>
 */
public class ThreadPoolMonitor implements ThreadPoolMonitorMXBean {
	/** A cache of registered thread pool monitors keyed by their registered JMX ObjectNames */
	protected static final Map<ObjectName, ThreadPoolMonitor> instances = new ConcurrentHashMap<ObjectName, ThreadPoolMonitor>();
	/** The platform MBeanServer */
	protected static final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	/** The ThreadPoolExecutor instance to be instrumented */
	protected final ThreadPoolExecutor threadPoolExecutor;
	
	/**
	 * Creates and registers a new ThreadPoolMonitor
	 * @param executor The thread pool to instrument
	 * @param objectName The JMX ObjectName of the thread pool MBean
	 */
	public static void registerMonitor(Executor executor, CharSequence objectName) {
		if(executor==null) throw new IllegalArgumentException("The passed executor was null", new Throwable());
		if(!(executor instanceof ThreadPoolExecutor)) throw new IllegalArgumentException("The passed executor [" + executor.getClass().getName() + "] is not an instance of ThreadPoolExecutor", new Throwable());
		ObjectName on = JMXHelper.objectName(objectName);
		if(!instances.containsKey(on)) {
			synchronized(instances) {
				if(!instances.containsKey(on)) {
					ThreadPoolExecutor tpe = (ThreadPoolExecutor)executor;
					ThreadPoolMonitor tpm = new ThreadPoolMonitor(tpe);
					instances.put(on, tpm);
					if(!server.isRegistered(on)) {
						try {
							server.registerMBean(tpm, on);
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Creates a new ThreadPoolMonitor
	 * @param threadPoolExecutor The thread pool to instrument
	 */
	protected ThreadPoolMonitor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}	
	
	/**
	 * Indicates if this pool allows core threads to time out and terminate if no tasks arrive within the keepAlive time, being replaced if needed when new tasks arrive.
	 * @return true if core threads can time out, false otherwise
	 */
	public boolean isAllowsCoreThreadTimeOut() {
		return threadPoolExecutor.allowsCoreThreadTimeOut();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getActiveCount()
	 */
	public int getActiveCount() {
		return threadPoolExecutor.getActiveCount();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getCompletedTaskCount()
	 */
	public long getCompletedTaskCount() {
		return threadPoolExecutor.getCompletedTaskCount();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getCorePoolSize()
	 */
	public int getCorePoolSize() {
		return threadPoolExecutor.getCorePoolSize();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getMaxiumPoolSize()
	 */
	public int getMaxiumPoolSize() {
		return threadPoolExecutor.getMaximumPoolSize();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getLargestPoolSize()
	 */
	public int getLargestPoolSize() {
		return threadPoolExecutor.getLargestPoolSize();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getCurrentPoolSize()
	 */
	public int getCurrentPoolSize() {
		return threadPoolExecutor.getPoolSize();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getKeepAliveTimeMillis()
	 */
	public long getKeepAliveTimeMillis() {
		return threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS);
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getQueueDepth()
	 */
	public int getQueueDepth() {
		return threadPoolExecutor.getQueue().size();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#getQueueCapacity()
	 */
	public int getQueueCapacity() {		
		return threadPoolExecutor.getQueue().remainingCapacity();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#purge()
	 */
	public void purge() {
		threadPoolExecutor.purge();
		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#isShutdown()
	 */
	public boolean isShutdown() {
		return threadPoolExecutor.isShutdown();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#isTerminated()
	 */
	public boolean isTerminated() {
		return threadPoolExecutor.isTerminated();
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean#isTerminating()
	 */
	public boolean isTerminating() {
		return threadPoolExecutor.isTerminating();
	}
	
}
