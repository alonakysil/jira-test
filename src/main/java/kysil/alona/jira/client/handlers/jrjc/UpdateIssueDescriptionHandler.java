package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

public class UpdateIssueDescriptionHandler extends BaseHandler {

	public UpdateIssueDescriptionHandler(JiraRestClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Update specific issue description");
	}

	@Override
	public void handle() {
		try {
			System.out.println("Please enter issue key (like TT-15, HH-3, BVG-45):");
	    	String issueKey = this.getBr().readLine();
	    	System.out.println("Wait please...");
	    	
        	if (!getAllIsssuesKeys().contains(issueKey)) {
        		System.out.println("Wrong issue key. Please try again.");
        		return;
        	} 
	    	String oldDescription = this.getRestClient().getIssueClient().getIssue(issueKey).claim().getDescription();
	    	System.out.println("Old issue description:");
	    	System.out.println(oldDescription);
	    	System.out.println("Please enter new issue description:");
	    	String description = this.getBr().readLine();
	    	System.out.println("Wait please...");
	    	updateIssueDescription(issueKey, description);
	     	System.out.println("New issue description has been set successfully!");
		} catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	private void updateIssueDescription (String issueId, String description) {
		IssueInput issueInput = new IssueInputBuilder().setDescription(description).build();
		this.getRestClient().getIssueClient().updateIssue(issueId, issueInput).claim();
	}
	
	private List<String> getAllIsssuesKeys() {
		Iterable<Issue> allIssues = this.getRestClient().getSearchClient().searchJql(" ORDER BY key ").claim().getIssues();
    	List<String> keys = new ArrayList<String>();
    	for (Issue issue : allIssues) {
			keys.add(issue.getKey());
		}
		return keys;
	}

}
