package Email.Assignment;

import java.time.LocalTime;

public class EmailStatRecorder implements ObserverObj {
	
	@Override
	public void update(LocalTime currentTime) {		//method of printing the notification informing email has received 

		System.out.println("Notification : an email is received at " +  currentTime.toString().substring(0, 8)  +".");
		
	}
	
}
