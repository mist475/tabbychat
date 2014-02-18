package acs.tabbychat.util;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TCChatLineFake extends ChatLine {
	protected int updateCounterCreated;
	protected IChatComponent lineString;
	protected int chatLineID;

	public TCChatLineFake() {
		super(-1, new ChatComponentText(""), 0);
	}

	public TCChatLineFake(int _counter, IChatComponent _string, int _id) {
		super(_counter, _string, _id);
		this.updateCounterCreated = _counter;
		if(_string == null) this.lineString = new ChatComponentText("");
		else this.lineString = _string;
		this.chatLineID = _id;
	}

	public IChatComponent getChatLineString() {
		return this.lineString;
	}
	public int getUpdatedCounter() {
		return this.updateCounterCreated;
	}

	public int getChatLineID() {
		return this.chatLineID;
	}
}
