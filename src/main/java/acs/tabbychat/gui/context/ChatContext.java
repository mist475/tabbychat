package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class ChatContext extends GuiButton {

	ChatContextMenu parent;
	
	public ChatContext(){
		super(0, 0, 0, 75, 15, null);
		this.displayString = this.getDisplayString();
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y){
		Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, Integer.MIN_VALUE);
		drawBorders();
		if(getDisplayIcon() != null)
			drawTexturedModelRectFromIcon(xPosition + 2, yPosition + 2, getDisplayIcon(), yPosition + 16, xPosition + 16);
		this.drawString(mc.fontRenderer, this.displayString, xPosition + 22, yPosition + 3, 0xeeeeee);
	}
	
	protected void drawBorders(){
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + 1, -0xffffff);
		Gui.drawRect(xPosition, yPosition, xPosition + 1, yPosition + height, -0xffffff);
		Gui.drawRect(xPosition, yPosition + height, xPosition + width, yPosition + height - 1, -0xffffff);
		Gui.drawRect(xPosition + width, yPosition, xPosition + width - 1, yPosition + height, -0xffffff);
	}

	/**
	 * what happens when clicked
	 */
	public abstract void actionPreformed();
	
	/**
	 * The display string
	 */
	public abstract String getDisplayString();
	
	/**
	 * the display icon, may be null
	 */
	public abstract IIcon getDisplayIcon();
	
	/**
	 * Checks if the clicked location is vaild to place this menu.
	 * 
	 * @param x Mouse X position
	 * @param y Mouse Y position
	 */
	public abstract boolean isLocationValid(int x, int y);
	
	public ChatContextMenu getParent(){
		return this.parent;
	}

}
