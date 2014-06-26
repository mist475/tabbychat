package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class ChatContext extends GuiButton {

	private IIcon icon;
	ChatContextMenu parent;
	
	public ChatContext(){
		super(0, 0, 0, 60, 20, null);
		this.displayString = this.getDisplayString();
		this.icon = this.getDisplayIcon();
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y){
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x444444);
		drawString(mc.fontRenderer, this.displayString, xPosition, yPosition, 0xaaaaaa);
		if(icon != null)
			drawTexturedModelRectFromIcon(xPosition + 2, yPosition + 2, icon, yPosition + 16, xPosition + 16);
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
	 * @param x X-Mouse position
	 * @param y Y-Mouse position
	 */
	public abstract boolean isLocationValid(int x, int y);
	
	public ChatContextMenu getParent(){
		return this.parent;
	}

}
