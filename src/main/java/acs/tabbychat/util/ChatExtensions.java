package acs.tabbychat.util;

import java.util.ArrayList;
import java.util.List;

import acs.tabbychat.api.IChatExtension;

import com.google.common.collect.Lists;

public class ChatExtensions {

	private List<IChatExtension> list = Lists.newArrayList();
	
	public ChatExtensions(List<Class<? extends IChatExtension>> list){
		for(Class<? extends IChatExtension> ext : list){
			try {
				IChatExtension exten = ext.newInstance();
				exten.load();
				this.list.add(exten);
			} catch (Exception e){
				TabbyChatUtils.log.error("Unable to initialize " + ext.getName(), e);
			}
		}
	}
	
	public <T extends IChatExtension> List<T> getListOf(Class<T> extClass){
		List<T> t = Lists.newArrayList();
		for(IChatExtension ext : list)
			if(extClass.isInstance(ext))
				t.add((T)ext);
		return t;
	}
	
}
