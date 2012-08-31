// libraries
import net.hellonico.potato.*;
import net.hellonico.jabber.*;

// classes
import org.jivesoftware.smack.Chat;

JabberLibrary jabber;
Chat chat;

void setup() {
  size(400,400);
  smooth();

  // initiate the library   
  jabber = new JabberLibrary(this);
  
  // start a chat. here the friend we are talking to
  // is in the setup file
  chat = jabber.startChat();
  
  // prepare a font
  PFont font = createFont("",40);
  textFont(font);
  
  background(128);
}

void draw() {
  
}

void mousePressed() {
   /* 
   Simply sends a message
   */ 
   jabber.sendMessage(chat,"My mouse was there: "+mouseX+":"+mouseY);	
}

/**
  This callback is registered when starting a chat
*/
void onJabberMessage(String user, String message) {
  background(128);
  text(message, 50, 150);
}
