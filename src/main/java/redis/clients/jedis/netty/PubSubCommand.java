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
 * <p>Title: PubSubCommand</p>
 * <p>Description: Enumerates the pubsub commands</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.PubSubCommand</code></p>
 */

public enum PubSubCommand {
	/** The pattern subscribe command */
	PSUBSCRIBE ,
	/** The channel publish command */
	PUBLISH,
	/** The pattern unsubscribe command */
	PUNSUBSCRIBE,
	/** The channel subscribe command */
	SUBSCRIBE,
	/** The channel unsubscribe command */
	UNSUBSCRIBE;
	
	
	/**
	 * Returns the PubSubCommand for the passed string
	 * @param commandName The command name 
	 * @return a PubSubCommand
	 */
	public static PubSubCommand command(CharSequence commandName) {
		if(commandName==null) throw new IllegalArgumentException("The passed command name was null", new Throwable());
		try {
			return PubSubCommand.valueOf(commandName.toString().trim().toUpperCase());			
		} catch (Exception e) {
			throw new IllegalArgumentException("The passed command name [" + commandName + "] is not a valid PubSubCommand", new Throwable());
		}
	}

}
