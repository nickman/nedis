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

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

/**
 * <p>Title: UnidirectionalChannelHandlerFactory</p>
 * <p>Description: Wraps an Up and Down handler and makes it unidirectional</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.UnidirectionalChannelHandlerFactory</code></p>
 */

public class UnidirectionalChannelHandlerFactory  {
	/**
	 * Creates a unidirectional channel handler that wraps the passed delegate
	 * @param delegate The ChannelHandler to delegate to 
	 * @param upstream If true, an Upstream handler will be returned, otherwise a Downstream handler will be returned.
	 * @return The up or down only ChannelHandler
	 */
	public static ChannelHandler delegate(final ChannelHandler delegate, boolean upstream) {
		if(upstream) {
			final ChannelUpstreamHandler upDelegate = (ChannelUpstreamHandler)delegate;
			return new ChannelUpstreamHandler() {
				public void handleUpstream(ChannelHandlerContext ctx,  ChannelEvent e) throws Exception {
					upDelegate.handleUpstream(ctx, e);					
				}				
			};
		}
		final ChannelDownstreamHandler downDelegate = (ChannelDownstreamHandler)delegate;
		return new ChannelDownstreamHandler() {
			public void handleDownstream(ChannelHandlerContext ctx,  ChannelEvent e) throws Exception {
				downDelegate.handleDownstream(ctx, e);					
			}				
		};		
	}
	
	

}
