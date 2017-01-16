package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import java.util.ArrayList;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;

import kysil.alona.jira.client.wrappers.MenuItemWrapper;
import kysil.alona.jira.client.wrappers.Utils;

public class GetAllIssuesHandler extends BaseHandler {

	
	public GetAllIssuesHandler(JiraRestClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Get all issues");
	}

	@Override
	public void handle() {
		System.out.println("Wait please...");
    	Iterable<BasicProject> projects = this.getRestClient().getProjectClient().getAllProjects().claim();
		ArrayList<MenuItemWrapper> menu = new ArrayList<MenuItemWrapper>();
		for (BasicProject pr : projects) {
			menu.add(new MenuItemWrapper(pr.getKey(), pr.getName()+ " (" + pr.getKey() + ")"));
		}
		String projectKey = (String) Utils.collectMenuInput("Please select project from listed below:", menu);
		System.out.println("Wait please...");
		Iterable<Issue> issues = getAllIssues(projectKey);
		System.out.println(projectKey + " project issues:");
		for (Issue issue : issues) {
			System.out.println(issue.getKey() + " - " + issue.getSummary());
		}
 	}

	private Iterable<Issue> getAllIssues(String projectId) {
		String pId = projectId;
		Iterable<Issue> issues = this.getRestClient().getSearchClient().searchJql("project = " + pId  + " ORDER BY key").claim().getIssues();
		return issues;
	}
}
