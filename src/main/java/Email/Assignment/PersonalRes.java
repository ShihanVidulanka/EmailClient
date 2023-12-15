package Email.Assignment;

public class PersonalRes extends Recipient implements Wishable {
	private String DateOfBirth;
	private String nickName;
	public PersonalRes(String name, String eMail,String nickName,String dateOfBirth) {
		super(name, eMail);
		this.nickName=nickName;
		this.DateOfBirth=dateOfBirth;
	}
	public String getNickName() {
		return nickName;
	}
	@Override
	public String SendBirthdayWish() {
		// TODO Auto-generated method stub
		return  "Lots of love, hugs and best wishes to you on your birthday.\nVidulanka";
	}
	@Override
	public String getDateOfBirth() {
		return DateOfBirth;
	}


}
