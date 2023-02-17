package acs.tabbychat.core;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatButton;
import acs.tabbychat.util.ChatComponentUtils;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatChannel {
    protected static int nextID = 3600;
    private final ReentrantReadWriteLock chatListLock = new ReentrantReadWriteLock(true);
    private final Lock chatReadLock = this.chatListLock.readLock();
    private final Lock chatWriteLock = this.chatListLock.writeLock();
    public ChatButton tab;
    public boolean unread = false;
    @Expose
    public boolean active = false;
    @Expose
    public boolean notificationsOn = false;
    @Expose
    public boolean hidePrefix = false;
    @Expose
    public String cmdPrefix = "";
    protected boolean hasSpam = false;
    protected int spamCount = 1;
    @Expose
    protected int chanID = nextID + 1;
    private File logFile;
    @Expose
    private String title;
    @Expose
    private String alias;
    @Expose
    private ArrayList<TCChatLine> chatLog;

    public ChatChannel() {
        this.chanID = nextID;
        nextID++;
        this.chatLog = new ArrayList<>();
        this.notificationsOn = TabbyChat.generalSettings.unreadFlashing.getValue();
    }    // Caches the split chat. Has a short expiration so we update when we need
    // to. If problems persist, increase expiration.
    private final Supplier<List<TCChatLine>> supplier = Suppliers.memoizeWithExpiration(
            () -> ChatChannel.this.getSplitChat(true), 50, TimeUnit.MILLISECONDS);

    public ChatChannel(int _x, int _y, int _w, int _h, String _title) {
        this();
        this.tab = new ChatButton(this.chanID, _x, _y, _w, _h, _title);
        this.title = _title;
        this.alias = this.title;
        this.tab.channel = this;
        this.tab.width(TabbyChat.mc.fontRenderer.getStringWidth(this.alias + "<>") + 8);
    }

    /**
     * Constructor to create new channel with title "_title"
     */
    public ChatChannel(String _title) {
        this(3, 3, Minecraft.getMinecraft().fontRenderer.getStringWidth("<" + _title + ">") + 8,
             14, _title);
    }

    public void addChat(TCChatLine newChat, boolean visible) {
        this.chatWriteLock.lock();
        try {
            this.chatLog.add(0, newChat);
        }
        finally {
            this.chatWriteLock.unlock();
        }
        if (!this.title.equals("*") && this.notificationsOn && !visible)
            this.unread = true;
    }

    public void deleteChatLines(int id) {
        this.chatReadLock.lock();
        try {
            this.chatLog.removeIf(line -> line.getChatLineID() == id);
        }
        finally {
            this.chatReadLock.unlock();
        }
    }

    public boolean doesButtonEqual(GuiButton btnObj) {
        return (this.tab.id == btnObj.id);
    }

    public String getAlias() {
        return this.alias;
    }

    /**
     * Sets alias
     */
    public void setAlias(String _alias) {
        this.alias = _alias;
        this.tab.width(TabbyChat.mc.fontRenderer.getStringWidth(_alias + "<>") + 8);
    }

    public int getButtonEnd() {
        return this.tab.x() + this.tab.width();
    }

    public TCChatLine getChatLine(int index) {
        TCChatLine retVal;
        this.chatReadLock.lock();
        List<TCChatLine> lines = getSplitChat(false);
        try {
            retVal = lines.get(index);
        }
        finally {
            this.chatReadLock.unlock();
        }
        return retVal;
    }

    /**
     * Returns the size of the log
     */
    public int getChatLogSize() {
        int mySize;
        this.chatReadLock.lock();
        try {
            mySize = getSplitChat(false).size();
        }
        finally {
            this.chatReadLock.unlock();
        }
        return mySize;
    }

    private List<TCChatLine> getSplitChat(boolean force) {
        if (!force) {
            return supplier.get();
        }
        return ChatComponentUtils.split(this.chatLog, ChatBox.getChatWidth());
    }

    public int getID() {
        return this.chanID;
    }

    /**
     * Returns display title
     */
    public String getDisplayTitle() {
        if (this.active)
            return "[" + this.alias + "]";
        else if (this.unread)
            return "<" + this.alias + ">";
        else
            return this.alias;
    }

    /**
     * Returns title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the log file for the channel
     */
    public File getLogFile() {
        return this.logFile;
    }

    /**
     * Sets the log file for the channel.
     */
    public void setLogFile(File file) {
        logFile = file;
    }

    /**
     * Sets the button type
     */
    public void setButtonObj(ChatButton btnObj) {
        this.tab = btnObj;
        this.tab.channel = this;
    }

    @Override
    public String toString() {
        return this.getDisplayTitle();
    }

    /**
     * Clears tab
     */
    public void clear() {
        this.chatWriteLock.lock();
        try {
            this.chatLog.clear();
        }
        finally {
            this.chatWriteLock.unlock();
        }
        this.tab = null;
    }

    /**
     * Sets button location
     */
    public void setButtonLoc(int _x, int _y) {
        this.tab.x(_x);
        this.tab.y(_y);
    }

    /**
     * Logs to chat
     */
    protected void setChatLogLine(int ind, TCChatLine newLine) {
        this.chatWriteLock.lock();
        try {
            if (ind < this.chatLog.size())
                this.chatLog.set(ind, newLine);
            else
                this.chatLog.add(newLine);
        }
        finally {
            this.chatWriteLock.unlock();
        }
        GuiNewChatTC.getInstance().refreshChat();
    }

    public void removeChatLine(int pos) {
        this.chatWriteLock.lock();
        try {
            if (pos < this.chatLog.size() && pos >= 0) {
                this.chatLog.remove(pos);
            }
        }
        finally {
            this.chatWriteLock.unlock();
        }
        GuiNewChatTC.getInstance().refreshChat();
    }

    /**
     * Trims the log
     */
    public void trimLog() {
        TabbyChat tc = GuiNewChatTC.tc;
        if (tc == null || tc.serverDataLock.availablePermits() < 1)
            return;
        int maxChats = tc.enabled() ? Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory
                                                               .getValue()) : 100;
        this.chatWriteLock.lock();
        try {
            while (this.chatLog.size() > maxChats) {
                this.chatLog.remove(this.chatLog.size() - 1);
            }
        }
        finally {
            this.chatWriteLock.unlock();
        }
    }

    /**
     * Notify the user of unread chat messages.
     */
    public void unreadNotify(Gui _gui, int _opacity) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiNewChatTC gnc = GuiNewChatTC.getInstance();
        int tabY = this.tab.y() - gnc.sr.getScaledHeight() - ChatBox.current.y;
        tabY = ChatBox.anchoredTop ? tabY - ChatBox.getChatHeight() + ChatBox.getUnfocusedHeight()
                                   : tabY + ChatBox.getChatHeight() - ChatBox.getUnfocusedHeight() + 1;

        Gui.drawRect(this.tab.x(), tabY, this.tab.x() + this.tab.width(), tabY + this.tab.height(),
                     0x720000 + (_opacity / 2 << 24));
        GL11.glEnable(GL11.GL_BLEND);
        mc.ingameGUI.getChatGUI().drawCenteredString(mc.fontRenderer, this.getDisplayTitle(),
                                                     this.tab.x() + this.tab.width() / 2, tabY + 4, 16711680 + (_opacity << 24));
    }

    /**
     * Imports old channels
     */
    protected void importOldChat(ChatChannel oldChan) {
        if (oldChan == null || oldChan.chatLog.isEmpty())
            return;
        this.chatWriteLock.lock();
        try {
            for (TCChatLine oldChat : oldChan.chatLog) {
                if (oldChat == null || oldChat.statusMsg)
                    continue;
                this.chatLog.add(oldChat);
            }
        }
        finally {
            this.chatWriteLock.unlock();
        }
        this.trimLog();
    }


}
