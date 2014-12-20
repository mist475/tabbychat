package acs.tabbychat.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import acs.tabbychat.util.TCChatLineFake;

public class TCChatLine extends TCChatLineFake implements Serializable {
    private static final long serialVersionUID = 646162627943686174L;
    protected boolean statusMsg = false;
    public Date timeStamp;

    public TCChatLine(int _counter, IChatComponent _string, int _id) {
        super(_counter, _string, _id);
    }

    public TCChatLine(ChatLine _cl) {
        super(_cl.getUpdatedCounter(), _cl.func_151461_a(), _cl.getChatLineID());
    }

    public TCChatLine(int _counter, IChatComponent _string, int _id, boolean _stat) {
        this(_counter, _string, _id);
        this.statusMsg = _stat;
    }

    protected void setChatLineString(IChatComponent newLine) {
        this.lineString = newLine;
    }

    public IChatComponent getTimeStamp() {
        String format = TabbyChat.generalSettings.timeStamp.format(timeStamp);
        return new ChatComponentText(format + " ");
    }

    @Override
    public IChatComponent getChatLineString() {
        IChatComponent result = func_151461_a();
        if (TabbyChat.generalSettings.timeStampEnable.getValue() && timeStamp != null) {
            result = getTimeStamp().appendSibling(result);
        }
        return result;
    }

    private void writeObject(ObjectOutputStream _write) throws IOException {
        _write.writeUTF(ChatComponentStyle.Serializer.func_150696_a(this.func_151461_a()));
        _write.writeBoolean(this.statusMsg);
        _write.writeLong(this.timeStamp.getTime());
    }

    private void readObject(ObjectInputStream _read) throws IOException, ClassNotFoundException {
        this.updateCounterCreated = -1;
        this.lineString = ChatComponentStyle.Serializer.func_150699_a(_read.readUTF());
        this.statusMsg = _read.readBoolean();
        this.timeStamp = new Date(_read.readLong());
    }
}
