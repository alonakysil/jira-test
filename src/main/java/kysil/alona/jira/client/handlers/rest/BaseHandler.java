package kysil.alona.jira.client.handlers.rest;

import java.io.BufferedReader;

import kysil.alona.jira.client.RESTClient;
import kysil.alona.jira.client.handlers.Handler;

public abstract class BaseHandler implements Handler {
	RESTClient restClient;
	private BufferedReader br;
	private String description;

	public BaseHandler(RESTClient restClient, BufferedReader br) {
		super();
		this.restClient = restClient;
		this.br = br;

	}
	
	
	
	public RESTClient getRestClient() {
		return restClient;
	}



	public BufferedReader getBr() {
		return br;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
