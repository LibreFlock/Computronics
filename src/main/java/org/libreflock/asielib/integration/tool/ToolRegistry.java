package org.libreflock.asielib.integration.tool;

import org.libreflock.asielib.api.tool.IToolProvider;
import org.libreflock.asielib.api.tool.IToolRegistry;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Vexatos
 */
public class ToolRegistry implements IToolRegistry, Iterable<IToolProvider> {

	private final ArrayList<IToolProvider> toolProviders = new ArrayList<IToolProvider>();

	@Override
	public void registerToolProvider(IToolProvider provider) {
		toolProviders.add(provider);
	}

	@Override
	public Iterator<IToolProvider> iterator() {
		return toolProviders.iterator();
	}
}
