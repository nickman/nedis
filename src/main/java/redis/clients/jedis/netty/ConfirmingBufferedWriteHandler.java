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

import java.util.Queue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.queue.BufferedWriteHandler;

/**
 * <p>Title: ConfirmingBufferedWriteHandler</p>
 * <p>Description: A {@link BufferedWriteHandler} extension that confirms the writes into the buffer by marking the {@link ChannelFuture} complete </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.ConfirmingBufferedWriteHandler</code></p>
 */
public class ConfirmingBufferedWriteHandler extends BufferedWriteHandler {
	
	
	/**
	 * Creates a new ConfirmingBufferedWriteHandler
	 * @param queue
	 * @param consolidateOnFlush
	 */
	public ConfirmingBufferedWriteHandler(Queue<MessageEvent> queue,
			boolean consolidateOnFlush) {
		super(queue, consolidateOnFlush);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.handler.queue.BufferedWriteHandler#writeRequested(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		try {
			super.writeRequested(ctx, e);
		} catch (Exception ex) {
			e.getFuture().setFailure(ex);
		}
		e.getFuture().setSuccess();
	}
	
	
}
