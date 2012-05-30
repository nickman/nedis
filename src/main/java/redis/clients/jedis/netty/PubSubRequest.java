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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: PubSubRequest</p>
 * <p>Description: Container for a redis pubsub request</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>redis.clients.jedis.netty.PubSubRequest</code></p>
 */

public class PubSubRequest {
	/** The PubSub Command */
	public final PubSubCommand pubSubCommand;
	/** The PubSub command arguments */
	public final List<String> arguments;
	/** The pubsub channel to publish to */
	public final String channel;
	
	/**
	 * Returns a new PubSubRequest
	 * @param command The {@link PubSubCommand} to issue
	 * @param message The message to publish
	 * @param channel The name of the channel to publish to
	 * @return a PubSubRequest
	 */
	public static PubSubRequest newRequest(PubSubCommand command, String channel, String message) {
		return new PubSubRequest(command, channel, message);
	}
	
	/**
	 * Returns a new PubSubRequest
	 * @param command The {@link PubSubCommand} to issue
	 * @param arguments The arguments to the request
	 * @return a PubSubRequest
	 */
	public static PubSubRequest newRequest(PubSubCommand command, String...arguments) {
		return new PubSubRequest(command, null, arguments);
	}
	
	
	/**
	 * Returns a new PubSubRequest
	 * @param commandName The {@link PubSubCommand} name to issue
	 * @param channel The name of the channel to publish to
	 * @param arguments The arguments to the request
	 * @return a PubSubRequest
	 */
	public static PubSubRequest newRequest(CharSequence commandName, String channel, String...arguments) {
		PubSubCommand command = PubSubCommand.command(commandName);
		return new PubSubRequest(command, arguments);
	}
	
	/**
	 * Returns a new PubSubRequest
	 * @param commandName The {@link PubSubCommand} name to issue
	 * @param channel The name of the channel to publish to
	 * @return a PubSubRequest
	 */
	public static PubSubRequest newRequest(CharSequence commandName, String...arguments) {
		return newRequest(commandName, null, arguments);
	}
	
	
	private PubSubRequest(PubSubCommand command, String channel, String message) {
		this.channel = channel;
		pubSubCommand = command;
		arguments = Collections.unmodifiableList(Collections.singletonList(message));
	}
	
	
	private PubSubRequest(PubSubCommand command, String channel, String...args) {
		this.channel = channel;
		pubSubCommand = command;
		List<String> tmp  = new ArrayList<String>(args.length);
		for(String s: args) {
			if(s!=null && !s.trim().isEmpty()) {
				tmp.add(s.trim());
			}
		}
		arguments = Collections.unmodifiableList(tmp);
	}
	
	private PubSubRequest(PubSubCommand command, String...args) {
		this(command, null, args);
	}
	
	

}
