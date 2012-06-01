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

import javax.management.MXBean;
import javax.management.openmbean.TabularData;

/**
 * <p>Title: ThreadPoolMonitorMXBean</p>
 * <p>Description: MXBean interface for the ThreadPoolMonitor</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.jmx.ThreadPoolMonitorMXBean</code></p>
 */
@MXBean
public interface ThreadPoolMonitorMXBean {
	/**
	 * Indicates if this pool allows core threads to time out and terminate if no tasks arrive within the keepAlive time, being replaced if needed when new tasks arrive.
	 * @return true if core threads can time out, false otherwise
	 */
	public boolean isAllowsCoreThreadTimeOut();
	
	/**
	 * Returns the approximate number of threads that are actively executing tasks.
	 * @return the approximate number of threads
	 */
	public int getActiveCount();
	
	/**
	 * Returns the approximate total number of tasks that have completed execution.
	 * @return the number of tasks completed
	 */
	public long getCompletedTaskCount();
	
	/**
	 * Returns the pool's core size
	 * @return the pool's core size
	 */
	public int getCorePoolSize();
	
	/**
	 * Returns the pool's maximum size
	 * @return the pool's maximum  size
	 */
	public int getMaxiumPoolSize();
	
	/**
	 * Returns the pool's historical largest size
	 * @return the pool's historical largest size
	 */
	public int getLargestPoolSize();
	
	/**
	 * Returns the pool's current size
	 * @return the pool's current size
	 */
	public int getCurrentPoolSize();
	
	
	/**
	 * Returns the thread keep-alive time in ms.
	 * @return the thread keep-alive time in ms.
	 */
	public long getKeepAliveTimeMillis();
	
	/**
	 * Returns the pool's execution queue depth
	 * @return the pool's execution queue depth
	 */
	public int getQueueDepth();
	
	/**
	 * Returns the pool's execution queue capacity
	 * @return the pool's execution queue capacity
	 */
	public int getQueueCapacity();
	
	/**
	 * Purges the pool
	 */
	public void purge();
	
	/**
	 * Returns true if this executor has been shut down.
	 * @return true if this executor has been shut down.
	 */
	public boolean isShutdown();
	
	/**
	 * Returns true if all tasks have completed following shut down.
	 * @return true if all tasks have completed following shut down.
	 */
	public boolean isTerminated();
	
	/**
	 * Returns true if this executor is in the process of terminating after shutdown or shutdownNow but has not completely terminated.
	 * @return true if this executor is in the process of terminating after shutdown or shutdownNow but has not completely terminated.
	 */
	public boolean isTerminating();
	
	/**
	 * Returns the name of the thread pool's thread group.
	 * @return the name of the thread pool's thread group.
	 */
	public String getThreadGroupName();
	
	/**
	 * Returns information about the thread pool's thread group
	 * @return information about the thread pool's thread group
	 */
	public String printThreadGroupInfo();
	
	/**
	 * Returns the ThreadInfos of the threads in the ThreadPool
	 * @return the ThreadInfos of the threads in the ThreadPool
	 */
	public TabularData getThreadInfos();
	
	
}
