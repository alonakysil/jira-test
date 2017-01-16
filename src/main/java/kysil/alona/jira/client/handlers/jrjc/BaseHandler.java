package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import com.atlassian.jira.rest.client.api.JiraRestClient;

import kysil.alona.jira.client.handlers.Handler;

public abstract class BaseHandler implements Handler {
	
	private JiraRestClient restClient;
	private BufferedReader br;
	private String description;
	
	public BaseHandler(JiraRestClient restClient, BufferedReader br) {
		super();
		this.restClient = restClient;
		this.br = br;
	}

	public JiraRestClient getRestClient() {
		return restClient;
	}

	public BufferedReader getBr() {
		return br;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
