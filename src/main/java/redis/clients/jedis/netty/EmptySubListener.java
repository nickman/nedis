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

/**
 * <p>Title: EmptySubListener</p>
 * <p>Description: A {@link SubListener} template.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.EmptySubListener</code></p>
 */
public class EmptySubListener implements SubListener {

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.SubListener#onChannelMessage(java.lang.String, java.lang.String)
	 */
	public void onChannelMessage(String channel, String message) {

	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.netty.SubListener#onPatternMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void onPatternMessage(String pattern, String channel, String message) {

	}

}
