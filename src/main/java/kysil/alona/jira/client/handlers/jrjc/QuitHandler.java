package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import java.io.IOException;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public class QuitHandler extends BaseHandler {

	public QuitHandler(JiraRestClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Quit application");
	}

	@Override
	public void handle() {
		System.out.println("Exit!");
		try {
			this.getRestClient().close();
			this.getBr().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
