package Email.Assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class EmailStatPrinter implements ObserverObj {

	@Override
	public void update(LocalTime currentTime) {		//method of save the notification that informed email has received

		String notification ="Notification : an email is received at " + currentTime.toString().substring(0, 8) +".";

		FileWriter myWriter;                // store Notification in notification.txt file 
    	try {
    		myWriter = new FileWriter("notification.txt",true);
    		myWriter.write(notification+"\n");
    		myWriter.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
	}

}
