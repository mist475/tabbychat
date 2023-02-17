package acs.tabbychat.gui;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.LinkedHashMap;

public class ChatBox {
    public static Rectangle current = new Rectangle(0, -36, 320, 180);
    public static Rectangle desired = new Rectangle(current);
    public static int absMinX = 0;
    public static int absMinY = -36;
    public static int absMinW = 200;
    public static int absMinH = 24;
    public static int unfocusedHeight = 160;
    public static boolean dragging = false;
    public static boolean resizing = false;
    public static boolean anchoredTop = false;
    public static boolean pinned = false;
    protected static int tabTrayHeight = 14;
    private static final int tabHeight = 14;
    private static int chatHeight = 165;
    private static Point dragStart = new Point(0, 0);
    private static final GuiNewChatTC gnc = GuiNewChatTC.getInstance();

    /**
     * Adds a row to the tab tray
     */
    public static void addRowToTray() {
        // Grow virtual screen width/height to counter reduced size due to
        // chat scaling
        float sf = gnc.getScaleSetting();
        int sh = MathHelper.floor_float((gnc.sr.getScaledHeight() + current.y) / sf - current.y);

        // Add tab row to tray
        tabTrayHeight += tabHeight;

        if (current.height + tabHeight - absMinY > sh) {
            // Check if box is too tall for screen
            // Constrain box height to screen, stick to top
            current.y = anchoredTop ? -sh + 1 : -sh + 1 + current.height;
            current.height = sh + absMinY - 3;
        }
        else if (!anchoredTop && current.y - current.height - tabHeight - 1 < -sh) {
            // Tray needs to slide up, but can't
            current.y = -sh + current.height + 1;
            current.height += tabHeight;
        }
        else if (anchoredTop && current.y + current.height + tabHeight > absMinY) {
            // Tray needs to slide down, but can't
            current.height += tabHeight;
            current.y = absMinY - current.height;
        }
        else { // Tray/chatbox is free to move either way
            // expand tray/box
            current.height += tabHeight;
        }
    }

    /**
     * Draws the chat box border
     */
    public static void drawChatBoxBorder(Gui overlay, boolean chatOpen, int opacity) {
        int borderColor = 0x000000 + (2 * opacity / 3 << 24);
        int trayColor = 0x000000 + (opacity / 3 << 24);
        int highlightColor = 0xffffa0 + (2 * opacity / 3 << 24);
        int handleColor = resizeHovered() ? highlightColor : borderColor;
        int pinColor = pinHovered() ? highlightColor : borderColor;

        if (chatOpen) {
            if (!anchoredTop) {
                // Draw border around entire chat area
                Gui.drawRect(-1, -current.height - 1, current.width + 1, -current.height,
                             borderColor);
                Gui.drawRect(-1, -current.height, 0, 0, borderColor);
                Gui.drawRect(current.width, -current.height, current.width + 1, 0, borderColor);
                Gui.drawRect(-1, 0, current.width + 1, 1, borderColor);

                // Draw border between focused chatbox and tab tray
                Gui.drawRect(0, -current.height + tabTrayHeight, current.width, -current.height
                        + tabTrayHeight + 1, borderColor);

                // Add shading to tab tray
                Gui.drawRect(0, -current.height, current.width, -current.height + tabTrayHeight,
                             trayColor);

                // Draw filler for extra chat space
                Gui.drawRect(0, -current.height + tabTrayHeight + 1, current.width
                        - ChatScrollBar.barWidth - 2, -chatHeight, opacity / 2 << 24);

                // Draw handle for mouse drag
                Gui.drawRect(current.width - 7, -current.height + 2, current.width - 2,
                             -current.height + 3, handleColor);
                Gui.drawRect(current.width - 3, -current.height + 3, current.width - 2,
                             -current.height + 7, handleColor);

                // Draw pin button
                Gui.drawRect(current.width - 14, -current.height + 2, current.width - 9,
                             -current.height + 7, pinColor);
                if (pinned)
                    Gui.drawRect(current.width - 13, -current.height + 3, current.width - 10,
                                 -current.height + 6, highlightColor);
            }
            else {
                // Draw border around entire chat area
                Gui.drawRect(-1, -1, current.width + 1, 0, borderColor);
                Gui.drawRect(-1, 0, 0, current.height, borderColor);
                Gui.drawRect(current.width, 0, current.width + 1, current.height, borderColor);
                Gui.drawRect(-1, current.height, current.width + 1, current.height + 1, borderColor);

                // Draw border between focused chatbox and tab tray
                Gui.drawRect(0, current.height - tabTrayHeight, current.width, current.height
                        - tabTrayHeight - 1, borderColor);

                // Add shading to tab tray
                Gui.drawRect(0, current.height, current.width, current.height - tabTrayHeight,
                             trayColor);

                // Draw filler for extra chat space
                Gui.drawRect(0, current.height - tabTrayHeight - chatHeight - 1, current.width
                        - ChatScrollBar.barWidth - 2, 0, opacity / 2 << 24);

                // Draw handle for mouse drag
                Gui.drawRect(current.width - 7, current.height - 2, current.width - 2,
                             current.height - 3, handleColor);
                Gui.drawRect(current.width - 3, current.height - 3, current.width - 2,
                             current.height - 7, handleColor);

                // Draw pin button
                Gui.drawRect(current.width - 14, current.height - 2, current.width - 9,
                             current.height - 7, pinColor);
                if (pinned)
                    Gui.drawRect(current.width - 13, current.height - 3, current.width - 10,
                                 current.height - 6, highlightColor);
            }
        }
        else if (unfocusedHeight > 0) {
            if (!anchoredTop) {
                // Draw border around unfocused chatbox
                Gui.drawRect(-1, -unfocusedHeight - 1, current.width + 1, -unfocusedHeight,
                             borderColor);
                Gui.drawRect(-1, -unfocusedHeight, 0, 0, borderColor);
                Gui.drawRect(current.width, -unfocusedHeight, current.width + 1, 0, borderColor);
                Gui.drawRect(-1, 0, current.width + 1, 1, borderColor);

                // Draw filler for scrollbar
                // overlay.drawRect(current.width-ChatScrollBar.barWidth-2,
                // -unfocusedHeight, current.width, 0, opacity / 2 << 24);
            }
            else {
                // Draw border around unfocused chatbox
                int y = getChatHeight() - unfocusedHeight;
                Gui.drawRect(-1, y + unfocusedHeight, current.width + 1, y + unfocusedHeight + 1,
                             borderColor);
                Gui.drawRect(-1, y + unfocusedHeight, 0, y, borderColor);
                Gui.drawRect(current.width, y + unfocusedHeight, current.width + 1, y, borderColor);
                Gui.drawRect(-1, y, current.width + 1, y - 1, borderColor);

                // Draw filler for scrollbar
                // overlay.drawRect(current.width-ChatScrollBar.barWidth-2,
                // unfocusedHeight, current.width, 0, opacity / 2 << 24);
            }
        }
    }

