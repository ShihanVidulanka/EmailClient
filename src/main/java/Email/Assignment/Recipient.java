package Email.Assignment;

import java.io.Serializable;

public class Recipient implements Serializable {
	private String name;
	private String eMail;
	public Recipient(String name,String eMail){
		this.name=name;
		this.eMail=eMail;
		}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return eMail;
	}
	public void SetName(String name) {
		this.name=name;
	}
	public void SetEmail(String eMail) {
		this.eMail=eMail;
	}
	
}
