package acs.tabbychat.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;

import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatScrollBar;
import acs.tabbychat.util.ChatComponentUtils;
import acs.tabbychat.util.TabbyChatUtils;

public class GuiNewChatTC extends GuiNewChat {
    private Minecraft mc;
    public ScaledResolution sr;
    protected int chatWidth = 320;
    public int chatHeight = 0;
    public List<String> sentMessages;
    public List<TCChatLine> chatLines;
    public List<TCChatLine> backupLines;
    private static final ReentrantReadWriteLock chatListLock = new ReentrantReadWriteLock(true);
    private static final Lock chatReadLock = chatListLock.readLock();
    private static final Lock chatWriteLock = chatListLock.writeLock();
    private int scrollOffset = 0;
    public boolean chatScrolled = false;
    protected boolean saveNeeded = true;
    private static GuiNewChatTC instance = null;
    public static TabbyChat tc;
    public static Logger log = TabbyChatUtils.log;

    private GuiNewChatTC(Minecraft par1Minecraft) {
        super(par1Minecraft);
        this.mc = par1Minecraft;
        this.sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        GuiNewChatTC.tc = TabbyChat.getInstance();
    }

    public void addChatLines(int _pos, TCChatLine _add) {
        chatReadLock.lock();
        try {
            List<TCChatLine> lines = ChatComponentUtils.split(_add, this.chatWidth);
            for (int i = lines.size() - 1; i >= 0; i--) {

                this.chatLines.add(_pos, lines.get(i));
            }
            this.backupLines.add(_pos, _add);

        } finally {
            chatReadLock.unlock();
        }
    }

    /**
     * Adds chat lines to channel _addChan.
     * 
     * @param _addChan
     */
    public void addChatLines(ChatChannel _addChan) {
        chatReadLock.lock();
        try {
            for (int i = 0; i < _addChan.getChatLogSize(); i++) {
                this.chatLines.add(_addChan.getChatLine(i));
                this.backupLines.add(_addChan.getChatLine(i));
            }
        } finally {
            chatReadLock.unlock();
        }
    }

    @Override
    public void addToSentMessages(String _msg) {
        if (this.sentMessages.isEmpty()
                || !(this.sentMessages.get(this.sentMessages.size() - 1)).equals(_msg)) {
            this.sentMessages.add(_msg);
        }
    }

    public int chatLinesTraveled() {
        return this.scrollOffset;
    }

    /**
     * Clears recieved chat
     */
    public void clearChatLines() {
        this.resetScroll();
        chatWriteLock.lock();
        try {
            this.chatLines.clear();
        } finally {
            chatWriteLock.unlock();
        }
    }

    @Override
    public void clearChatMessages() {
        if (this.chatLines == null || this.backupLines == null)
            return;
        chatWriteLock.lock();
        try {
            this.chatLines.clear();
            this.backupLines.clear();
        } finally {
            chatWriteLock.unlock();
        }
        this.sentMessages.clear();
    }

    @Override
    public void deleteChatLine(int _id) {
        chatReadLock.lock();
        try {
            Iterator<TCChatLine> _iter = this.chatLines.iterator();
            ChatLine _cl;
            while (_iter.hasNext()) {
                _cl = _iter.next();
                if (_cl.getChatLineID() == _id) {
                    _iter.remove();
                }
            }
            _iter = this.backupLines.iterator();
            while (_iter.hasNext()) {
                _cl = _iter.next();
                if (_cl.getChatLineID() == _id) {
                    _iter.remove();
                }
            }
            tc.deleteChatLines(_id);
        } finally {
            chatReadLock.unlock();
        }
    }

    @Override
    public void drawChat(int currentTick) {
        if (!TabbyChat.liteLoaded && !TabbyChat.modLoaded)
            TabbyChatUtils.chatGuiTick(mc);

        // Save channel data if at main menu or disconnect screen, use flag so
        // it's only saved once
        if (mc.currentScreen != null) {
            if (this.mc.currentScreen instanceof GuiDisconnected
                    || this.mc.currentScreen instanceof GuiIngameMenu) {
                if (this.saveNeeded) {
                    tc.storeChannelData();
                    TabbyChat.advancedSettings.saveSettingsFile();
                }
                this.saveNeeded = false;
                return;
            } else {
                this.saveNeeded = true;
            }
        }

        this.sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);

        int lineCounter = 0;
        int visLineCounter = 0;
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int maxDisplayedLines = 0;
            boolean chatOpen = false;
            float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            ;
            float chatScaling = this.func_146244_h();
            int fadeTicks = 200;

