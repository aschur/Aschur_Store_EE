package management;

public class AppSetting {

	private String name;
	private String value;
	
	public AppSetting(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	private void setValue(String value) {
		this.value = value;
	}
	
	
	
}
