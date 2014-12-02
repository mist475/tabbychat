package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Extend this class to create a context menu.<br />
 * Call {@code ChatContextMenu.addContext(ChatContext)} to register<br />
 * Don't register children.
 */
public abstract class ChatContext extends GuiButton {

    ChatContextMenu menu;
    public ChatContextMenu children;
    protected boolean enabled;

    public ChatContext() {
        super(0, 0, 0, 100, 15, null);
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        if (!visible)
            return;
        if (getChildren() != null)
            children = new ChatContextMenu(this, xPosition + width, yPosition, getChildren());

        this.displayString = this.getDisplayString();
        if (!visible)
            return;
        Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1,
                getBackgroundColor(isHovered(x, y)));
        drawBorders();
        if (getDisplayIcon() != null)
            drawIcon();
        this.drawString(mc.fontRenderer, this.displayString, xPosition + 18, yPosition + 4,
                getStringColor());
        if (this.getChildren() != null) {
            // This has children.
            int length = mc.fontRenderer.getCharWidth('>');
            this.drawString(mc.fontRenderer, ">", xPosition + width - length, yPosition + 4,
                    getStringColor());
            for (ChatContext chat : children.items) {
                if (isHoveredWithChildren(x, y)) {
                    chat.visible = true;
                } else {
                    chat.visible = false;
                }
            }
            children.drawMenu(x, y);
        }
    }

    protected boolean isHovered(int x, int y) {
        return x >= xPosition && x <= xPosition + width && y >= yPosition
                && y <= yPosition + height;
    }

    protected boolean isHoveredWithChildren(int x, int y) {
        boolean hovered = isHovered(x, y);
        if (!hovered && getChildren() != null)
            for (ChatContext item : children.items) {
                if (item.visible)
                    hovered = item.isHoveredWithChildren(x, y);
                if (hovered)
                    break;
            }
        return hovered;
    }

    protected void drawIcon() {
        int x1 = xPosition + 4, y1 = yPosition + 3, x2 = x1 + 9, y2 = y1 + 9;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(getDisplayIcon());
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x1, y1, this.zLevel, 0, 0);
        tess.addVertexWithUV(x1, y2, this.zLevel, 0, 1);
        tess.addVertexWithUV(x2, y2, this.zLevel, 1, 1);
        tess.addVertexWithUV(x2, y1, this.zLevel, 1, 0);
        tess.draw();
    }

    protected void drawBorders() {
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + 1, -0xffffff);
        Gui.drawRect(xPosition, yPosition, xPosition + 1, yPosition + height, -0xffffff);
        Gui.drawRect(xPosition, yPosition + height, xPosition + width, yPosition + height - 1,
                -0xffffff);
        Gui.drawRect(xPosition + width, yPosition, xPosition + width - 1, yPosition + height,
                -0xffffff);

        Gui.drawRect(xPosition + height, yPosition, xPosition + height + 1, yPosition + height,
                -0xffffff);
    }

    private int getStringColor() {
        if (!enabled && getDisabledBehavior() == Behavior.GRAY)
            return 0x999999;
        return 0xeeeeee;
    }

    private int getBackgroundColor(boolean hovered) {
        if (hovered)
            return Integer.MIN_VALUE + 0x252525;
        else
            return Integer.MIN_VALUE;

    }

    protected boolean mouseClicked(int x, int y) {
        if (getChildren() == null) {
            this.onClicked();
            return true;
        } else {
            return children.mouseClicked(x, y);
        }
    }

    /**
     * what happens when clicked
     */
    public abstract void onClicked();

    /**
     * The display string
     */
    public abstract String getDisplayString();

    /**
     * the display icon, may be null
     */
    public abstract ResourceLocation getDisplayIcon();

    /**
     * This item's children. Shown when hovered or clicked.
     */
    public abstract List<ChatContext> getChildren();

    /**
     * Checks if the clicked location is vaild to place this menu.
     * 
     * @param x
     *            Mouse X position
     * @param y
     *            Mouse Y position
     */
    public abstract boolean isPositionValid(int x, int y);

    /**
     * How this item displays when {@code isPositionValid(int, int)} return
     * false.
     */
    public abstract Behavior getDisabledBehavior();

    public ChatContextMenu getMenu() {
        return this.menu;
    }

    public static enum Behavior {
        HIDE,
        GRAY;
    }

}
