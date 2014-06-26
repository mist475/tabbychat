package acs.tabbychat.gui.context;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import acs.tabbychat.core.GuiChatTC;

import com.google.common.collect.Lists;

public class ChatContextMenu extends Gui {
	
	private static List<ChatContext> items = Lists.newArrayList();
	private Minecraft mc = Minecraft.getMinecraft();
	private ScaledResolution sr;
	public boolean active;
	public GuiChatTC screen;
	public int xPos;
	public int yPos;
	public int width;
	public int height;
	
	public ChatContextMenu(GuiChatTC chat, int x, int y){
		sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		this.xPos = x;
		this.yPos = y;
		this.width = 100;
		int xPos = x;
		if(x > sr.getScaledWidth() - width)
			xPos = sr.getScaledWidth() - width;
		int i = 0;
		this.screen = chat;
		for(ChatContext item : items){
			//if(!item.isLocationValid(x, y))
			//	continue;
			item.id = i;
			item.parent = this;
			item.xPosition = xPos;
			item.yPosition = this.yPos + i*15;
			i++;
		}
		this.height = 16*i;
		if(y > sr.getScaledHeight() - height){
			yPos = sr.getScaledWidth() - height;
			for(ChatContext item : items){
				item.yPosition -= height;
			}
		}
		
	}
	
	public void drawMenu(int x, int y){
		//if(!active)
			//return;
		for(ChatContext item : items){
			if(!item.isPositionValid(this.xPos, this.yPos))
				continue;
			item.drawButton(mc, this.xPos, this.yPos + 20 * item.id);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY){
		for(ChatContext item : items){
			if(mouseX >= item.xPosition && mouseX <= item.xPosition + item.width && mouseY >= item.yPosition && mouseY <= item.yPosition + item.height){
				item.onClicked();
				return;
			}
		}
	}
	
	public void buttonClicked(ChatContext item){
		item.onClicked();
	}
	
	public static void addContext(Class<? extends ChatContext> item){
		try {
			items.add(item.newInstance());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isCursorOver(int x, int y) {
		return x >= xPos && x <= xPos + width && y >= yPos && y <= yPos + height;
	}
	
	public static void insertContextAtPos(int pos, ChatContext item){
		items.add(pos, item);
	}
	
	public static void removeContext(ChatContext item){
		items.remove(item);
	}
	
	public static void removeContext(int pos){
		items.remove(pos);
	}
	
	public static List<ChatContext> getContextList(){
		return items;
	}
}
