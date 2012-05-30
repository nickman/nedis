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

public enum PubSubCommand implements CR {
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
	
	
	private PubSubCommand() {
    	bytes = name().getBytes();
    	byteCount = bytes.length;
    	fullBytes = new byte[byteCount + CR_LENGTH];
    	System.arraycopy(name().getBytes(), 0, fullBytes, 0, byteCount);
    	System.arraycopy(CR_BYTES, 0, fullBytes, byteCount, CR_LENGTH);
    	fullByteCount = fullBytes.length;    	
    	prefix = ("$" + name().length() + CR).getBytes();
	}
	
    private final int byteCount;
    private final int fullByteCount;
    private final byte[] bytes;
    private final byte[] fullBytes;
    private final byte[] prefix;
    
    public static void log(Object msg) {
    	System.out.println(msg);
    }
    
    public static void main(String[] args) {
    	log("PubSubCommands:");
    	for(PubSubCommand psc: PubSubCommand.values()) {
    		log("[" + new String(psc.getFullBytes()) + "]");
    		log("Byte Length:" + psc.getByteCount());
    		log("Full Byte Length:" + psc.getFullByteCount());
    		log("=========");
    	}
    }

	/**
	 * Returns the byte count of this command, not including the CR
	 * @return the byteCount the byte count of this command, not including the CR
	 */
	public int getByteCount() {
		return byteCount;
	}
	
	/**
	 * Returns the byte count of this command, including the CR
	 * @return the byteCount the byte count of this command, including the CR
	 */
	public int getFullByteCount() {
		return fullByteCount;
	}

	/**
	 * Returns the bytes of the command without the CR
	 * @return the bytes of the command without the CR
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Returns the bytes of the command with the CR
	 * @return the bytes of the command with the CR
	 */
	public byte[] getFullBytes() {
		return fullBytes;
	}
		
	
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

	/**
	 * Returns the prefix for the command. eg. for SUBSCRIBE, <code>$9</code>
	 * @return the prefix for the command
	 */
	public byte[] getPrefix() {
		return prefix;
	}

}
