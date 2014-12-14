package acs.tabbychat.gui.context;

import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.gui.ChatBox;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Point;
import java.util.List;

public class ChatContextMenu extends Gui {

    private static List<ChatContext> registered = Lists.newArrayList();
    private Minecraft mc = Minecraft.getMinecraft();
    private ScaledResolution sr;
    protected List<ChatContext> items;
    public ChatContext parent;
    public GuiChatTC screen;
    public int xPos;
    public int yPos;
    public int width;
    public int height;

    public ChatContextMenu(GuiChatTC chat, int x, int y) {
        this.items = registered;
        this.screen = chat;
        setup(chat, x, y);
    }

    ChatContextMenu(ChatContext parent, int x, int y, List<ChatContext> items) {
        // this(parent.screen, x, y);
        this.parent = parent;
        this.items = items;
        this.screen = parent.getMenu().screen;
        setup(screen, x, y);
    }

    private void setup(GuiChatTC chat, int x, int y) {
        sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.xPos = x;
        this.yPos = y;
        this.width = 100;
        if (x > sr.getScaledWidth() - width) {
            if (this.parent == null)
                xPos -= width;
            else
                xPos -= width * 2;
        }
        List<ChatContext> visible = Lists.newArrayList();
        for (ChatContext item : items) {
            item.menu = this;
            item.enabled = item.isPositionValid(xPos, yPos);
            if (!item.enabled && item.getDisabledBehavior() == ChatContext.Behavior.HIDE)
                continue;
            visible.add(item);
        }
        this.height = visible.size() * 15;
        if (yPos + height > sr.getScaledHeight()) {
            yPos -= height;
            if (this.parent != null)
                yPos += 15;
        }
        if (yPos < 0)
            yPos = 0;
        int i = 0;
        for (ChatContext item : visible) {
            item.id = i;
            item.xPosition = xPos;
            item.yPosition = yPos + i * 15;
            // if(item.yPosition + item.height > sr.getScaledHeight() ||
            // item.yPosition < 0)
            // item.visible = false;
            i++;
        }
    }

    public void drawMenu(int x, int y) {
        Point scaled = ChatBox.scaleMouseCoords(x, y, true);
        for (ChatContext item : items) {
            if (!item.enabled && item.getDisabledBehavior() == ChatContext.Behavior.HIDE)
                continue;
            item.drawButton(mc, scaled.x, scaled.y);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        for (ChatContext item : items) {
            if (!item.enabled)
                continue;
            if (item.isHoveredWithChildren(mouseX, mouseY)) {
                return item.mouseClicked(mouseX, mouseY);
            }
        }
        return false;
    }

    public void buttonClicked(ChatContext item) {
        item.onClicked();
    }

    public static void addContext(ChatContext item) {
        registered.add(item);
    }

    public boolean isCursorOver(int x, int y) {
        boolean children = false;
        for (ChatContext cont : this.items) {
            if (cont.isHoveredWithChildren(x, y) && cont.children != null) {
                children = cont.children.isCursorOver(x, y);
            }
            if (children)
                break;
        }
        return (x > xPos && x < xPos + width && y > yPos && y < yPos + height) || children;
    }

    public static void insertContextAtPos(int pos, ChatContext item) {
        registered.add(pos, item);
    }

    public static void removeContext(ChatContext item) {
        registered.remove(item);
    }

    public static void removeContext(int pos) {
        registered.remove(pos);
    }

    public static List<ChatContext> getRegisteredMenus() {
        return registered;
    }
}
