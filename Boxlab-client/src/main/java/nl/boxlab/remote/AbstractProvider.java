package nl.boxlab.remote;

import nl.boxlab.ClientContext;

public class AbstractProvider {
	
	private ClientContext context;

	public AbstractProvider(ClientContext context) {
		this.context = context;
	}
}
