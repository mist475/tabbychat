package acs.tabbychat.core;

import acs.tabbychat.util.TCChatLineFake;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TCChatLine extends TCChatLineFake implements Serializable {
	private static final long serialVersionUID = 646162627943686174L;
	protected boolean statusMsg = false;
	protected String timeStamp = "";
	/**
	 * 
	 * @param _counter
	 * @param _string
	 * @param _id
	 */
	public TCChatLine(int _counter, IChatComponent _string, int _id) {
		super(_counter, _string, _id);
	}
	/**
	 * 
	 * @param _cl
	 */
	public TCChatLine(ChatLine _cl) {
		super(_cl.getUpdatedCounter(), _cl.func_151461_a(), _cl.getChatLineID());
	}
	/**
	 * 
	 * @param _counter
	 * @param _string
	 * @param _id
	 * @param _stat
	 */
	public TCChatLine(int _counter, IChatComponent _string, int _id, boolean _stat) {
		this(_counter, _string, _id);
		this.statusMsg = _stat;
	}
	/**
	 * 
	 * @param newLine
	 */
	protected void setChatLineString(IChatComponent newLine) {
		this.lineString = newLine;
	}
	/**
	 * 
	 * @param _write
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream _write) throws IOException {
		_write.writeUTF(ChatComponentStyle.Serializer.func_150696_a(this.getChatLineString()));
		_write.writeBoolean(this.statusMsg);
		_write.writeUTF(this.timeStamp);
	}
	/**
	 * 
	 * @param _read
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream _read) throws IOException, ClassNotFoundException {
		this.updateCounterCreated = -1;
		this.lineString = ChatComponentStyle.Serializer.func_150699_a(_read.readUTF());
		this.statusMsg = _read.readBoolean();
		this.timeStamp = _read.readUTF();
	}
}
