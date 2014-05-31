package acs.tabbychat.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

/**
 * A library for modifying Chat Components.
 * 
 * @author Matthew Messinger
 * 
 */
public class ChatComponentUtil {

	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

	/**
	 * Replaces all instances of the given word.
	 * 
	 * @param chat
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static IChatComponent replaceText(IChatComponent chat, String regex,
			String replacement) {
		List<IChatComponent> iter = chat.getSiblings();
		IChatComponent newChat = new ChatComponentText("");
		for (IChatComponent next : iter) {
			IChatComponent comp = new ChatComponentText(next
					.getUnformattedText().replaceAll(regex, replacement));
			comp.setChatStyle(next.getChatStyle().createShallowCopy());
			newChat.appendSibling(comp);
		}
		return newChat;
	}

	/**
	 * Splits a ChatComponent into multiple lines so it will fit within a width.
	 * 
	 * @param chat
	 * @param limit
	 * @return
	 */
	public static ComponentList split(IChatComponent chat, int limit) {
		// Split components up
		ComponentList ichatList = ComponentList.newInstance();
		ComponentList list = ComponentList.newInstance();
		ComponentList list1 = ComponentList.newInstance();
		if (chat.getSiblings().size() == 0) {
			list.add(chat);
		} else {
			list.addAll(chat.getSiblings());
		}
		// Convert legacy formatting
		for (IChatComponent ichat : list){
			if(ichat.getUnformattedTextForChat().contains("\u00a7"))
				list1.addAll(formattedStringToChat(ichat.getUnformattedTextForChat()).getSiblings());
			else
				list1.add(ichat);
		}
		for (IChatComponent ichat : list1) {
			String[] str = ichat.getFormattedText().split(
					String.format(WITH_DELIMITER, " "));
			List<String> chatList = new ArrayList<String>();
			for (String s : str) {
				for (String s1 : (List<String>) fontRenderer
						.listFormattedStringToWidth(s, limit)) {
					chatList.add(StringUtils.stripControlCodes( s1 ) );
				}
			}
			// Create component and add to list
			for (String s : chatList) {
				IChatComponent a = new ChatComponentText(s).setChatStyle(ichat
						.getChatStyle().createShallowCopy());
				ichatList.add(a);
			}
		}
		// Assemble lines
		ComponentList chatList = ComponentList.newInstance();
		IChatComponent newChat = new ChatComponentText("");
		for (IChatComponent ichat : ichatList) {
			if (fontRenderer.getStringWidth(newChat.getUnformattedText()
					+ ichat.getUnformattedText()) <= limit) {
				newChat.appendSibling(ichat);
				continue;
			} else {
				chatList.add(newChat.appendSibling(new ChatComponentText("")));
				newChat = new ChatComponentText("").appendSibling(ichat);
			}
		}
		if (!chatList.contains(newChat))
			chatList.add(newChat);

		return chatList;
	}

	/**
	 * Convert legacy formatting
	 * @param chat
	 * @return
	 */
	public static IChatComponent formattedStringToChat(String chat) {

		IChatComponent newChat = new ChatComponentText("");
		String[] parts = chat.split("\u00a7");
		boolean first = true;
		for (String part : parts) {
			if (first) {
				first = false;
				newChat.appendText(part);
				continue;
			}
			IChatComponent last = (IChatComponent) newChat.getSiblings().get(
					newChat.getSiblings().size() - 1);
			EnumChatFormatting format = null;
			for (EnumChatFormatting formats : EnumChatFormatting.values()) {
				if (String.valueOf(formats.getFormattingCode()).equals(
						part.substring(0, 1)))
					format = formats;
			}

			if (format != null) {
				IChatComponent chat1 = new ChatComponentText(part.substring(1));
				if (format.equals(EnumChatFormatting.RESET)) {
					chat1.getChatStyle().setColor(EnumChatFormatting.WHITE);
					chat1.getChatStyle().setBold(false);
					chat1.getChatStyle().setItalic(false);
					chat1.getChatStyle().setObfuscated(false);
					chat1.getChatStyle().setStrikethrough(false);
					chat1.getChatStyle().setUnderlined(false);
				} else {
					chat1.setChatStyle(last.getChatStyle().createDeepCopy());
				}
				if (format.isColor())
					chat1.getChatStyle().setColor(format);
				if (format.equals(EnumChatFormatting.BOLD))
					chat1.getChatStyle().setBold(true);
				if (format.equals(EnumChatFormatting.ITALIC))
					chat1.getChatStyle().setItalic(true);
				if (format.equals(EnumChatFormatting.UNDERLINE))
					chat1.getChatStyle().setUnderlined(true);
				if (format.equals(EnumChatFormatting.OBFUSCATED))
					chat1.getChatStyle().setObfuscated(true);
				if (format.equals(EnumChatFormatting.STRIKETHROUGH))
					chat1.getChatStyle().setStrikethrough(true);

				newChat.appendSibling(chat1);

			} else {
				last.appendText("\u00a7" + part);
			}

		}
		return newChat;
	}
	
	public static IChatComponent subComponent(IChatComponent chat, int index){
		IChatComponent result = new ChatComponentText("");
		
		int pos = 1;
		boolean found = false;
		for(IChatComponent ichat : getRecursiveSiblings(chat)){
			String text = ichat.getUnformattedText();
			if(text.length() + pos >= index && !found){
				found = true;
				IChatComponent local = new ChatComponentText(text.substring(pos - index));
				local.setChatStyle(ichat.getChatStyle().createDeepCopy());
				
				result.appendSibling(local);
			} else if (!found)
				pos += text.length();
			 else 
				result.appendSibling(ichat);
			
		}
		System.out.println(result.getUnformattedText());
		return result;
	}

	public static IChatComponent reverseSubComponent(IChatComponent chat, int index) {
		System.out.println(chat.getUnformattedText().substring(index));
		IChatComponent result = new ChatComponentText("");

		int pos = 1;
		boolean found = false;
		for (IChatComponent ichat : getRecursiveSiblings(chat)) {
			String text = ichat.getUnformattedText();        
			if (text.length() + pos >= index && !found) {
				found = true;
				IChatComponent local = new ChatComponentText(text.substring(0, index - pos));
				local.setChatStyle(ichat.getChatStyle().createDeepCopy());

				result.appendSibling(local);
				return chat;
			} else {
				result.appendSibling(ichat);
				pos += text.length();
			}
		}
		System.out.println(result.getUnformattedText());
		return result;
	}
	
	public static IChatComponent subComponent(IChatComponent chat, int start, int end){
		//IChatComponent result = reverseSubComponent(subComponent(chat, start), end);
		//System.out.println(chat.getUnformattedText().substring(start, end));
		return chat.createCopy();
	}

	public static ComponentList getRecursiveSiblings(IChatComponent chat){
		ComponentList list = ComponentList.newInstance();
		List<IChatComponent> siblings = chat.getSiblings();
		//TabbyChatUtils.log.info(chat.getSiblings().size());
		if(siblings.size() == 0){
			list.add(chat);
		} else {
			for(IChatComponent sib : siblings)
				list.addAll(getRecursiveSiblings(sib));
		}
		return list;
	}
	
}