    /**
     * Restricts chat to screen boundary
     */
    public static void enforceScreenBoundary(Rectangle newBounds) {
        // Grow virtual screen width/height to counter reduced size due to chat
        // scaling
        float scaleSetting = gnc.getScaleSetting();
        int scaledWidth = Math.round((gnc.sr.getScaledWidth() - current.x) / scaleSetting
                                             + current.x);
        int scaledHeight = Math.round((gnc.sr.getScaledHeight() + current.y) / scaleSetting
                                              - current.y);

        current.setBounds(newBounds);
        if (gnc.sr.getScaledHeight() < -current.y)
            scaledHeight = gnc.sr.getScaledHeight();
        if (gnc.sr.getScaledWidth() < current.x)
            scaledWidth = gnc.sr.getScaledWidth();

        // Enforce minimum width/height
        if (current.height < absMinH)
            current.height = absMinH;
        if (current.width < absMinW)
            current.width = absMinW;

        // Enforce maximum width/height
        if (current.height > scaledHeight - 2) {
            current.height = scaledHeight - 2;
            if (anchoredTop)
                current.y = -scaledHeight + 1;
            else
                current.y = -scaledHeight + current.height + 1;
        }
        if (current.width > scaledWidth - 2) {
            current.width = scaledWidth - 2;
            current.x = 0;
        }

        // Enforce minimum x position
        if (current.x < absMinX + 1)
            current.x = absMinX + 1;

        // Enforce maximum x position (including width)
        if (current.x + current.width + 1 > scaledWidth) {
            // If resizing, reduce width to compensate
            if (resizing)
                current.width = scaledWidth - current.x - 1;
                // Otherwise, reduce x-location to compensate
            else
                current.x = scaledWidth - current.width - 1;
        }

        if (anchoredTop) {
            // Enforce minimum y position (top of screen)
            if (current.y - 1 < -scaledHeight) {
                current.y = -scaledHeight + 1;
            }
            // Enforce maximum y position (including height) (bottom of screen)
            else if (current.y + current.height + 1 > absMinY) {
                // If resizing, reduce height to compensate
                if (resizing)
                    current.height = absMinY - current.y - 1;
                    // Otherwise, increase y-location to compensate
                else
                    current.y = absMinY - current.height - 1;
            }
        }
        else {
            // Enforce maximum y position (bottom of screen)
            if (current.y + 1 > absMinY)
                current.y = absMinY - 1;
                // Enforce minimum y position (including height) (top of screen)
            else if (current.y - current.height - 1 < -scaledHeight) {
                // If resizing, reduce height to compensate
                if (resizing)
                    current.height = scaledHeight + current.y - 1;
                    // Otherwise, increase y-location to compensate
                else
                    current.y = current.height + 1 - scaledHeight;
            }
        }
    }

