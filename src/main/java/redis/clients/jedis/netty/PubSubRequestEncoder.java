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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * <p>Title: PubSubRequestEncoder</p>
 * <p>Description: Encoder to encode redis pubsub requests.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.PubSubRequestEncoder</code></p>
 */

public class PubSubRequestEncoder extends OneToOneEncoder implements CR {

	/**
	 * Creates a new PubSubRequestEncoder
	 */
	public PubSubRequestEncoder() {
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneEncoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if(msg instanceof PubSubRequest) {
			PubSubRequest psr = (PubSubRequest)msg;
			// the command size plus 8 bytes for the preamble
			int messageSize = psr.pubSubCommand.getFullByteCount() + 8; 
			int argCount = 1 + psr.arguments.size();
			boolean publish = psr.pubSubCommand==PubSubCommand.PUBLISH; 
			if(publish) {
				argCount++;
				messageSize += psr.channel.length();
			}
			for(String s: psr.arguments) {
				messageSize += s.getBytes().length + 8;
			}
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(messageSize);
			// ========== Arg Count ==========
			buffer.writeByte(ProtocolByte.ASTERISK_BYTE.getByte());
			buffer.writeBytes(("" + argCount).getBytes());
			buffer.writeBytes(CR_BYTES);
			// ========== Command ==========
			buffer.writeBytes(psr.pubSubCommand.getPrefix());
			buffer.writeBytes(psr.pubSubCommand.getFullBytes());
			// ========== Publish ==========
			if(publish) {
				byte[] channelBytes = psr.channel.getBytes();
				buffer.writeBytes(("$" + channelBytes.length).getBytes());
				buffer.writeBytes(CR_BYTES);
				buffer.writeBytes(channelBytes);
				buffer.writeBytes(CR_BYTES);				
			}
			// ========== Arguments ==========
			for(String arg: psr.arguments) {
				// ============  Arg Length ============
				byte[] argBytes = arg.getBytes();
				buffer.writeBytes(("$" + argBytes.length).getBytes());
				buffer.writeBytes(CR_BYTES);
				// ============  Arg Value ============
				buffer.writeBytes(argBytes);
				buffer.writeBytes(CR_BYTES);
			}
			return buffer;
		}
		throw new RuntimeException("Unexpected Message Type [" + msg.getClass().getName() + "]", new Throwable());
		
	}
	
	/*
SUBSCRIBE first second
*3
$9
subscribe
$5
first
:1
*3
$9
subscribe
$6
second
:2 
	 */

}
