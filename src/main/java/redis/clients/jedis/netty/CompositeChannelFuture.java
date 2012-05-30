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

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * <p>Title: CompositeChannelFuture</p>
 * <p>Description: A {@link ChannelFuture} implementation that wraps one or more subsidiary {@link ChannelFuture}s </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.CompositeChannelFuture</code></p>
 */
public class CompositeChannelFuture implements ChannelFuture {

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#getChannel()
	 */
	public Channel getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#isDone()
	 */
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#isCancelled()
	 */
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#isSuccess()
	 */
	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#getCause()
	 */
	public Throwable getCause() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#cancel()
	 */
	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#setSuccess()
	 */
	public boolean setSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#setFailure(java.lang.Throwable)
	 */
	public boolean setFailure(Throwable cause) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#setProgress(long, long, long)
	 */
	public boolean setProgress(long amount, long current, long total) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#addListener(org.jboss.netty.channel.ChannelFutureListener)
	 */
	public void addListener(ChannelFutureListener listener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#removeListener(org.jboss.netty.channel.ChannelFutureListener)
	 */
	public void removeListener(ChannelFutureListener listener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#rethrowIfFailed()
	 */
	public ChannelFuture rethrowIfFailed() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#await()
	 */
	public ChannelFuture await() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#awaitUninterruptibly()
	 */
	public ChannelFuture awaitUninterruptibly() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#await(long, java.util.concurrent.TimeUnit)
	 */
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#await(long)
	 */
	public boolean await(long timeoutMillis) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#awaitUninterruptibly(long, java.util.concurrent.TimeUnit)
	 */
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelFuture#awaitUninterruptibly(long)
	 */
	public boolean awaitUninterruptibly(long timeoutMillis) {
		// TODO Auto-generated method stub
		return false;
	}

}
