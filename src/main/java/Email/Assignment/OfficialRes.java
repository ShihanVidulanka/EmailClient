package Email.Assignment;

public class OfficialRes extends Recipient {
	private String designation;
	
	public OfficialRes(String name, String eMail,String designation) {
		super(name, eMail);
		this.designation=designation;
	}
	
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
}
