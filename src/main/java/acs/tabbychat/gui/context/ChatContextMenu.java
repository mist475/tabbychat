package acs.tabbychat.gui.context;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import acs.tabbychat.core.GuiChatTC;

import com.google.common.collect.Lists;

public class ChatContextMenu extends Gui {
	
	private static List<ChatContext> registered = Lists.newArrayList();
	private Minecraft mc = Minecraft.getMinecraft();
	private ScaledResolution sr;
	protected List<ChatContext> items;
	public ChatContextMenu parent;
	public GuiChatTC screen;
	public int xPos;
	public int yPos;
	public int width;
	public int height;
	
	public ChatContextMenu(GuiChatTC chat, int x, int y){
		this.items = registered;
		this.screen = chat;
		setup(chat, x, y);
	}
	
	private ChatContextMenu(ChatContextMenu parent, int x, int y, List<ChatContext> items){
		//this(parent.screen, x, y);
		this.parent = parent;
		this.items = items;
		this.screen = parent.screen;
		setup(screen, x, y);
	}
	
	private void setup(GuiChatTC chat, int x, int y){
		sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		this.xPos = x;
		this.yPos = y;
		this.width = 100;
		int xPos = x;
		if(x > sr.getScaledWidth() - width){
			if(this.parent == null)
				xPos = sr.getScaledWidth() - width;
			else
				xPos -= width*2;
		}
		int i = 0;
		for(ChatContext item : items){
			item.menu = this;
			item.enabled = item.isPositionValid(xPos, yPos);
			if(!item.enabled && item.getDisabledBehavior() == ChatContext.Behavior.HIDE)
				continue;
			item.id = i;
			item.xPosition = xPos;
			item.yPosition = this.yPos + i*15;
			i++;
		}
		this.height = 16*i;
		if(yPos > sr.getScaledHeight() - height){
			yPos = sr.getScaledWidth() - height;
			for(ChatContext item : items){
				item.yPosition -= height;
				if(this.parent != null)
					item.yPosition += 25;
			}
		}
		for(ChatContext item : items){
			if(item.getChildren() != null){
				item.children = new ChatContextMenu(this, item.xPosition + item.width, item.yPosition, item.getChildren());
			}
		}
		
	}
	
	public void drawMenu(int x, int y){
		for(ChatContext item : items){
			if(!item.enabled && item.getDisabledBehavior() == ChatContext.Behavior.HIDE)
				continue;
			item.drawButton(mc, x, y);
		}
	}
	
	public boolean mouseClicked(int mouseX, int mouseY){
		for(ChatContext item : items){
			if(!item.enabled)
				continue;
			if(item.isHoveredWithChildren(mouseX, mouseY)){
				return item.mouseClicked(mouseX, mouseY);
			}
		}
		return true;
	}
	
	public void buttonClicked(ChatContext item){
		item.onClicked();
	}
	
	public static void addContext(ChatContext item){
		registered.add(item);
	}
	
	public boolean isCursorOver(int x, int y) {
		boolean children = false;
		for(ChatContext cont : this.items){
			if(cont.isHoveredWithChildren(x,y) && cont.children != null){
				children = cont.children.isCursorOver(x, y);
			}
			if(children)
				break;
		}
		return (x > xPos &&
				x < xPos + width &&
				y > yPos &&
				y < yPos + height) ||
				children;
	}
	
	public static void insertContextAtPos(int pos, ChatContext item){
		registered.add(pos, item);
	}
	
	public static void removeContext(ChatContext item){
		registered.remove(item);
	}
	
	public static void removeContext(int pos){
		registered.remove(pos);
	}
	
	public static List<ChatContext> getRegisteredMenus(){
		return registered;
	}
}
