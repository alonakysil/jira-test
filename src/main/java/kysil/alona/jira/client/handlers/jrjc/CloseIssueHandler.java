package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;

import kysil.alona.jira.client.wrappers.MenuItemWrapper;
import kysil.alona.jira.client.wrappers.Utils;

public class CloseIssueHandler extends BaseHandler {
	
	private final String TRANSITION_CLOSE = "close";


	public CloseIssueHandler(JiraRestClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Close specific issue");
	}

	@Override
	public void handle() {
		try {
            System.out.println("Wait please...");
        	Iterable<Resolution> resolutions = this.getRestClient().getMetadataClient().getResolutions().claim();
        	Iterable<Issue> allIssues = this.getRestClient().getSearchClient().searchJql(" ORDER BY key ").claim().getIssues();
        	List<String> keys = new ArrayList<String>();
        	for (Issue issue : allIssues) {
				keys.add(issue.getKey());
			}
        	System.out.println("Please enter issue key (like TT-15, HH-3, BVG-45):");
        	String issueKey = this.getBr().readLine();
        	if (!keys.contains(issueKey)) {
        		System.out.println("Wrong issue key. Please try again.");
        		return;
        	}        	
        	ArrayList<Transition> transitions = (ArrayList<Transition>) this.getRestClient().getIssueClient().getTransitions(this.getRestClient().getIssueClient().getIssue(issueKey).claim()).claim();
			for (Transition transition : transitions) {
				if (transition.getName().toLowerCase().contains(TRANSITION_CLOSE)) {
					ArrayList<MenuItemWrapper> menu = new ArrayList<MenuItemWrapper>();
		        	for (Resolution resolution : resolutions) {
		        		menu.add(new MenuItemWrapper(resolution.getId().toString(), resolution.getName()));
					}
		        	String resolutionId = (String) Utils.collectMenuInput("Please select resolution type from listed below ", menu);
		        	System.out.println("Please enter comment (not mandatory):");
		        	String comment = this.getBr().readLine();
		        	System.out.println("Wait please...");
		        	closeIssue(issueKey, transition.getId(), resolutionId, comment);
		        	System.out.println("Issue has been closed successfully!"); 
		        	return;
				} else {
					continue;
					
				}
			}
			System.out.println("Unable to close isuue. Issue has been already closed or does not have such workflow point");
        	
        	
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	
	private void closeIssue (String issueKey, int transitionId, String resolutionId, String comment) {
		 final Collection<FieldInput> fieldInputs;
		 fieldInputs = Arrays.asList(new FieldInput("resolution", ComplexIssueInputFieldValue.with("id", resolutionId)));
		 TransitionInput transitionInput;
		 if (null == comment || comment.isEmpty()) {
			 transitionInput = new TransitionInput(transitionId, fieldInputs);
		 } else {
			 transitionInput = new TransitionInput(transitionId, fieldInputs, Comment.valueOf(comment));
		 }
	     this.getRestClient().getIssueClient().transition(this.getRestClient().getIssueClient().getIssue(issueKey).claim(), transitionInput).claim();
	}

}
