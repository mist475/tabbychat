package acs.tabbychat.gui.context;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;

/**
 * Dummy context menu that includes setter methods and extra getter methods.
 */
public class ContextDummy extends ChatContext {

	private Method onClick;
	private Method isValid;
	private Behavior behavior = Behavior.GRAY;
	private String string;
	private ResourceLocation icon;
	private List<ChatContext> children;
	
	public ContextDummy(){
	}
	
	public ContextDummy(String display){
		this.displayString = display;
	}
	
	@Override
	public void onClicked() {
		if(onClick == null)
			return;
		try{
			onClick.invoke(null);
		}catch(Exception e){
			LogManager.getLogger().error(e);
		}
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return this.displayString;
	}

	@Override
	public ResourceLocation getDisplayIcon() {
		// TODO Auto-generated method stub
		return this.icon;
	}

	@Override
	public List<ChatContext> getChildren() {
		// TODO Auto-generated method stub
		return this.children;
	}

	@Override
	public boolean isPositionValid(int x, int y) {
		if(this.isValid == null)
			return false;
		try{
			return ((Boolean)isValid.invoke(null)).booleanValue();
		}catch(Exception e){
			LogManager.getLogger().error(e);
			return false;
		}
	}

	@Override
	public Behavior getDisabledBehavior() {
		// TODO Auto-generated method stub
		return this.behavior;
	}
	
	/**
	 * Sets the method that is invoked when clicked
	 * @param method Must be public static
	 */
	public void setOnClickMethod(Method method){
		this.onClick = method;
	}
	
	public Method getOnClickMethod(){
		return this.onClick;
	}
	
	/**
	 * Sets the method called to determine the if the current location is valid.
	 * If null, will return true.  If errors, returns false.
	 * @param method Must be public static and return boolean
	 */
	public void setIsValidMethod(Method method){
		this.isValid = method;
	}
	
	public Method getIsValidMethod(){
		return this.isValid;
	}
	
	private Method methodFromString(String method) throws NoSuchMethodException, SecurityException, ClassNotFoundException{
		String cl = method.substring(0,method.lastIndexOf('.')-1);
		String md = method.substring(method.lastIndexOf('.')+1);
		Class cass = Class.forName(cl);
		return cass.getMethod(md, new Class[0]);
		
	}
	
	public void setBehavior(Behavior behavior){
		this.behavior = behavior;
	}
	
	public void setDisplayIcon(ResourceLocation icon){
		this.icon = icon;
	}
	
	public void setDisplayString(String string){
		this.displayString = string;
	}
	
	public void setChildren(List<ChatContext> children){
		this.children = children;
	}

}
