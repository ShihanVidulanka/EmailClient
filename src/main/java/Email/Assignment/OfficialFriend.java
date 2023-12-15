package Email.Assignment;

public class OfficialFriend extends OfficialRes implements Wishable{
	private String DateOfBirth;
	public OfficialFriend(String name, String eMail,String designation,String dateOfBirth ) {
		super(name, eMail,designation);
		this.DateOfBirth=dateOfBirth;
	}

	@Override
	public String SendBirthdayWish() {
		return  "Wish you a Happy Birthday.\nVidulanka";
	}
	@Override
	public String getDateOfBirth() {
		return DateOfBirth;
	}
}
