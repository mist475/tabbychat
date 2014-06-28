package acs.tabbychat.api;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.API;

/**
 * Stores the registered extension classes.
 */
public class TCExtensionManager {

	public static final TCExtensionManager INSTANCE = new TCExtensionManager();
	private List<Class<? extends IChatExtension>> list = Lists.newArrayList();
	
	private TCExtensionManager(){}
	
	public List<Class<? extends IChatExtension>> getExtensions(){
		return list;
	}
	
	public void registerExtension(Class<? extends IChatExtension> ext){
		list.add(ext);
	}
	
	public void unregisterExtension(Class<? extends IChatExtension> ext){
		list.remove(ext);
	}
}
