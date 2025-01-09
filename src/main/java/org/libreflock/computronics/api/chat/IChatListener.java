package org.libreflock.computronics.api.chat;

import net.minecraftforge.event.ServerChatEvent;

public interface IChatListener {

	void receiveChatMessage(ServerChatEvent event);

	boolean isValid();
}
