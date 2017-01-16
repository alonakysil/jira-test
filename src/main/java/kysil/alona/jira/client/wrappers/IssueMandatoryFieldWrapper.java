package kysil.alona.jira.client.wrappers;

import java.util.List;

public class IssueMandatoryFieldWrapper extends BaseWrapper {
	private Object value;
	private List<? extends Object> allowedValues;
	public IssueMandatoryFieldWrapper(Object id, String name, List<? extends Object> allowedValues) {
		super(id, name);
		this.allowedValues = allowedValues;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isValueAllowed(Object value) {
		if (null != allowedValues) {
			return allowedValues.contains(value);
		}
		return true;
	}
	

}
