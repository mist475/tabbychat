package acs.tabbychat.core;

import acs.tabbychat.util.TCChatLineFake;
import com.google.gson.annotations.Expose;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Date;

public class TCChatLine extends TCChatLineFake {
    @Expose
    public Date timeStamp;
    @Expose
    protected boolean statusMsg = false;

    public TCChatLine(int _counter, IChatComponent _string, int _id) {
        super(_counter, _string, _id);
    }

    public TCChatLine(ChatLine _cl) {
        super(_cl.getUpdatedCounter(), _cl.func_151461_a(), _cl.getChatLineID());
        if (_cl instanceof TCChatLine line) {
            timeStamp = line.timeStamp;
            statusMsg = line.statusMsg;
        }
    }

    public TCChatLine(int _counter, IChatComponent _string, int _id, boolean _stat) {
        this(_counter, _string, _id);
        this.statusMsg = _stat;
    }

    protected void setChatLineString(IChatComponent newLine) {
        this.chatComponent = newLine;
    }

    public IChatComponent getTimeStamp() {
        String format = TabbyChat.generalSettings.timeStamp.format(timeStamp);
        return new ChatComponentText(format + " ");
    }

    public IChatComponent getChatComponentWithTimestamp() {
        IChatComponent result = getChatComponent();
        if (TabbyChat.generalSettings.timeStampEnable.getValue() && timeStamp != null) {
            result = getTimeStamp().appendSibling(result);
        }
        return result;
    }
}
