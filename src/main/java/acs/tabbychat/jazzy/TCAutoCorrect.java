package acs.tabbychat.jazzy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;

public class TCAutoCorrect {

	private String word;
	private Map<String, Float> words = new HashMap<String, Float>();
	private List similar;
	private SpellDictionary dictionary;

	private TCAutoCorrect() {
		InputStream in = TCAutoCorrect.class.getResourceAsStream("/english.0");
		try {
			dictionary = new SpellDictionaryHashMap(new InputStreamReader(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TCAutoCorrect(String str) {
		this();
		word = str;
		similar = dictionary.getSuggestions(word, 1);
	}

	public static TCAutoCorrect getDictionary(String str) {
		return new TCAutoCorrect(str);
	}

	public List getSimilar() {
		return similar;
	}

	public String getWord() {
		return word;
	}

}
