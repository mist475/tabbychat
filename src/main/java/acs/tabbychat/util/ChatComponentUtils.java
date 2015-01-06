package acs.tabbychat.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import acs.tabbychat.core.TCChatLine;

import com.google.common.collect.Lists;

/**
 * A library for modifying Chat Components.
 * 
 * @author Matthew Messinger
 */
public class ChatComponentUtils {

    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    /**
     * Replaces all instances of the given word.
     * 
     * @param chat
     * @param regex
     * @param replacement
     * @return
     */
    @SuppressWarnings("unchecked")
    public static IChatComponent replaceText(IChatComponent chat, String regex, String replacement) {
        List<IChatComponent> iter = chat.getSiblings();
        IChatComponent newChat = new ChatComponentText("");
        for (IChatComponent next : iter) {
            IChatComponent comp = new ChatComponentText(next.getUnformattedText().replaceAll(regex,
                    replacement));
            comp.setChatStyle(next.getChatStyle().createShallowCopy());
            newChat.appendSibling(comp);
        }
        return newChat;
    }

    public static String formatString(String text, boolean force) {
        return !force && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting
                .getTextWithoutFormattingCodes(text) : text;
    }

    public static List<TCChatLine> split(List<TCChatLine> lines, int width) {
        List<TCChatLine> list = Lists.newArrayList();
        for (TCChatLine line : lines) {
            list.addAll(split(line, width));
        }
        return list;
    }

    public static List<TCChatLine> split(TCChatLine line, int width) {
        List<TCChatLine> list = Lists.newArrayList();
        List<IChatComponent> ichat = split(line.getChatComponentWithTimestamp(), width);
        for (IChatComponent chat : ichat) {
            list.add(0, new TCChatLine(line.getUpdatedCounter(), chat, line.getChatLineID()));
        }
        return list;
    }

    /**
     * Splits a ChatComponent into multiple lines so it will fit within a width.
     * Adapted from Minecraft 1.8's GuiUtilRenderComponents
     *
     * @param chat
     * @param limit
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<IChatComponent> split(IChatComponent chat, int limit) {

        int j = 0;
        ChatComponentText chatcomponenttext = new ChatComponentText("");
        List<IChatComponent> arraylist = Lists.newArrayList();
        ArrayList<IChatComponent> arraylist1 = Lists.newArrayList(chat);

        for (int k = 0; k < arraylist1.size(); ++k) {
            if (chatcomponenttext == null) {
                chatcomponenttext = new ChatComponentText(" ");
            }
            IChatComponent ichatcomponent1 = arraylist1.get(k);
            String s = ichatcomponent1.getUnformattedTextForChat();
            // Fix for when bad coders create chat using a null string
            if (s == null) {
                s = "";
            }
            boolean flag2 = false;
            String s1;

            if (s.contains("\n")) {
                int l = s.indexOf(10);
                s1 = s.substring(l + 1);
                s = s.substring(0, l + 1);
                ChatComponentText chatcomponenttext1 = new ChatComponentText(s1);
                chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                arraylist1.add(k + 1, chatcomponenttext1);
                flag2 = true;
            }
            // Second boolean v
            String s4 = formatString(ichatcomponent1.getChatStyle().getFormattingCode() + s, true);
            s1 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int j1 = fontRenderer.getStringWidth(s1);
            ChatComponentText chatcomponenttext2 = new ChatComponentText(s1);
            chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());

            if (j + j1 > limit) {
                String s2 = fontRenderer.trimStringToWidth(s4, limit - j, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && s3.length() > 0) {
                    int i1 = s2.lastIndexOf(" ");

                    if (i1 >= 0 && fontRenderer.getStringWidth(s4.substring(0, i1)) > 0) {
                        s2 = s4.substring(0, i1);
                        ++i1; // first boolean
                        s3 = s4.substring(i1);
                    } else if (j > 0 && !s4.contains(" ")) {
                        s2 = "";
                        s3 = s4;
                    }

                    ChatComponentText chatcomponenttext3 = new ChatComponentText(s3);
                    chatcomponenttext3.setChatStyle(ichatcomponent1.getChatStyle()
                            .createShallowCopy());
                    arraylist1.add(k + 1, chatcomponenttext3);
                }

                j1 = fontRenderer.getStringWidth(s2);
                chatcomponenttext2 = new ChatComponentText(s2);
                chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                flag2 = true;
            }

            if (j + j1 <= limit) {
                j += j1;
                chatcomponenttext.appendSibling(chatcomponenttext2);
            } else {
                flag2 = true;
            }

            if (flag2) {
                arraylist.add(chatcomponenttext);
                j = 0;
                chatcomponenttext = null;
            }
        }

        arraylist.add(chatcomponenttext);
        return arraylist;
    }

    /**
     * Convert legacy formatting
     * 
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
                if (String.valueOf(formats.getFormattingCode()).equals(part.substring(0, 1)))
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

    @SuppressWarnings("unchecked")
    public static IChatComponent subComponent(IChatComponent chat, int index) {
        IChatComponent result = new ChatComponentText("");

        int pos = 0;
        boolean found = false;
        Iterator<IChatComponent> iter = chat.iterator();
        while (iter.hasNext()) {
            IChatComponent ichat = iter.next();
            if (!ichat.getSiblings().isEmpty()) {
                continue;
            }
            String text = ichat.getUnformattedTextForChat();
            if (text.length() + pos >= index) {
                if (found)
                    result.appendSibling(ichat);
                else {
                    found = true;
                    IChatComponent local = new ChatComponentText(text.substring(index - pos));
                    local.setChatStyle(ichat.getChatStyle().createDeepCopy());

                    result.appendSibling(local);
                }
            }
            pos += text.length();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static IChatComponent subComponent(IChatComponent chat, int start, int end) {
        IChatComponent result = new ChatComponentText("");
        int pos = start;
        Iterator<IChatComponent> iter = ChatComponentStyle.createDeepCopyIterator(subComponent(
                chat, start).getSiblings());
        while (iter.hasNext()) {
            IChatComponent ichat = iter.next();
            if (!ichat.getSiblings().isEmpty()) {
                continue;
            }
            String text = ichat.getUnformattedTextForChat();
            if (pos + text.length() >= end) {
                IChatComponent local = new ChatComponentText(text.substring(0, end - pos));
                local.getChatStyle().setParentStyle(chat.getChatStyle().createDeepCopy());
                result.appendSibling(local);
                break;
            } else {
                result.appendSibling(ichat);
            }
            pos += text.length();

        }
        return result;
    }
}