            int numLinesTotal = 0;
            chatReadLock.lock();
            try {
                numLinesTotal = this.chatLines.size();
            } finally {
                chatReadLock.unlock();
            }
            chatOpen = this.getChatOpen();
            if (numLinesTotal == 0 && !chatOpen) {
                this.mc.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
                return;
            }

            if (tc.enabled()) {
                if (TabbyChat.generalSettings.timeStampEnable.getValue())

                    maxDisplayedLines = MathHelper.floor_float(ChatBox.getChatHeight() / 9.0f);
                if (!chatOpen)
                    maxDisplayedLines = MathHelper
                            .floor_float(TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue()
                                    * ChatBox.getChatHeight() / 900.0f);
                this.chatWidth = ChatBox.getChatWidth();
                fadeTicks = TabbyChat.advancedSettings.chatFadeTicks.getValue().intValue();
            } else {
                maxDisplayedLines = this.func_146232_i();
                this.chatWidth = MathHelper.ceiling_float_int(this.func_146228_f() / chatScaling);
            }
            GL11.glPushMatrix();
            if (tc.enabled()) {
                GL11.glTranslatef(ChatBox.current.x, 48.0f + ChatBox.current.y, 0.0f);
            } else {
                GL11.glTranslatef(2.0f, 29.0f, 0.0f);
            }
            GL11.glScalef(chatScaling, chatScaling, 1.0f);

            int lineAge;
            int currentOpacity = 0;
            List<TCChatLine> msgList;

