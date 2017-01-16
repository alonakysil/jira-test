package kysil.alona.jira.client.wrappers;

public class BaseWrapper {
	private Object id;
	private String name;
	
	public BaseWrapper(Object id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Object getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
