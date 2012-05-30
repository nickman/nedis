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

import static redis.clients.jedis.netty.MultiBulkReplyEnum.*;
import static redis.clients.jedis.netty.ProtocolBytes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

/**
 * <p>Title: MultiBulkReplyDecoder</p>
 * <p>Description: A Replay decoder for Redis multibulk replies</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.MultiBulkReplyDecoder</code></p>
 */
public class MultiBulkReplyDecoder<T> extends ReplayingDecoder<MultiBulkReplyEnum> {
    
    /** The byte sequence of a CR */
    public static byte[] CR_BYTES = "\r\n".getBytes();
    /** The length of the CR byte sequence */
    public static final int CR_LENGTH = CR_BYTES.length;

	/**
	 * Creates a new MultiBulkReplyDecoder
	 */
	public MultiBulkReplyDecoder() {
		super(MultiBulkReplyEnum.TYPE);
	}
	
	/**
	 * Drains the stream of the CR bytes between each redis line
	 * @param cb The channel buffer to read from
	 * @throws Exception thrown if the byte sequence cannot be fully read or is fullly read but does not contain the expected bytes.
	 */
	protected void  readCr(ChannelBuffer cb) throws Exception {
		byte[] bytes = new byte[CR_LENGTH];
		cb.readBytes(bytes);
		if(!Arrays.equals(CR_BYTES, bytes)) {
			throw new Exception("Unexpected byte sequence [" + new String(bytes) + "]. Expected [" + new String(CR_BYTES) + "]", new Throwable());
		}
	}

	/**
	 * Reads a byte array from the channel buffer and returns it
	 * @param cb The channel buffer to read from
	 * @param expectedBytes The expected number of bytes
	 * @return the read byte array
	 */
	protected byte[]  read(ChannelBuffer cb, int expectedBytes)  {
		byte[] bytes = new byte[expectedBytes];
		cb.readBytes(bytes);
		return bytes;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.handler.codec.replay.ReplayingDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer, java.lang.Enum)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer channelBuffer, MultiBulkReplyEnum state) throws Exception {
		switch (state) {
			case TYPE:
				byte typeByte = channelBuffer.readByte();
				if(typeByte==ASTERISK_BYTE.getByte()) {					
					checkpoint(ARG_COUNT);
				} else {
					throw new Exception("Unexpected byte character [" + (char)typeByte + "] Expected [" + ASTERISK_BYTE + "]", new Throwable());
				}
				break;
			case ARG_COUNT:
				int argCount = channelBuffer.readInt();
				ctx.setAttachment(new Object[] {new AtomicInteger(argCount), null, new ArrayList<byte[]>(argCount)});
				readCr(channelBuffer);
				checkpoint(NEXT_SIZE_PREFIX);
				break;
			case NEXT_SIZE_PREFIX:
				byte sizePrefixByte = channelBuffer.readByte();
				if(sizePrefixByte==DOLLAR_BYTE.getByte()) {
					readCr(channelBuffer);
					checkpoint(NEXT_SIZE);
				} else {
					throw new Exception("Unexpected byte character [" + (char)sizePrefixByte + "] Expected [" + DOLLAR_BYTE + "]", new Throwable());
				}				
				break;				
			case NEXT_SIZE:
				int nextSize = channelBuffer.readInt();
				((Object[])ctx.getAttachment())[1] = nextSize;
				readCr(channelBuffer);
				checkpoint(NEXT_MESSAGE);
				break;
			case NEXT_MESSAGE:
				Object[] channelState  = (Object[])ctx.getAttachment();
				nextSize = (Integer)channelState[1];
				((ArrayList<byte[]>)channelState[2]).add(read(channelBuffer, nextSize));
				readCr(channelBuffer);
				if(((AtomicInteger)channelState[0]).decrementAndGet()==0) {
					return channelState[2];
				}
				checkpoint(NEXT_SIZE_PREFIX);				
		}
		return null;
	}

}
