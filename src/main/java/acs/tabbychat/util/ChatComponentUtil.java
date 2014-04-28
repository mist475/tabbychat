package acs.tabbychat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import acs.tabbychat.settings.ColorCodeEnum;

import com.google.common.collect.Lists;

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
	public static IChatComponent[] split(IChatComponent chat, int limit) {
		// Split components up
		List<IChatComponent> ichatList = new ArrayList();
		List<IChatComponent> list = new ArrayList();
		List<IChatComponent> list1 = new ArrayList();
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
			List<String> chatList = new ArrayList();
			for (String s : str) {
				for (String s1 : (List<String>) fontRenderer
						.listFormattedStringToWidth(s, limit)) {
					chatList.add(StringUtils.stripControlCodes( s1 ) );
				}
			}
			// Create component and add to list
			for (String s : chatList) {
				IChatComponent a = new ChatComponentText(s).setChatStyle(ichat
						.getChatStyle().createDeepCopy());
				ichatList.add(a);
			}
		}
		// Assemble lines
		List<IChatComponent> chatList = new ArrayList();
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

		return chatList.toArray(new IChatComponent[0]);
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

	public static IChatComponent mergeChatComponent(IChatComponent[] icc) {
		if (icc.length == 0 || icc == null)
			return null;
		IChatComponent newChat = null;
		for (IChatComponent chat : icc) {
			if (newChat != null)
				newChat = newChat.appendSibling(chat);
			else
				newChat = chat;
		}

		return newChat;

	}

}
