package kysil.alona.jira.client;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class Configuration {
	
	private String url;
    private String username;
    private String password;
    
	
		
	public Configuration(String fileName) {
		super();
		XMLConfiguration configRead = null;
        try {
			configRead = new XMLConfiguration(fileName);
			url = configRead.getString("url");
		    username = configRead.getString("username");
		    password = configRead.getString("password");
		} catch (ConfigurationException e1) {
			System.out.println("Unable to read default settings!");
		}
	}

	public boolean isConfigurationFull(){
		if (null == url 
        		|| url.isEmpty()
        		||null == username
        		|| username.isEmpty()
        		|| null == password
        		|| password.isEmpty()) {
			return false;
		}
		return true;		
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	

}