    /**
     * Returns chatbox height
     */
    public static int getChatHeight() {
        return current.height - tabTrayHeight - 1;
    }

    /**
     * Returns chatbox width
     */
    public static int getChatWidth() {
        if (gnc.getChatOpen())
            return current.width - ChatScrollBar.barWidth - 2;
        else
            return current.width;
    }

    /**
     * Returns the minimum chatbox width
     */
    public static int getMinChatWidth() {
        return current.width - ChatScrollBar.barWidth - 2;
    }

    /**
     * Returns the height of the chatbox while unfocused
     */
    public static int getUnfocusedHeight() {
        return unfocusedHeight;
    }

    /**
     * Sets unfocused chatbox height
     */
    public static void setUnfocusedHeight(int uHeight) {
        unfocusedHeight = Math.min(uHeight, (int) (TabbyChat.advancedSettings.chatBoxUnfocHeight
                .getValue() * getChatHeight() / 100.0f));
    }

    /**
     * Handles mouse dragging
     */
    public static void handleMouseDrag(int _curX, int _curY) {
        if (!dragging)
            return;

        Point click = scaleMouseCoords(_curX, _curY, true);
        if (Math.abs(click.x - dragStart.x) < 3 && Math.abs(click.y - dragStart.y) < 3)
            return;

        float scaleSetting = gnc.getScaleSetting();
        int scaledHeight = Math.round((gnc.sr.getScaledHeight() + current.y) / scaleSetting
                                              - current.y);

        desired.x = current.x + click.x - dragStart.x;
        desired.y = current.y + click.y - dragStart.y;

        // look for snapping to top
        if (desired.y - current.height < -scaledHeight + 1 && !anchoredTop) {
            anchoredTop = true;
            desired.y -= current.height;
        }
        // look for snapping to bottom
        else if (desired.y + current.height + 1 > absMinY && anchoredTop) {
            anchoredTop = false;
            desired.y += current.height;
        }

        desired.setSize(current.width, current.height);
        enforceScreenBoundary(desired);

        dragStart = click;
    }

    /**
     * Resize chatbox
     */
    public static void handleMouseResize(int _curX, int _curY) {
        if (!resizing)
            return;

        Point click = scaleMouseCoords(_curX, _curY, true);
        if (Math.abs(click.x - dragStart.x) < 3 && Math.abs(click.y - dragStart.y) < 3)
            return;

        desired.width = current.width + click.x - dragStart.x;
        desired.x = current.x;
        desired.y = current.y;
        if (!anchoredTop) {
            desired.height = current.height - click.y + dragStart.y;
        }
        else {
            desired.height = current.height + click.y - dragStart.y;
        }

        enforceScreenBoundary(desired);
        GuiNewChatTC.getInstance().refreshChat();
        dragStart = click;
    }

    /**
     * Returns if the chatbox is pinned
     */
    public static boolean pinHovered() {
        // Check for mouse cursor over pin button

        Point cursor = scaleMouseCoords(Mouse.getX(), Mouse.getY());
        if (cursor == null)
            return false;

        int rX = current.x + current.width - 15;
        int rY;
        if (anchoredTop)
            rY = current.y + current.height - 8;
        else
            rY = current.y - current.height;

        return (cursor.x > rX && cursor.x < rX + 6 && cursor.y > rY && cursor.y < rY + 8);
    }

    /**
     * Resizes the chatbox
     */
    public static boolean resizeHovered() {
        // Check for mouse cursor over resize handle

        Point cursor = scaleMouseCoords(Mouse.getX(), Mouse.getY());
        if (cursor == null)
            return false;

        int rX = current.x + current.width - 8;
        int rY;
        if (anchoredTop)
            rY = current.y + current.height - 8;
        else
            rY = current.y - current.height;

        return (cursor.x > rX && cursor.x < rX + 8 && cursor.y > rY && cursor.y < rY + 8);
    }

