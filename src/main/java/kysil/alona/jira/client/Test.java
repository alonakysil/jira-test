package kysil.alona.jira.client;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import kysil.alona.jira.client.handlers.Handler;
import kysil.alona.jira.client.handlers.jrjc.CloseIssueHandler;
import kysil.alona.jira.client.handlers.jrjc.CreateIssueHandler;
import kysil.alona.jira.client.handlers.jrjc.GetAllIssuesHandler;
import kysil.alona.jira.client.handlers.jrjc.QuitHandler;
import kysil.alona.jira.client.handlers.jrjc.UpdateIssueDescriptionHandler;
import kysil.alona.jira.client.wrappers.MenuItemWrapper;
import kysil.alona.jira.client.wrappers.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class Test {

	private static JiraRestClient restClient;
	private static RESTClient client;
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {		
        BufferedReader br = null;
        int connectionType;       
        Configuration configuration = new Configuration("conf.xml");
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Welcome to Jira REST client!");
           boolean wrongCredentials = false; 
            
            while(true) {
            	URI jiraServerUri;
            	String username;
            	String password;
            	
            	if (wrongCredentials || !configuration.isConfigurationFull()) {
					System.out.println("Please enter your Jira server URL and credentials");

					System.out.println("URL:");
					String url = br.readLine();
					jiraServerUri = URI.create(url);

					System.out.println("Username:");
					username = br.readLine();

					System.out.println("Password:");
					password = br.readLine();
                } else {
                	jiraServerUri = URI.create(configuration.getUrl());
                	username = configuration.getUsername();
                	password = configuration.getPassword();
                }
            	
        		ArrayList<MenuItemWrapper> selectConnectionTypeMenu = new ArrayList<MenuItemWrapper>();
    			selectConnectionTypeMenu.add(new MenuItemWrapper(1, "JRJC"));
    			selectConnectionTypeMenu.add(new MenuItemWrapper(2, "custom REST"));
    			connectionType = (Integer) Utils.collectMenuInput("Please select connection type from listed below:", selectConnectionTypeMenu);
    			System.out.println("Connecting...");
				try {
					if (1 == connectionType) {
						final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
						restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
						restClient.getSessionClient().getCurrentSession().claim();
					} else {
						client = new RESTClient(jiraServerUri.getHost(), username, password);
					}
					wrongCredentials = false;
					break;
				} catch (Exception e) {
					wrongCredentials = true;
					System.out.println("Wrong credentials or site URL. Try again please.");
					continue;
				}
            }
            
            ArrayList<Handler> handlers = new ArrayList<Handler>();
            if (1 == connectionType) {
            	handlers.add(new GetAllIssuesHandler(restClient, br));
    			handlers.add(new UpdateIssueDescriptionHandler(restClient, br));
    			handlers.add(new CreateIssueHandler(restClient, br));
    			handlers.add(new CloseIssueHandler(restClient, br));
    			handlers.add(new QuitHandler(restClient, br));
            } else {
            	handlers.add(new kysil.alona.jira.client.handlers.rest.GetAllIssuesHandler(client, br));
            	handlers.add(new kysil.alona.jira.client.handlers.rest.QuitHandler(client, br));
            }

			
			
			ArrayList<MenuItemWrapper> menu = new ArrayList<MenuItemWrapper>();
			int i = 1;
			for (Handler handler : handlers) {
				menu.add(new MenuItemWrapper(i, handler.getDescription()));
				i++;
			}

			try {
				while (true) {				
					int handlerNum = (Integer) Utils.collectMenuInput("Please select action from listed below:", menu);
					handlers.get(handlerNum - 1).handle();
					System.out.println("-----------\n");
				}
			} finally {
				restClient.close();
			}

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}

}
