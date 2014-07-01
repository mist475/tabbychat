package acs.tabbychat.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Stores the registered extension classes.
 */
public class TCExtensionManager {

	public static final TCExtensionManager INSTANCE = new TCExtensionManager();
	private List<Class<? extends IChatExtension>> list = Lists.newArrayList();
	
	private TCExtensionManager(){}
	
	public List<Class<? extends IChatExtension>> getExtensions(){
		return ImmutableList.copyOf(list);
	}
	
	public void registerExtension(Class<? extends IChatExtension> ext){
		if(!list.contains(ext))
			list.add(ext);
	}
	
	public void unregisterExtension(Class<? extends IChatExtension> ext){
		list.remove(ext);
	}
}
