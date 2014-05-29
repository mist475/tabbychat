package acs.tabbychat.util;

import java.util.ArrayList;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 * Stores a List of IChatComponents and provides
 * methods useful to chat lists, such as merging.
 * 
 * @author Matthew Messinger
 *
 */
@SuppressWarnings("serial")
public class ComponentList extends ArrayList<IChatComponent> {
	
	public static ComponentList newInstance(){
		return new ComponentList();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(IChatComponent chat : this){
			sb.append(chat.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String getUnformattedText(){
		StringBuilder sb = new StringBuilder();
		for(IChatComponent chat : this){
			sb.append(chat.getUnformattedText());
		}
		return sb.toString();
	}

	public String getFormattedText() {
		StringBuilder sb = new StringBuilder();
		for(IChatComponent chat : this){
			sb.append(chat.getFormattedText());
		}
		return sb.toString();
	}
	
	public IChatComponent merge(){
		IChatComponent chat = new ChatComponentText("");
		for(IChatComponent ichat : this){
			chat.appendSibling(ichat);
		}
		return chat;
	}

}