    /**
     * Sets chatbox height
     */
    public static void setChatSize(int height) {
        chatHeight = height;
    }

    public static void startDragging(int atX, int atY) {
        dragging = true;
        resizing = false;
        dragStart = scaleMouseCoords(atX, atY, true);
    }

    public static void startResizing(int atX, int atY) {
        dragging = false;
        resizing = true;
        dragStart = scaleMouseCoords(atX, atY, true);
    }

    public static boolean tabTrayHovered(int mx, int my) {
        boolean chatOpen = gnc.getChatOpen();
        GuiScreen theScreen = TabbyChat.mc.currentScreen;
        if (!chatOpen || theScreen == null)
            return false;

        Point click = scaleMouseCoords(mx, my);

        if (!anchoredTop) {
            return (click.x > current.x && click.x < current.x + current.width
                    && click.y > current.y - current.height && click.y < current.y - current.height
                    + tabTrayHeight);
        }
        else {
            return (click.x > current.x && click.x < current.x + current.width
                    && click.y > current.y + current.height - tabTrayHeight && click.y < current.y
                    + current.height);
        }
    }

    /**
     * Returns the current mouse coordinates
     */
    public static Point scaleMouseCoords(int _x, int _y) {
        return scaleMouseCoords(_x, _y, false);
    }

    /**
     * Returns the scaled mouse coordinates
     */
    public static Point scaleMouseCoords(int _x, int _y, boolean forGuiScreen) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen theScreen = mc.currentScreen;
        if (theScreen == null)
            return null;
        /*
         * transform LWJGL coordinate space (bottom-left) to Minecraft screen
         * coordinate space (top-left, scaled to GUI settings)
         */

        // transform to Minecraft GUI scale settings
        _x = _x * theScreen.width / mc.displayWidth;

        // transform to include chat scale setting and initial offset
        float chatScale = gnc.getScaleSetting();
        _x = Math.round((_x - current.x) / chatScale) + current.x;

        if (!forGuiScreen) {
            // transform to GuiNewChat coordinate space (bottom-to-top is 0 to
            // negative screen height)
            // including chat scale setting
            _y = -_y * theScreen.height / mc.displayHeight;
            // Subtract offset, scale, add offset back
            _y = Math.round((_y - current.y) / chatScale) + current.y;
        }
        else {
            // Apply set GUI scale, keep screen bottom as origin
            _y = _y * theScreen.height / mc.displayHeight;
            // Subtract offset, scale, add offset back
            _y = Math.round((_y + current.y) / chatScale) - current.y;
            // Flip to GuiScreen coordinate space - origin at top/left
            _y = theScreen.height - _y;
        }
        return new Point(_x, _y);
    }

    /**
     * Updates tabs
     */
    public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs) {
        int tabWidth;
        int tabX = current.x;
        int tabY = gnc.sr.getScaledHeight() + current.y
                + (anchoredTop ? current.height - tabTrayHeight : -current.height);
        int tabDx = 0;
        int rows = 0;

        // Reset tab tray height
        int moveY = tabTrayHeight - tabHeight;
        tabTrayHeight = tabHeight;
        current.height -= moveY;

        for (ChatChannel chan : chanObjs.values()) {
            tabWidth = TabbyChat.mc.fontRenderer.getStringWidth(chan.getAlias() + "<>") + 8;
            if (tabDx + tabWidth > current.width - 6 && tabWidth < current.width - 6) {
                rows++;
                if (tabHeight * (rows + 1) > tabTrayHeight) {
                    addRowToTray();
                }
                tabDx = 0;
                if (!anchoredTop) {
                    for (ChatChannel chan2 : chanObjs.values()) {
                        if (chan2 == chan)
                            break;
                        chan2.tab.y(chan2.tab.y() + tabHeight);
                    }
                }
            }

            if (chan.tab == null) {
                chan.setButtonObj(new ChatButton(chan.getID(), tabX + tabDx, gnc.sr
                        .getScaledHeight() + current.y, tabWidth, tabHeight, chan.getDisplayTitle()));
            }
            else {
                chan.tab.id = chan.getID();
                chan.tab.x(tabX + tabDx);
                chan.tab.y(tabY);
                if (anchoredTop)
                    chan.tab.y(chan.tab.y() + tabHeight * rows);
                chan.tab.width(tabWidth);
                chan.tab.height(tabHeight);
                chan.tab.displayString = chan.getDisplayTitle();
            }
            tabDx += tabWidth + 1;
        }
    }
}
