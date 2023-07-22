package acs.tabbychat.core;

import acs.tabbychat.api.IChatKeyboardExtension;
import acs.tabbychat.api.IChatMouseExtension;
import acs.tabbychat.api.IChatRenderExtension;
import acs.tabbychat.api.IChatUpdateExtension;
import acs.tabbychat.api.TCExtensionManager;
import acs.tabbychat.compat.MacroKeybindCompat;
import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatButton;
import acs.tabbychat.gui.ChatChannelGUI;
import acs.tabbychat.gui.ChatScrollBar;
import acs.tabbychat.gui.PrefsButton;
import acs.tabbychat.gui.context.ChatContextMenu;
import acs.tabbychat.util.ChatExtensions;
import acs.tabbychat.util.TabbyChatUtils;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tv.twitch.chat.ChatUserInfo;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiChatTC extends GuiChat {
    private static ScaledResolution sr;
    private final Logger log = TabbyChatUtils.log;
    private final List<String> foundPlayerNames = new ArrayList<>();
    public String historyBuffer = "";
    public String defaultInputFieldText = "";
    public List<GuiTextField> inputList = new ArrayList<>(3);
    public ChatScrollBar scrollBar;
    public TabbyChat tc;
    public GuiNewChatTC gnc;
    private boolean playerNamesFound = false;
    private boolean waitingOnPlayerNames = false;
    private int playerNameIndex = 0;
    private int spellCheckCounter = 0;
    private ChatContextMenu contextMenu;
    private ChatExtensions extensions;

    public GuiChatTC() {
        super();
        this.mc = Minecraft.getMinecraft();
        sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.gnc = GuiNewChatTC.getInstance();
        this.tc = TabbyChat.getInstance();
        this.extensions = new ChatExtensions(TCExtensionManager.INSTANCE.getExtensions());
    }

    public GuiChatTC(String par1Str) {
        this();
        this.defaultInputFieldText = par1Str;
    }

    @Override
    public void actionPerformed(GuiButton par1GuiButton) {
        // Attempts to send button to extensions.
        // If one returns true, stops here.
        for (IChatMouseExtension extension : extensions.getListOf(IChatMouseExtension.class)) {
            if (extension.actionPerformed(par1GuiButton))
                return;
        }

        if (!(par1GuiButton instanceof ChatButton _button))
            return;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && tc.channelMap.get("*") == _button.channel) {
            this.mc.displayGuiScreen(TabbyChat.generalSettings);
            return;
        }
        if (!this.tc.enabled())
            return;
        // Remove channel
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (_button.channel.active)
                this.tc.activatePrev();
            this.buttonList.remove(_button);
            this.tc.channelMap.remove(_button.channel.getTitle());
            // Select/Deselect channel
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            if (!_button.channel.active) {
                this.gnc.mergeChatLines(_button.channel);
                _button.channel.unread = false;
            }
            _button.channel.active = !_button.channel.active;
            if (!_button.channel.active)
                this.tc.resetDisplayedChat();
        }
        else {
            List<String> preActiveTabs = this.tc.getActive();
            for (ChatChannel chan : this.tc.channelMap.values()) {
                if (!_button.equals(chan.tab))
                    chan.active = false;
            }
            if (!_button.channel.active) {
                ChatScrollBar.scrollBarMouseWheel();
                if (preActiveTabs.size() == 1) {
                    this.checkCommandPrefixChange(this.tc.channelMap.get(preActiveTabs.get(0)),
                                                  _button.channel);
                }
                else {
                    _button.channel.active = true;
                    _button.channel.unread = false;
                }
            }
            this.tc.resetDisplayedChat();
        }
    }

    protected void addChannelLive(ChatChannel brandNewChan) {
        if (!this.buttonList.contains(brandNewChan.tab)) {
            this.buttonList.add(brandNewChan.tab);
        }
    }

    /**
     * Checks for command prefix change
     */
    public void checkCommandPrefixChange(ChatChannel oldChan, ChatChannel newChan) {
        String oldPrefix = oldChan.cmdPrefix.trim();
        String currentInput = this.inputField.getText().trim();
        if (currentInput.equals(oldPrefix) || currentInput.length() == 0) {
            String newPrefix = newChan.cmdPrefix.trim();
            if (newPrefix.length() > 0 && !newChan.hidePrefix)
                this.inputField.setText(newPrefix + " ");
            else
                this.inputField.setText("");
        }
        oldChan.active = false;
        newChan.active = true;
        newChan.unread = false;
    }

    /**
     * Completes player names
     */
    @Override
    public void func_146404_p_() {
        String textBuffer;
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1,
                                                                           this.inputField.getCursorPosition(), false)
                                                 - this.inputField.getCursorPosition());
            if (this.playerNameIndex >= this.foundPlayerNames.size()) {
                this.playerNameIndex = 0;
            }
        }
        else {
            int prevWordIndex = this.inputField.func_146197_a(-1,
                                                              this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.playerNameIndex = 0;
            String nameStart = this.inputField.getText().substring(prevWordIndex).toLowerCase();
            textBuffer = this.inputField.getText().substring(0,
                                                             this.inputField.getCursorPosition());
            this.func_146405_a(textBuffer);
            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(prevWordIndex - this.inputField.getCursorPosition());

        }

        if (this.foundPlayerNames.size() > 1) {
            int low = this.playerNameIndex - 1;
            int high = this.playerNameIndex + 4;
            if (low < 0) {
                int diff = Math.abs(low);
                low = 0;
                high += diff;
            }
            if (high >= this.foundPlayerNames.size()) {
                high = foundPlayerNames.size();
            }
            while (high - low < 5 && this.foundPlayerNames.size() >= 5) {
                low--;
            }
            if (high - low < 5) {
                low = 0;
            }
            List<String> newList = new ArrayList<>();
            for (int i = low; i < high; i++) {
                newList.add(this.foundPlayerNames.get(i));
            }

            int counter = low;
            StringBuilder _sb = new StringBuilder();
            for (Iterator<String> _iter = newList.iterator(); _iter
                .hasNext(); _sb
                     .append(textBuffer)) {
                textBuffer = _iter.next();
                if (counter == this.playerNameIndex + 1) {
                    _sb.append(EnumChatFormatting.RESET);
                }
                if (_sb.length() > 0) {
                    _sb.append(", ");
                }
                if (counter == this.playerNameIndex) {
                    _sb.append(EnumChatFormatting.BOLD);
                }
                counter++;
            }
            if (high < this.foundPlayerNames.size()) {
                _sb.append(" ...");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(
                new ChatComponentText(_sb.toString()), 1);
        }

        this.inputField.writeText(this.foundPlayerNames.get(this.playerNameIndex++));
    }

    @Override
    public void confirmClicked(boolean zeroId, int worldNum) {
        if (worldNum == 0) {
            if (zeroId)
                this.func_146407_a(this.clickedURI);
            this.clickedURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float pointless) {
        sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();

        // Calculate positions of currently-visible input fields
        int inputHeight = 0;
        for (GuiTextField guiTextField : this.inputList) {
            if (guiTextField.getVisible())
                inputHeight += 12;
        }

        // Draw text fields and background
        int bgWidth = (MacroKeybindCompat.present) ? this.width - 24 : this.width - 2;
        drawRect(2, this.height - 2 - inputHeight, bgWidth, this.height - 2, Integer.MIN_VALUE);
        for (GuiTextField field : this.inputList) {
            if (field.getVisible())
                field.drawTextBox();
        }

        // Draw current message length indicator
        if (this.tc.enabled()) {
            String requiredSends = ((Integer) this.getCurrentSends()).toString();
            int sendsX = sr.getScaledWidth() - 12;
            if (MacroKeybindCompat.present)
                sendsX -= 22;
            this.fontRendererObj.drawStringWithShadow(requiredSends, sendsX, this.height
                - inputHeight, 0x707070);
        }

        // Update & draw spell check data
        if (TabbyChat.spellingSettings.spellCheckEnable.getValue()
            && this.inputField.getText().length() > 0) {
            TabbyChat.spellChecker.drawErrors(this, this.inputList);
            if (this.spellCheckCounter == 200) {
                TabbyChat.spellChecker.update(this.inputList);
                this.spellCheckCounter = 0;
            }
            this.spellCheckCounter++;
        }

        // Update chat tabs (add to buttonlist)
        ChatBox.updateTabs(this.tc.channelMap);

        // Determine appropriate scaling for chat tab size and location
        float scaleSetting = this.gnc.getScaleSetting();
        GL11.glPushMatrix();
        float scaleOffsetX = ChatBox.current.x * (1.0f - scaleSetting);
        float scaleOffsetY = (this.gnc.sr.getScaledHeight() + ChatBox.current.y)
            * (1.0f - scaleSetting);
        GL11.glTranslatef(scaleOffsetX, scaleOffsetY, 1.0f);
        GL11.glScalef(scaleSetting, scaleSetting, 1.0f);

        // Deal with hover events
        IChatComponent icc = gnc.func_146236_a(Mouse.getX(), Mouse.getY());
        if (icc != null && icc.getChatStyle().getChatHoverEvent() != null) {
            HoverEvent hoverevent = icc.getChatStyle().getChatHoverEvent();
            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = null;
                try {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(hoverevent.getValue()
                                                                  .getUnformattedText());
                    if (nbtbase instanceof NBTTagCompound nbt)
                        itemstack = ItemStack.loadItemStackFromNBT(nbt);
                }
                catch (Exception ignored) {
                }
                if (itemstack != null)
                    this.renderToolTip(itemstack, cursorX, cursorY);
                else
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!",
                                                     cursorX, cursorY);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
                this.drawCreativeTabHoveringText(hoverevent.getValue().getFormattedText(), cursorX,
                                                 cursorY);
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                StatBase statbase = StatList.func_151177_a(hoverevent.getValue()
                                                               .getUnformattedText());

                if (statbase != null) {
                    IChatComponent icc1 = statbase.func_150951_e();
                    ChatComponentTranslation cct = new ChatComponentTranslation(
                        "stats.tooltip.type."
                            + (statbase.isAchievement() ? "achievement" : "statistics")
                    );
                    cct.getChatStyle().setItalic(true);
                    String s = statbase instanceof Achievement ? ((Achievement) statbase)
                        .getDescription() : null;
                    ArrayList<String> arraylist = Lists.newArrayList(icc1.getFormattedText(), cct.getFormattedText());

                    if (s != null)
                        arraylist.addAll(this.fontRendererObj.listFormattedStringToWidth(s, 150));
                    this.func_146283_a(arraylist, cursorX, cursorY);
                }
                else
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED
                                                         + "Invalid statistic/achievement!", cursorX, cursorY);
            }
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        for (GuiButton buttonTemp : this.buttonList) {
            if (buttonTemp instanceof PrefsButton && buttonTemp.id == 1) {
                if (mc.thePlayer != null && !mc.thePlayer.isPlayerSleeping()) {
                    this.buttonList.remove(buttonTemp);
                    continue;
                }
            }
            buttonTemp.drawButton(this.mc, cursorX, cursorY);
        }

        // Draw context menus
        if (this.contextMenu != null)
            this.contextMenu.drawMenu(Mouse.getX(), Mouse.getY());

        GL11.glPopMatrix();

        // Draw the screen for extensions
        for (IChatRenderExtension extension : extensions.getListOf(IChatRenderExtension.class)) {
            extension.drawScreen(cursorX, cursorY, pointless);
        }

    }


    /**
     * Adapted from GuiChat#func_146405_a(String,String)}
     * Was still named func_73893_a from earlier versions
     */
    private void func_146405_a(String nameStart) {
        if (nameStart.length() >= 1) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(nameStart));
            this.waitingOnPlayerNames = true;
        }
    }

    @Override
    public void func_146406_a(String[] par1ArrayOfStr) {
        if (this.waitingOnPlayerNames) {
            this.foundPlayerNames.clear();
            String[] _copy = par1ArrayOfStr;
            int _len = par1ArrayOfStr.length;

            for (int i = 0; i < _len; ++i) {
                String name = _copy[i];
                if (name.length() > 0) {
                    this.foundPlayerNames.add(name);
                    TabbyChat.spellChecker.addToIgnoredWords(name);
                }
            }

            if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.func_146404_p_();
            }
        }
    }

    private int getCurrentSends() {
        int lng = 0;
        int _s = this.inputList.size() - 1;
        for (int i = _s; i >= 0; i -= 1) {
            lng += this.inputList.get(i).getText().length();
        }
        if (lng == 0)
            return 0;
        else
            return (lng + 100 - 1) / 100;
    }

    private int getFocusedFieldIndex() {
        int _s = this.inputList.size();
        for (int i = 0; i < _s; i++) {
            if (this.inputList.get(i).isFocused() && this.inputList.get(i).getVisible())
                return i;
        }
        return 0;
    }

    private int getInputListSize() {
        int size = 0;
        for (GuiTextField field : inputList) {
            if (!field.getText().isEmpty()) {
                size++;
            }
        }
        return size;
    }

    @Override
    public void getSentHistory(int _dir) {
        int loc = this.sentHistoryCursor + _dir;
        int historyLength = this.gnc.getSentMessages().size();
        loc = Math.max(0, loc);
        loc = Math.min(historyLength, loc);
        if (loc == this.sentHistoryCursor)
            return;
        if (loc == historyLength) {
            this.sentHistoryCursor = historyLength;
            this.setText(new StringBuilder(historyBuffer), 1);
        }
        else {
            if (this.sentHistoryCursor == historyLength)
                this.historyBuffer = this.inputField.getText();
            StringBuilder _sb = new StringBuilder(this.gnc.getSentMessages().get(loc));
            this.setText(_sb, _sb.length());
            this.sentHistoryCursor = loc;
        }
    }

    @Override
    public void handleMouseInput() {
        // Allow chatbox dragging
        if (ChatBox.resizing) {
            if (!Mouse.isButtonDown(0))
                ChatBox.resizing = false;
            else
                ChatBox.handleMouseResize(Mouse.getEventX(), Mouse.getEventY());
            return;
        }
        else if (ChatBox.dragging) {
            if (!Mouse.isButtonDown(0))
                ChatBox.dragging = false;
            else
                ChatBox.handleMouseDrag(Mouse.getEventX(), Mouse.getEventY());
            return;
        }

        if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
            if (ChatBox.resizeHovered() && !ChatBox.dragging) {
                ChatBox.startResizing(Mouse.getEventX(), Mouse.getEventY());
            }
            else if (ChatBox.pinHovered()) {
                ChatBox.pinned = !ChatBox.pinned;
            }
            else if (ChatBox.tabTrayHovered(Mouse.getEventX(), Mouse.getEventY())
                && !ChatBox.resizing) {
                ChatBox.startDragging(Mouse.getEventX(), Mouse.getEventY());
            }
        }

        int wheelDelta = Mouse.getEventDWheel();
        if (wheelDelta != 0) {
            wheelDelta = Math.min(1, wheelDelta);
            wheelDelta = Math.max(-1, wheelDelta);
            if (!isShiftKeyDown())
                wheelDelta *= 7;

            this.gnc.scroll(wheelDelta);
            if (this.tc.enabled())
                ChatScrollBar.scrollBarMouseWheel();
        }
        else if (this.tc.enabled())
            ChatScrollBar.handleMouse();

        if (mc.currentScreen.getClass() != GuiChat.class)
            super.handleMouseInput();

        // let extensions handle mouse.
        for (IChatMouseExtension ext : extensions.getListOf(IChatMouseExtension.class)) {
            ext.handleMouseInput();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        // Refresh the extensions when the gui is started.
        this.extensions = new ChatExtensions(TCExtensionManager.INSTANCE.getExtensions());
        this.buttonList.clear();
        this.inputList.clear();
        sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();
        this.tc.checkServer();
        if (this.tc.enabled()) {
            if (this.scrollBar == null)
                this.scrollBar = new ChatScrollBar();
            for (ChatChannel chan : this.tc.channelMap.values()) {
                this.buttonList.add(chan.tab);
            }
        }
        else {
            this.buttonList.add(this.tc.channelMap.get("*").tab);
        }

        this.sentHistoryCursor = this.gnc.getSentMessages().size();
        int textFieldWidth = (MacroKeybindCompat.present) ? this.width - 26 : this.width - 4;
        String text = this.defaultInputFieldText;
        if (this.inputField != null)
            text = inputField.getText();
        this.inputField = new GuiTextField(this.fontRendererObj, 4, this.height - 12,
                                           textFieldWidth, 12);
        this.inputField.setMaxStringLength(500);
        this.inputField.setCanLoseFocus(false);
        this.inputField.setFocused(true);
        this.inputField.setText(text);
        this.inputField.setVisible(true);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputList.add(0, this.inputField);
        if (!tc.enabled())
            return;

        GuiTextField placeholder;
        for (int i = 1; i < 3; i++) {
            placeholder = new GuiTextField(this.fontRendererObj, 4, this.height - 12 * (i + 1),
                                           textFieldWidth, 12);
            placeholder.setMaxStringLength(500);
            placeholder.setCanLoseFocus(false);
            placeholder.setFocused(false);
            placeholder.setText("");
            placeholder.setVisible(false);
            placeholder.setEnableBackgroundDrawing(false);
            this.inputList.add(i, placeholder);
        }

        if (this.tc.enabled()) {
            List<String> activeTabs = this.tc.getActive();
            if (activeTabs.size() != 1) {
                this.inputField.setText("");
            }
            else {
                String thePrefix = this.tc.channelMap.get(activeTabs.get(0)).cmdPrefix.trim();
                boolean prefixHidden = this.tc.channelMap.get(activeTabs.get(0)).hidePrefix;
                if (thePrefix.length() > 0 && !prefixHidden && this.inputField.getText().isEmpty())
                    this.inputField.setText(this.tc.channelMap.get(activeTabs.get(0)).cmdPrefix
                                                .trim() + " ");
            }
            ChatBox.enforceScreenBoundary(ChatBox.current);
        }

        // Init the gui for extensions.
        for (IChatUpdateExtension extension : extensions.getListOf(IChatUpdateExtension.class)) {
            extension.initGui(this);
        }
    }

    /**
     * Inserts characters at cursor
     */
    private void insertCharsAtCursor(String _chars) {
        StringBuilder msg = new StringBuilder();
        int cPos = 0;
        boolean cFound = false;
        for (int i = this.inputList.size() - 1; i >= 0; i--) {
            msg.append(this.inputList.get(i).getText());
            if (this.inputList.get(i).isFocused()) {
                cPos += this.inputList.get(i).getCursorPosition();
                cFound = true;
            }
            else if (!cFound) {
                cPos += this.inputList.get(i).getText().length();
            }
        }
        if (this.fontRendererObj.getStringWidth(msg.toString())
            + this.fontRendererObj.getStringWidth(_chars) < (sr.getScaledWidth() - 20)
            * this.inputList.size()) {
            msg.insert(cPos, _chars);
            this.setText(msg, cPos + _chars.length());
        }
    }

    @Override
    public void keyTyped(char _char, int _code) {
        this.waitingOnPlayerNames = false;

        if (_code != Keyboard.KEY_TAB)
            this.playerNamesFound = false;
        switch (_code) {
            // TAB: execute vanilla name completion
            case Keyboard.KEY_TAB -> {
                if (GuiScreen.isCtrlKeyDown()) {
                    // CTRL+SHIFT+TAB: switch active tab to previous
                    if (GuiScreen.isShiftKeyDown()) {
                        tc.activatePrev();
                        // CTRL+TAB: switch active tab to next
                    }
                    else
                        tc.activateNext();
                    break;
                }
                this.func_146404_p_();
            }
            // ESCAPE: close the chat interface
            case Keyboard.KEY_ESCAPE -> this.mc.displayGuiScreen(null);

            // RETURN: send chat to server
            case Keyboard.KEY_NUMPADENTER, Keyboard.KEY_RETURN -> this.sendChat(ChatBox.pinned);

            // UP: if currently in multi-line chat, move into the above textbox.
            // Otherwise, go back one in the sent history (forced by Ctrl)
            case Keyboard.KEY_UP -> {
                if (GuiScreen.isCtrlKeyDown())
                    this.getSentHistory(-1);
                else {
                    int foc = this.getFocusedFieldIndex();
                    if (foc + 1 < this.inputList.size() && this.inputList.get(foc + 1).getVisible()) {
                        int gcp = this.inputList.get(foc).getCursorPosition();
                        int lng = this.inputList.get(foc + 1).getText().length();
                        int newPos = Math.min(gcp, lng);
                        this.inputList.get(foc).setFocused(false);
                        this.inputList.get(foc + 1).setFocused(true);
                        this.inputList.get(foc + 1).setCursorPosition(newPos);
                    }
                    else
                        this.getSentHistory(-1);
                }
            }
            // DOWN: if currently in multi-line chat, move into the below textbox.
            // Otherwise, go forward one in the sent history (force by Ctrl)
            case Keyboard.KEY_DOWN -> {
                if (GuiScreen.isCtrlKeyDown())
                    this.getSentHistory(1);
                else {
                    int foc = this.getFocusedFieldIndex();
                    if (foc - 1 >= 0 && this.inputList.get(foc - 1).getVisible()) {
                        int gcp = this.inputList.get(foc).getCursorPosition();
                        int lng = this.inputList.get(foc - 1).getText().length();
                        int newPos = Math.min(gcp, lng);
                        this.inputList.get(foc).setFocused(false);
                        this.inputList.get(foc - 1).setFocused(true);
                        this.inputList.get(foc - 1).setCursorPosition(newPos);
                    }
                    else
                        this.getSentHistory(1);
                }
            }
            // PAGE UP: scroll up through chat
            case Keyboard.KEY_PRIOR -> {
                this.gnc.scroll(19);
                if (this.tc.enabled())
                    ChatScrollBar.scrollBarMouseWheel();
            }
            // PAGE DOWN: scroll down through chat
            case Keyboard.KEY_NEXT -> {
                this.gnc.scroll(-19);
                if (this.tc.enabled())
                    ChatScrollBar.scrollBarMouseWheel();
            }
            // BACKSPACE: delete previous character, minding potential contents of
            // other input fields
            case Keyboard.KEY_BACK -> {
                if (this.inputField.isFocused() && this.inputField.getCursorPosition() > 0)
                    this.inputField.textboxKeyTyped(_char, _code);
                else
                    this.removeCharsAtCursor(-1);
            }
            // DELETE: delete next character, minding potential contents of other
            // input fields
            case Keyboard.KEY_DELETE -> {
                if (this.inputField.isFocused())
                    this.inputField.textboxKeyTyped(_char, _code);
                else
                    this.removeCharsAtCursor(1);
            }
            // LEFT/RIGHT: move the cursor
            case Keyboard.KEY_LEFT -> {
                int foc = this.getFocusedFieldIndex();
                if (foc < this.getInputListSize() - 1
                    && this.inputList.get(foc).getCursorPosition() == 0) {
                    this.inputList.get(foc).setFocused(false);
                    this.inputList.get(foc + 1).setFocused(true);
                    this.inputList.get(foc + 1).setCursorPosition(
                        inputList.get(foc + 1).getText().length());
                }
                this.inputList.get(this.getFocusedFieldIndex()).textboxKeyTyped(_char, _code);
            }
            case Keyboard.KEY_RIGHT -> {
                int foc1 = this.getFocusedFieldIndex();
                if (foc1 > 0
                    && this.inputList.get(foc1).getCursorPosition() >= this.inputList.get(foc1)
                    .getText().length()) {
                    this.inputList.get(foc1).setFocused(false);
                    this.inputList.get(foc1 - 1).setFocused(true);
                    this.inputList.get(foc1 - 1).setCursorPosition(0);
                }
                this.inputList.get(this.getFocusedFieldIndex()).textboxKeyTyped(_char, _code);
            }
            default -> {
                // CTRL + NUM1-9: Make the numbered tab active
                if (GuiScreen.isCtrlKeyDown() && !Keyboard.isKeyDown(Keyboard.KEY_LMENU)
                    && !Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
                    if (_code > 1 && _code < 12) {
                        tc.activateIndex(_code - 1);
                        // CTRL+O: open options
                    }
                    else if (_code == Keyboard.KEY_O) {
                        this.mc.displayGuiScreen(TabbyChat.generalSettings);
                    }
                    else {
                        this.inputField.textboxKeyTyped(_char, _code);
                    }
                    // Keypress will not trigger overflow, send to default input
                    // field
                }
                else if (this.inputField.isFocused()
                    && this.fontRendererObj.getStringWidth(this.inputField.getText()) < sr
                    .getScaledWidth() - 20) {
                    this.inputField.textboxKeyTyped(_char, _code);
                    // Keypress will trigger overflow, send through helper function
                }
                else {
                    this.insertCharsAtCursor(Character.toString(_char));
                }
            }
        }

        // pass keyTyped to extensions
        for (IChatKeyboardExtension ext : extensions.getListOf(IChatKeyboardExtension.class)) {
            ext.keyTyped(_char, _code);
        }
    }

    protected void sendChat(boolean keepopen) {
        StringBuilder _msg = new StringBuilder(1500);
        for (int i = this.inputList.size() - 1; i >= 0; i--) {
            _msg.append(this.inputList.get(i).getText());
        }
        if (_msg.toString().length() > 0) {
            TabbyChatUtils.writeLargeChat(_msg.toString());
            for (int i = 1; i < this.inputList.size(); i++) {
                this.inputList.get(i).setText("");
                this.inputList.get(i).setFocused(false);
            }
        }
        if (!tc.enabled() || !keepopen)
            this.mc.displayGuiScreen(null);
        else {
            this.resetInputFields();
        }
        this.sentHistoryCursor = this.gnc.getSentMessages().size() + 1;
    }

    @Override
    public void mouseClicked(int _x, int _y, int _button) {
        Point scaled = ChatBox.scaleMouseCoords(Mouse.getX(), Mouse.getY(), true);
        boolean clicked = false;
        if (scaled != null && _button == 0 && this.mc.gameSettings.chatLinks
            && (this.contextMenu == null || !contextMenu.isCursorOver(scaled.x, scaled.y))) {
            IChatComponent ccd = this.gnc.func_146236_a(Mouse.getX(), Mouse.getY());
            if (ccd != null) {
                ClickEvent clickEvent = ccd.getChatStyle().getChatClickEvent();
                if (clickEvent != null) {
                    if (isShiftKeyDown()) {
                        this.inputField.writeText(ccd.getChatStyle().getChatClickEvent()
                                                      .getValue());
                    }
                    else {
                        URI url;
                        if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                            try {
                                url = new URI(clickEvent.getValue());

                                if (this.mc.gameSettings.chatLinksPrompt) {
                                    this.clickedURI = url;
                                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this,
                                                                                    clickEvent.getValue(), 0, false));
                                }
                                else {
                                    this.func_146407_a(url);
                                }
                            }
                            catch (URISyntaxException var7) {
                                log.error("Can\'t open url for " + clickEvent, var7);
                            }
                        }
                        else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                            url = (new File(clickEvent.getValue())).toURI();
                            this.func_146407_a(url);
                        }
                        else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                            this.inputField.setText(clickEvent.getValue());
                        }
                        else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                            this.func_146403_a(clickEvent.getValue());
                        }
                        else if (clickEvent.getAction() == ClickEvent.Action.TWITCH_USER_INFO) {
                            ChatUserInfo var8 = this.mc.func_152346_Z().func_152926_a(
                                clickEvent.getValue());
                            if (var8 != null) {
                                this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc
                                                                                   .func_152346_Z(), var8));
                            }
                            else {
                                log.error("Tried to handle twitch user but couldn\'t find them!");
                            }
                        }
                        else {
                            log.error("Don\'t know how to handle " + clickEvent);
                        }
                    }
                }
                else {
                    URL url;
                    try {
                        url = new URL(ccd.getUnformattedText());
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedURI = url.toURI();
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, ccd
                                .getUnformattedText(), 0, false));
                        }
                        else {
                            this.func_146407_a(url.toURI());
                        }
                    }
                    catch (MalformedURLException | URISyntaxException ignored) {
                    }
                }
            }
        }
        else if (scaled != null && contextMenu != null && contextMenu.isCursorOver(scaled.x, scaled.y)) {
            clicked = !contextMenu.mouseClicked(scaled.x, scaled.y);
        }
        if (!clicked)
            if (scaled != null && _button == 1
                && (this.contextMenu == null || !this.contextMenu.isCursorOver(scaled.x,
                                                                               scaled.y))) {
                this.contextMenu = new ChatContextMenu(this, scaled.x, scaled.y);
            }
            else {
                this.contextMenu = null;
            }

        for (int i = 0; i < this.inputList.size(); i++) {
            if (_y >= this.height - 12 * (i + 1) && this.inputList.get(i).getVisible()) {
                this.inputList.get(i).setFocused(true);
                for (GuiTextField field : this.inputList) {
                    if (field != this.inputList.get(i))
                        field.setFocused(false);
                }
                this.inputList.get(i).mouseClicked(_x, _y, _button);
                break;
            }
        }

        // Pass click info to extensions
        if (!clicked)
            for (IChatMouseExtension extension : this.extensions
                .getListOf(IChatMouseExtension.class)) {
                if (extension.mouseClicked(_x, _y, _button))
                    return;
            }
        // Replicating GuiScreen's mouseClicked method since 'super' won't work
        for (GuiButton _guibutton : this.buttonList) {
            if (_guibutton instanceof ChatButton guiButton) {
                if (guiButton.mousePressed(this.mc, _x, _y)) {
                    if (_button == 0) {
                        this.selectedButton = guiButton;
                        this.mc.thePlayer.playSound("random.click", 1.0F, 1.0F);
                        this.actionPerformed(guiButton);
                        return;
                    }
                    else if (_button == 1) {
                        if (guiButton.channel == this.tc.channelMap.get("*"))
                            return;
                        this.mc.displayGuiScreen(new ChatChannelGUI(guiButton.channel));
                    }
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        ChatBox.dragging = false;
        ChatBox.resizing = false;
        gnc.resetScroll();

        // run onGuiClosed on extensions
        for (IChatUpdateExtension ext : extensions.getListOf(IChatUpdateExtension.class)) {
            ext.onGuiClosed();
        }
    }

    /**
     * Removes characters at cursor
     */
    private void removeCharsAtCursor(int _del) {
        StringBuilder msg = new StringBuilder();
        int cPos = 0;
        boolean cFound = false;
        for (int i = this.inputList.size() - 1; i >= 0; i--) {
            msg.append(this.inputList.get(i).getText());
            if (this.inputList.get(i).isFocused()) {
                cPos += this.inputList.get(i).getCursorPosition();
                cFound = true;
            }
            else if (!cFound) {
                cPos += this.inputList.get(i).getText().length();
            }
        }
        int other = cPos + _del;
        other = Math.min(msg.length() - 1, other);
        other = Math.max(0, other);
        if (other < cPos) {
            msg.replace(other, cPos, "");
            this.setText(msg, other);
        }
        else if (other > cPos) {
            msg.replace(cPos, other, "");
            this.setText(msg, cPos);
        }
    }

    /**
     * Resets input fields
     */
    private void resetInputFields() {
        for (GuiTextField gtf : this.inputList) {
            gtf.setText("");
            gtf.setFocused(false);
            gtf.setVisible(false);
        }
        this.inputField.setFocused(true);
        this.inputField.setVisible(true);

        List<String> actives = tc.getActive();
        if (actives.size() == 1) {
            ChatChannel current = tc.channelMap.get(actives.get(0));
            String pre = current.cmdPrefix.trim();
            boolean hidden = current.hidePrefix;
            if (pre.length() > 0 && !hidden) {
                this.inputField.setText(pre + " ");
            }
        }
        this.inputField.setCursorPositionEnd();
        this.sentHistoryCursor = this.gnc.getSentMessages().size();
    }

    private void setText(StringBuilder txt, int pos) {
        List<String> txtList = this.stringListByWidth(txt, sr.getScaledWidth() - 20);

        int strings = Math.min(txtList.size() - 1, this.inputList.size() - 1);
        for (int i = strings; i >= 0; i--) {
            this.inputList.get(i).setText(txtList.get(strings - i));
            if (pos > txtList.get(strings - i).length()) {
                pos -= txtList.get(strings - i).length();
                this.inputList.get(i).setVisible(true);
                this.inputList.get(i).setFocused(false);
            }
            else if (pos >= 0) {
                this.inputList.get(i).setFocused(true);
                this.inputList.get(i).setVisible(true);
                this.inputList.get(i).setCursorPosition(pos);
                pos = -1;
            }
            else {
                this.inputList.get(i).setVisible(true);
                this.inputList.get(i).setFocused(false);
            }
        }
        if (pos > 0) {
            this.inputField.setCursorPositionEnd();
        }
        if (this.inputList.size() > txtList.size()) {
            for (int j = txtList.size(); j < this.inputList.size(); j++) {
                this.inputList.get(j).setText("");
                this.inputList.get(j).setFocused(false);
                this.inputList.get(j).setVisible(false);
            }
        }
        if (!this.inputField.getVisible()) {
            this.inputField.setFocused(true);
            this.inputField.setVisible(true);
        }
    }

    private List<String> stringListByWidth(StringBuilder _sb, int _w) {
        List<String> result = new ArrayList<>(5);
        int _len = 0;
        int _cw;
        StringBuilder bucket = new StringBuilder(_sb.length());
        for (int ind = 0; ind < _sb.length(); ind++) {
            _cw = this.fontRendererObj.getCharWidth(_sb.charAt(ind));
            if (_len + _cw > _w) {
                result.add(bucket.toString());
                bucket = new StringBuilder(_sb.length());
                _len = 0;
            }
            _len += _cw;
            bucket.append(_sb.charAt(ind));
        }
        if (bucket.length() > 0)
            result.add(bucket.toString());
        return result;
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();

        // Update screen for extensions
        for (IChatUpdateExtension ext : extensions.getListOf(IChatUpdateExtension.class)) {
            ext.updateScreen();
        }
    }

}
