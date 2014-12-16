package acs.tabbychat.util;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;

/**
 * Stores a List of IChatComponents and provides
 * methods useful to chat lists, such as merging.
 * 
 * @author Matthew Messinger
 */
@SuppressWarnings("serial")
public class ComponentList extends ArrayList<IChatComponent> {

    public static ComponentList newInstance() {
        return new ComponentList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IChatComponent chat : this) {
            sb.append(chat.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getUnformattedText() {
        StringBuilder sb = new StringBuilder();
        for (IChatComponent chat : this) {
            sb.append(chat.getUnformattedText().trim());
            sb.append(" ");
        }
        return sb.toString();
    }

    public String getFormattedText() {
        StringBuilder sb = new StringBuilder();
        for (IChatComponent chat : this) {
            sb.append(chat.getFormattedText().trim());
            sb.append(" ");
        }
        return sb.toString();
    }

    public IChatComponent merge() {
        IChatComponent chat = new ChatComponentText("");
        for (IChatComponent ichat : this) {
            chat.appendSibling(ichat);
        }
        return chat;
    }

}
