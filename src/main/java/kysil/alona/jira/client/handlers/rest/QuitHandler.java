package kysil.alona.jira.client.handlers.rest;

import java.io.BufferedReader;
import java.io.IOException;

import kysil.alona.jira.client.RESTClient;

public class QuitHandler extends BaseHandler {
	
	public QuitHandler(RESTClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Quit application");
	}

	@Override
	public void handle() {
		System.out.println("Exit!");
		try {
			this.getBr().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
