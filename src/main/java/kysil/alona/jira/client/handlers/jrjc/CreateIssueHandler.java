package kysil.alona.jira.client.handlers.jrjc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import kysil.alona.jira.client.wrappers.IssueMandatoryFieldWrapper;
import kysil.alona.jira.client.wrappers.MenuItemWrapper;
import kysil.alona.jira.client.wrappers.Utils;

public class CreateIssueHandler extends BaseHandler {
	private enum AlreadySettedFields {
		issuetype, 
		project, 
		reporter
	}
	
	private enum AdditionalFields {
		description
	}

	public CreateIssueHandler(JiraRestClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Create new issue");
	}
	
	

	@Override
	public void handle() {
		try {
			System.out.println("Wait please...");
        	Iterable<BasicProject> projects = this.getRestClient().getProjectClient().getAllProjects().claim();
        	Iterable <IssueType> issueTypes = this.getRestClient().getMetadataClient().getIssueTypes().claim();
        	Iterable<Priority> priorities = this.getRestClient().getMetadataClient().getPriorities().claim();
        	
        	
        	ArrayList<MenuItemWrapper> menu = new ArrayList<MenuItemWrapper>();
        	for (BasicProject pr : projects) {
				menu.add(new MenuItemWrapper(pr.getKey(), pr.getName()+ " (" + pr.getKey() + ")"));
			}
        	String projectKey = (String) Utils.collectMenuInput("Please select project from listed below:", menu);
        	
        	ArrayList<MenuItemWrapper> menu2 = new ArrayList<MenuItemWrapper>();
        	for (IssueType issueType : issueTypes) {
				menu2.add(new MenuItemWrapper(issueType.getId(), issueType.getName()));
			}
        	Long issueTypeId = (Long) Utils.collectMenuInput("Please select issue type from listed below:", menu2);
        	
        	System.out.println("Wait please...");       	
        	List<String> projectKeys = new ArrayList<String>();
			projectKeys.add(projectKey);			
			List<Long> issueTypeIds = new ArrayList<Long>();
			issueTypeIds.add(issueTypeId);			
			GetCreateIssueMetadataOptions  options = new GetCreateIssueMetadataOptionsBuilder()
			        .withExpandedIssueTypesFields()
			        .withIssueTypeIds(issueTypeIds)
			        .withProjectKeys(projectKeys).build();
			List<IssueMandatoryFieldWrapper> requiredFields = new ArrayList<IssueMandatoryFieldWrapper>();
			Iterable<CimProject> cimProjects = this.getRestClient().getIssueClient().getCreateIssueMetadata(options).claim();
			for (CimProject cimProject : cimProjects) {
				Iterable<CimIssueType> cimIissueTypes = cimProject.getIssueTypes();
				for (CimIssueType cimIssueType : cimIissueTypes) {
					for (Entry<String, CimFieldInfo> entry : cimIssueType.getFields().entrySet()) {						
						if ((entry.getValue().isRequired() && !Arrays.toString(AlreadySettedFields.values()).contains(entry.getKey())) 
								|| Arrays.toString(AdditionalFields.values()).contains(entry.getKey())) {
							if (entry.getValue().getId().equals("parent")) {
								requiredFields.add(new IssueMandatoryFieldWrapper(entry.getValue().getId(), entry.getValue().getName(), getAllIsssuesKeys()));
							} else {
								requiredFields.add(new IssueMandatoryFieldWrapper(entry.getValue().getId(), entry.getValue().getName(), null));
							}
						}
					}
				}
			}
			menu = new ArrayList<MenuItemWrapper>();
        	for (Priority priority : priorities) {
				menu.add(new MenuItemWrapper(priority.getId(), priority.getName()));
			}
        	Long priorityId = (Long) Utils.collectMenuInput("Please select priority from listed below:", menu);
			
			for (IssueMandatoryFieldWrapper field : requiredFields) {
				System.out.println("Please enter value for " + field.getName());
				String value = this.getBr().readLine();
				if(!field.isValueAllowed(value)) {
					System.out.println("Invalid value. Please try again.");
					return;
				}
				field.setValue(value);				
				
			}
			
        	System.out.println("Wait please...");
        	BasicIssue newIssue = createIssue(projectKey, issueTypeId, priorityId, requiredFields);
        	System.out.println("New issue has been created successfully!\n"
        			+ "New issue key is "
        			+ newIssue.getKey());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private BasicIssue createIssue (String projectKey, Long issueTypeId, Long priorityId, List<IssueMandatoryFieldWrapper> issueFields) {
		IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey, issueTypeId);
		issueInputBuilder.setPriorityId(priorityId);
		for (IssueMandatoryFieldWrapper issueMandatoryFieldWrapper : issueFields) {
			issueInputBuilder.setFieldValue((String) issueMandatoryFieldWrapper.getId(), issueMandatoryFieldWrapper.getValue());
		}
		BasicIssue issue = this.getRestClient().getIssueClient().createIssue(issueInputBuilder.build()).claim();
		return issue;
		
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
