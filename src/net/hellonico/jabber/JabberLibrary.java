/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package net.hellonico.jabber;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import processing.core.PApplet;

public class JabberLibrary implements MessageListener {

	PApplet myParent;

	public final static String VERSION = "##library.prettyVersion##";

	private XMPPConnection connection;
	private HashMap<String, String> _settings;

	private Method fancyEventMethod;

	public JabberLibrary(PApplet theParent, HashMap settings) {
		try {
			init(theParent, settings);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public JabberLibrary(PApplet theParent) {
		try {
			Class klass = Class.forName("net.hellonico.potato.Potato");
			Constructor c = klass.getConstructor(PApplet.class);
			Object potato = c.newInstance(theParent);
			Method m = klass.getMethod("getSettings", String.class);
			HashMap settings = (HashMap) m.invoke(potato, "jabber");
			this.init(theParent, settings);
		} catch (Exception e) {
			throw new RuntimeException("This is carrot day." + e.getMessage());
		}

	}

	public static String version() {
		return VERSION;
	}

	public void init(PApplet theParent, HashMap settings) throws XMPPException {
		myParent = theParent;
		ConnectionConfiguration connConfig = new ConnectionConfiguration(
				(String) settings.get("host"), 
				Integer.parseInt((String) settings.get("port")),
				(String) settings.get("extra"));
		connection = new XMPPConnection(connConfig);
		connection.connect();

		connection.login(
				(String) settings.get("user"), 
				(String) settings.get("password"));
		System.out.println(connection.isConnected());
		Roster roster = connection.getRoster();
		System.out.println("Number of contacts: " + roster.getEntryCount());
		this._settings = settings;
	}
	
	public void registerCallback() {
		String methodName = "onJabberMessage";
		try {
			fancyEventMethod = myParent.getClass().getMethod(methodName,
					new Class[] { String.class, String.class });
			System.out.println("Registered callback:" + methodName);
		} catch (Exception e) {
			System.out
					.println("Register Callback has no method in the main applet:"
							+ methodName);
		}
	}

	public Chat startChat(String friend, MessageListener ml) {
		ChatManager chatmanager = connection.getChatManager();
		registerCallback();
		return chatmanager.createChat("nico@fivecool.net", ml);
	}

	public Chat startChat(String friend) {
		return startChat(friend, this);
	}
	public Chat startChat() {
		return startChat(_settings.get("friend"), this);
	}
	public void sendMessage(Chat c, String message) {
		try {
			c.sendMessage(message);
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public void processMessage(Chat arg0, Message arg1) {
		System.out.println("*\t" + arg1.getBody());
		if(arg1.getBody()!=null)
		  if(this.fancyEventMethod!=null) 
			try {
				fancyEventMethod.invoke(myParent, new Object[] { arg1.getFrom(), arg1.getBody() });
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void main(String[] args) throws Exception {
		HashMap<String, String> settings = new HashMap();
		settings.put("host", "talk.google.com");
		settings.put("port", "5222");
		settings.put("extra", "gmail.com");
		settings.put("user", "hellonico@gmail.com");
		settings.put("password", "tiabtfda28!");

		JabberLibrary api = new JabberLibrary(new PApplet());
		Chat chat = api.startChat("nico@fivecool.net");

		for (int i = 0; i < 10; i++) {
			chat.sendMessage("test:" + i);
		}
	}

}