            // Display valid chat lines
            for (lineCounter = 0; lineCounter + this.scrollOffset < numLinesTotal
                    && lineCounter < maxDisplayedLines; ++lineCounter) {
                msgList = new ArrayList<TCChatLine>();
                chatReadLock.lock();
                try {
                    msgList.add(this.chatLines.get(lineCounter + this.scrollOffset));
                    if (msgList.get(0) != null
                            && msgList.get(0).getChatComponentWithTimestamp().getFormattedText()
                                    .startsWith(" ")) {
                        for (int sameMsgCounter = 1; lineCounter + sameMsgCounter
                                + this.scrollOffset < numLinesTotal
                                && lineCounter + sameMsgCounter < maxDisplayedLines; ++sameMsgCounter) {
                            TCChatLine checkLine = this.chatLines.get(lineCounter + sameMsgCounter
                                    + this.scrollOffset);
                            if (checkLine.getUpdatedCounter() != msgList.get(0).getUpdatedCounter())
                                break;
                            msgList.add(checkLine);
                            if (!checkLine.getChatComponentWithTimestamp().getFormattedText()
                                    .startsWith(" "))
                                break;
                        }
                    }
                } finally {
                    chatReadLock.unlock();
                }
                if (msgList.isEmpty() || msgList.get(0) == null)
                    continue;
                lineCounter += msgList.size() - 1;
                lineAge = currentTick - msgList.get(0).getUpdatedCounter();
                if (lineAge < fadeTicks || chatOpen) {
                    if (!chatOpen) {
                        double agePercent = (double) lineAge / (double) fadeTicks;
                        agePercent = 10.0D * (1.0D - agePercent);
                        if (agePercent < 0.0D)
                            agePercent = 0.0D;
                        else if (agePercent > 1.0D)
                            agePercent = 1.0D;
                        agePercent *= agePercent;
                        currentOpacity = (int) (255.0D * agePercent);
                    } else {
                        currentOpacity = 255;
                    }
                    currentOpacity = (int) (currentOpacity * chatOpacity);
                    if (currentOpacity > 3) {
                        for (int i = 0; i < msgList.size(); i++) {
                            visLineCounter++;
                            byte xOrigin = 0;
                            int yOrigin = ChatBox.anchoredTop && tc.enabled() ? -(visLineCounter * 9)
                                    + ChatBox.getChatHeight()
                                    : -visLineCounter * 9;
                            drawRect(xOrigin, yOrigin, xOrigin + this.chatWidth, yOrigin + 9,
                                    currentOpacity / 2 << 24);
                            GL11.glEnable(GL11.GL_BLEND);
                            int idx = ChatBox.anchoredTop && tc.enabled() ? msgList.size() - i - 1
                                    : i;
                            String _chat = msgList.get(idx).getChatComponentWithTimestamp()
                                    .getFormattedText();

                            if (!this.mc.gameSettings.chatColours)
                                _chat = StringUtils.stripControlCodes(_chat);
                            int textOpacity = (TabbyChat.advancedSettings.textIgnoreOpacity
                                    .getValue() ? 255 : currentOpacity);
                            if (msgList.get(i).getUpdatedCounter() < 0) {
                                this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin,
                                        yOrigin + 1, 0x888888 + (textOpacity << 24));
                            } else
                                this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin,
                                        yOrigin + 1, 0xffffff + (textOpacity << 24));
                            GL11.glDisable(GL11.GL_ALPHA_TEST);
                        }
                    }
                }
            }
            this.chatHeight = visLineCounter * 9;
            if (tc.enabled()) {
                if (chatOpen) {
                    ChatBox.setChatSize(this.chatHeight);
                    ChatScrollBar.drawScrollBar();
                    ChatBox.drawChatBoxBorder(this, true, (int) (255 * chatOpacity));
                } else {
                    ChatBox.setUnfocusedHeight(this.chatHeight);
                    ChatBox.drawChatBoxBorder(this, false, currentOpacity);
                    tc.pollForUnread(this, currentTick);
                }
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public IChatComponent func_146236_a(int clickX, int clickY) {
        if (!this.getChatOpen())
            return null;
        else {
            IChatComponent returnMe = null;
            Point adjClick = ChatBox.scaleMouseCoords(clickX, clickY);
            int clickXRel = Math.abs(adjClick.x - ChatBox.current.x);
            int clickYRel = Math.abs(adjClick.y - ChatBox.current.y);
            if (clickXRel >= 0 && clickYRel >= 0 && clickXRel < this.chatWidth
                    && clickYRel < this.chatHeight) {
                chatReadLock.lock();
                try {
                    int displayedLines = Math.min(this.getHeightSetting() / 9,
                            this.chatLines.size());
                    if (clickXRel <= ChatBox.getChatWidth()
                            && clickYRel < this.mc.fontRenderer.FONT_HEIGHT * displayedLines
                                    + displayedLines) {
                        int lineIndex = clickYRel / this.mc.fontRenderer.FONT_HEIGHT
                                + this.scrollOffset;
                        if (lineIndex < displayedLines + this.scrollOffset
                                && this.chatLines.get(lineIndex) != null) {
                            TCChatLine chatline = this.chatLines.get(lineIndex);

                            clickYRel = 0;

                            Iterator<?> iter = chatline.getChatComponentWithTimestamp().iterator();
                            while (iter.hasNext()) {
                                returnMe = (IChatComponent) iter.next();
                                if (returnMe instanceof ChatComponentText) {
                                    clickYRel += this.mc.fontRenderer.getStringWidth(this
                                            .func_146235_b(((ChatComponentText) returnMe)
                                                    .getChatComponentText_TextValue()));

                                    if (clickYRel > clickXRel)
                                        return returnMe;
                                }

                            }
                        }
                    }
                } finally {
                    chatReadLock.unlock();
                }
            }
            return returnMe;
        }
    }

    private String func_146235_b(String p_146235_1_) {
        return Minecraft.getMinecraft().gameSettings.chatColours ? p_146235_1_ : EnumChatFormatting
                .getTextWithoutFormattingCodes(p_146235_1_);
    }

    private void func_146237_a(IChatComponent _msg, int id, int tick, boolean backupFlag) {

        boolean optionalDeletion = false;
        TCChatLine chatLine = new TCChatLine(tick, _msg, id);

        // Delete message if requested
        if (id != 0) {
            optionalDeletion = true;
            this.deleteChatLine(id);
        }

        MathHelper.floor_float(this.func_146228_f() / this.func_146244_h());
        if (tc.enabled()) {
            if (!backupFlag)
                tc.checkServer();
            ChatBox.getMinChatWidth();
        }
        if (TabbyChat.generalSettings.timeStampEnable.getValue())
            mc.fontRenderer.getStringWidth(TabbyChat.generalSettings.timeStampStyle.getValue()
                    .toString());

        // Add chatlines to appropriate lists
        if (tc.enabled() && !optionalDeletion && !backupFlag) {
            tc.processChat(chatLine);
            // refreshChat();
        } else {
            this.addChatLines(0, chatLine);
            tc.addToChannel("*", chatLine, true);
        }

        // Trim lists to size as needed
        if (tc.serverDataLock.availablePermits() < 1)
            return;
        int maxChats = tc.enabled() ? Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory
                .getValue()) : 100;
        int numChats = 0;
        chatReadLock.lock();
        try {
            numChats = this.chatLines.size();
        } finally {
            chatReadLock.unlock();
        }
        if (numChats <= maxChats)
            return;

        chatWriteLock.lock();
        try {
            while (this.chatLines.size() > maxChats) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
            if (!backupFlag) {
                while (this.backupLines.size() > maxChats) {
                    this.backupLines.remove(this.backupLines.size() - 1);
                }
            }
        } finally {
            chatWriteLock.unlock();
        }
    }

    @Override
    public void refreshChat() {
        // Chat settings have changed
        tc.resetDisplayedChat();
    }

    /**
     * Returns the number of messages received.
     * 
     * @return
     */
    public int GetChatSize() {
        int theSize = 0;
        chatReadLock.lock();
        try {
            theSize = this.chatLines.size();
        } finally {
            chatReadLock.unlock();
        }
        return theSize;
    }

    @Override
    public boolean getChatOpen() {
        return (this.mc.currentScreen instanceof GuiChat || this.mc.currentScreen instanceof GuiChatTC);
    }

    /**
     * Returns chat window height when unfocused
     * 
     * @return
     */
    public int getHeightSetting() {
        if (tc.enabled()) {
            return ChatBox.getChatHeight();
        } else
            return func_146243_b(this.mc.gameSettings.chatHeightFocused);
    }

    /**
     * Returns the current instance
     * 
     * @return instance
     */
    public static GuiNewChatTC getInstance() {
        if (instance == null) {
            instance = new GuiNewChatTC(Minecraft.getMinecraft());
            tc = TabbyChat.getInstance(instance);
            TabbyChatUtils.hookIntoChat(instance);
            if (!tc.enabled())
                tc.disable();
            else
                tc.enable();
        }
        return instance;
    }

    /**
     * Returns the scale settings
     * 
     * @return
     */
    public float getScaleSetting() {
        float theSetting = this.func_146244_h();
        return Math.round(theSetting * 100.0f) / 100.0f;
    }

    @Override
    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    /**
     * Merges chat lines
     * 
     * @param _new
     */
    public void mergeChatLines(ChatChannel _new) {
        int newSize = _new.getChatLogSize();
        chatWriteLock.lock();
        try {
            List<TCChatLine> _current = this.chatLines;
            if (_new == null || newSize <= 0)
                return;

            int _c = 0;
            int _n = 0;
            int dt = 0;
            while (_n < newSize && _c < _current.size()) {
                dt = _new.getChatLine(_n).getUpdatedCounter()
                        - _current.get(_c).getUpdatedCounter();
                if (dt > 0) {
                    _current.add(_c, _new.getChatLine(_n));
                    _n++;
                } else if (dt == 0) {
                    if (_current.get(_c).equals(_new.getChatLine(_n))
                            || _current.get(_c).getChatComponent()
                                    .equals(_new.getChatLine(_n).getChatComponent())) {
                        _c++;
                        _n++;
                    } else
                        _c++;
                } else
                    _c++;
            }

            while (_n < newSize) {
                _current.add(_current.size(), _new.getChatLine(_n));
                _n++;
            }
        } finally {
            chatWriteLock.unlock();
        }
    }

    @Override
    public void printChatMessage(IChatComponent _msg) {
        this.printChatMessageWithOptionalDeletion(_msg, 0);
    }

    @Override
    public void printChatMessageWithOptionalDeletion(IChatComponent _msg, int flag) {
        this.func_146237_a(_msg, flag, this.mc.ingameGUI.getUpdateCounter(), false);
        log.info("[CHAT] " + _msg.getUnformattedText());
    }

    @Override
    public void resetScroll() {
        this.scrollOffset = 0;
        this.chatScrolled = false;
    }

    @Override
    public void scroll(int _lines) {
        int maxLineDisplay;
        if (tc.enabled()) {
            maxLineDisplay = Math.round(ChatBox.getChatHeight() / 9.0f);
            if (!this.getChatOpen())
                maxLineDisplay = Math.round(maxLineDisplay
                        * TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f);
        } else
            maxLineDisplay = this.func_146232_i();

        this.scrollOffset += _lines;
        int numLines = 0;
        chatReadLock.lock();
        try {
            numLines = this.chatLines.size();
        } finally {
            chatReadLock.unlock();
        }
        this.scrollOffset = Math.min(this.scrollOffset, numLines - maxLineDisplay);
        if (this.scrollOffset <= 0) {
            this.scrollOffset = 0;
            this.chatScrolled = false;
        }
    }

    public void setVisChatLines(int _move) {
        this.scrollOffset = _move;
    }
}
