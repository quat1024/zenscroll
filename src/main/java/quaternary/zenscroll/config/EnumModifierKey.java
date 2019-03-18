package quaternary.zenscroll.config;

import java.util.Locale;

//Accessed on serverside due to config stuffs, so nothing fancy to see in here
public enum EnumModifierKey {
	SHIFT,
	CTRL,
	ALT,
	NONE;
	
	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}
	
	public static EnumModifierKey byName(String name) {
		for(EnumModifierKey xd : values()) {
			if(xd.getName().equals(name)) return xd;
		}
		
		return null;
	}
	
	public static String[] allNames() {
		EnumModifierKey[] values = values();
		
		String[] names = new String[values.length];
		for(int i = 0; i < values.length; i++) {
			names[i] = values[i].getName();
		}
		
		return names;
	}
}
