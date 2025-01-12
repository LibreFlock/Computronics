package org.libreflock.computronics.util;

/**
 * @author Vexatos
 */
// @SuppressWarnings("deprecation")
public class StringUtil {

	public static String localize(String key) {
		return net.minecraft.client.resources.I18n.get(key).replace("\\n", "\n");
	}

	public static String localizeAndFormat(String key, Object... formatting) {
		return String.format(net.minecraft.client.resources.I18n.get(key), formatting);
	}

	public static boolean canTranslate(String key) {
		return net.minecraft.client.resources.I18n.get(key).equals(key);
	}
}
