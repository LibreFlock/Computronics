package org.libreflock.computronics.integration.charset;

import org.libreflock.computronics.integration.charset.audio.IntegrationCharsetAudio;
import org.libreflock.computronics.integration.charset.wires.IntegrationCharsetWires;
import org.libreflock.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class IntegrationCharset {

	public static IntegrationCharsetWires wires;
	public static IntegrationCharsetAudio audio;

	public void preInit() {
		if(Mods.API.hasAPI(Mods.API.CharsetWires)) {
			wires = new IntegrationCharsetWires();
		}
	}

	public void postInit() {
		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
			audio = new IntegrationCharsetAudio();
			audio.postInit();
		}
	}
}
