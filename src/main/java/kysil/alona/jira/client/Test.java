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
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class Test {

	private static JiraRestClient restClient;
	private static RESTClient client;
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {		
        BufferedReader br = null;
        XMLConfiguration configRead = null;
        boolean completeConfiguration = false;
        String defaultUrl = null;
	    String defaultUsername = null;
	    String defaultPassword = null;
	    int connectionType;
		try {
			configRead = new XMLConfiguration("conf.xml");
			defaultUrl = configRead.getString("url");
		    defaultUsername = configRead.getString("username");
		    defaultPassword = configRead.getString("password");
		    completeConfiguration = true;
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
			System.out.println("Unable to read default settings!");
		}
       
        
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            String leaveEmpty = completeConfiguration ?  " (leave it empty to use DEFAULT)!" : "!";
            System.out.println("Welcome to Jira REST client!\nPlease enter your Jira server URL and credentials" 
            					+ leaveEmpty);
            while(true) {
            	System.out.println("URL:");
				
        		String url = br.readLine();
        		URI jiraServerUri;
    			if (null != url && !url.isEmpty()) {
        			jiraServerUri = URI.create(url);
        		} else {
        			jiraServerUri = URI.create(defaultUrl);
        		}
        		
        		System.out.println("Username:");
        		String username = br.readLine();
        		if (null == username || username.isEmpty()) {
        			username = defaultUsername;
        		} 
        		
        		System.out.println("Password:");
        		String password = br.readLine();
        		if (null == password || password.isEmpty()) {
        			password = defaultPassword;
        		} 
        		//TODO select con type
        		
        		ArrayList<MenuItemWrapper> selectConnectionTypeMenu = new ArrayList<MenuItemWrapper>();
    			selectConnectionTypeMenu.add(new MenuItemWrapper(1, "JRJC"));
    			selectConnectionTypeMenu.add(new MenuItemWrapper(2, "REST"));
    			connectionType = (int) Utils.collectMenuInput("Please select connection type from listed below:", selectConnectionTypeMenu);
    			System.out.println("Connecting...");
    			if (1 == connectionType) {
    				final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            		restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
            		try {
            			restClient.getSessionClient().getCurrentSession().claim();
            			break;
            		}catch (Exception e) {
            			System.out.println("Bad data. Try again please.");
            			continue;
            		} 
    			} else { 
    				try { 
            			client = new RESTClient(jiraServerUri.getHost(), username, password);
            			break;
            		}catch (Exception e) {
            			System.out.println("Bad data. Try again please.");
            			continue;
            		}
            			
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
					int handlerNum = (int) Utils.collectMenuInput("Please select action from listed below:", menu);
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
