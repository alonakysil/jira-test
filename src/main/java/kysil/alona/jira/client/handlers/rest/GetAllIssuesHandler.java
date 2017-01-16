package kysil.alona.jira.client.handlers.rest;

import kysil.alona.jira.client.RESTClient;
import kysil.alona.jira.client.wrappers.MenuItemWrapper;
import kysil.alona.jira.client.wrappers.Utils;

import java.io.BufferedReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.api.domain.BasicProject;

public class GetAllIssuesHandler extends BaseHandler {
	public GetAllIssuesHandler(RESTClient restClient, BufferedReader br) {
		super(restClient, br);
		this.setDescription("Get all issues");
	}

	@Override
	public void handle() {
		try {
			System.out.println("Wait please...");
			JSONArray projects = new JSONArray(this.getRestClient().sendRequest("/rest/api/2/project", "GET", null));
			ArrayList<MenuItemWrapper> menu = new ArrayList<MenuItemWrapper>();
			
			for (Object object : projects) {
				JSONObject project = new JSONObject(object.toString());
				String key = project.getString("key");
				String name = project.getString("name");
				menu.add(new MenuItemWrapper(key, name + " (" + key + ")"));
			}
			
			String projectKey = (String) Utils.collectMenuInput("Please select project from listed below:", menu);
			System.out.println("Wait please...");
			

			String data = getJSON_Body(projectKey);
			String jsonString = this.getRestClient().sendRequest("/rest/api/2/search", "GET", data);
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray issues = jsonObject.getJSONArray("issues");
			for (Object object : issues) {
				JSONObject issue = new JSONObject(object.toString());
				String key = issue.getString("key");
				JSONObject fields = issue.getJSONObject("fields");
				String summmary = fields.getString("summary");
				System.out.println(key + " - " + summmary);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getJSON_Body(String projectKey) {
		JsonObject query = Json.createObjectBuilder().add("jql", "project = " + projectKey).build();
		return query.toString();

	}

}
