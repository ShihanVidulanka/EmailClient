package Email.Assignment;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class Email_Client {
	//make a static variable to get the count of recipient in the email client
	public static int CountOfrecipients;
	//create lists to store data of recipients ,sent e-mails,receive e-mails
	public static ArrayList<Recipient> ClientList = new ArrayList<Recipient>();
	public static ArrayList<Email> SentEmails = new ArrayList<Email>();
	public static ArrayList<Email> ReceiveEmails = new ArrayList<Email>();

	public static void main(String[] args) {
	
	//create objects of observes , blocking queue, email receiving thread and email save thread
    boolean run = true;
    ObserverObj[] observers = {new EmailStatPrinter(),new EmailStatRecorder()};
    Blocking_Queue  blockingQueue= new Blocking_Queue(5); 
    EmailReceiver_Thread receiveThread =new EmailReceiver_Thread(blockingQueue,observers);
    SaveReceiveEmail_Thread saveReceiveMail =new SaveReceiveEmail_Thread(blockingQueue);
    
    //get the e-mails that were sent before from the Email.ser file when the application starts
    DeserialiseEmail();
    //starting threads
    receiveThread.start();
    saveReceiveMail.start();
    
    //get details of recipients in Email-client 
    GetListOfRecipient();
    //code for set birthday wishes on today, when the email client open
    SendBirthdaywishes();
	

	//Generate a loop to choose options in multiple times
	while(run) {  
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("Enter option type: \n"
    			+ "1 - Adding a new recipient\n"
    			+ "2 - Sending an email\n"
    			+ "3 - Printing out all the recipients who have birthdays\n"
    			+ "4 - Printing out details of all the sent emails on given date\n"
    			+ "5 - Printing out the number of recipient objects in the application\n"
    			+ "4 - Printing out details of all the received emails on given date\n"
    			+ "7 - Exit from the email client");
    	try {
    	int option = scanner.nextInt();
    	
    	switch(option){
    	
    	
    		case 1:
    			//call method to add recipient
    			AddRecipient();
                break;
                
                
    		case 2:
    			//call method to send mail
    			NewMailSend();
    			break;
    			
    			
    		case 3:
    			//call method to check birthday wishes on a given date
    			PrintBirthdayRecipient();
    			break;
    			
    			
    		case 4:
    			//call method to print sent mails on given date
    			PrintSentEmails(SentEmails);
    			break;
    			
    		case 5:
    			// code to print the number of recipient objects in the application
    			System.out.println(CountOfrecipients +" number of recipients in your email client.\n");
    			break;	
    		case 6:
    			//call method to print receiving mails on given date
    			PrintSentEmails(ReceiveEmails);
    			break;
    		case 7:
    			//code for end the email client and serialize email objects
    			run=false;
    			SerializeEmails();
    			break;
    			
    		default :
    			System.out.println("Invalid sudgession\n");
    	}
    	
    	// start email client
    	// code to create objects for each recipient in clientList.txt
        // use necessary variables, methods and classes	
    	
    	}
    	catch (Exception e) {
    		System.out.println("Invalid input please enter correct recipient infomation.\n");
    	}
	}
	
	//stopping mail receiving thread and save receive mail thread
	receiveThread.Stop();
	saveReceiveMail.Stop();

}

//method to add recipient
public static void AddRecipient() {
	// input format - Official: nimal,nimal@gmail.com,ceo
    // Use a single input to get all the details of a recipient
    Scanner InputRecipient = new Scanner(System.in);
	System.out.println("Enter your Recipentin below formats :\n"
						+"If a official client use Official: <name>,<e-mail address>,<possition>\n"
						+"If a official friend client use Office_friend: <name>,<e-mail address>,<possition>,<date of birth on YYYY/MM/DD format>\n"
						+"If a personal recipient use Personal: <name>,<nick-name>,<e-mail address>,<date of birth on YYYY/MM/DD format>");             	
    String inputRecipient = InputRecipient.nextLine();
    //InputRecipient.close();
    // code to check can input store and add a new recipient
    if( CreateRecipient(inputRecipient)) {
    	FileWriter myWriter;                // store details in clientList.txt file 
    	try {
    		myWriter = new FileWriter("clientList.txt",true);
    		myWriter.write(inputRecipient+"\n");
    		myWriter.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	CountOfrecipients+=1;
    }
    

}
	
//method to create the relevant object using given details
public static boolean  CreateRecipient(String input) {
    	String[] details = input.split("[ ,]");
    	if (details[0].equals("Official:")) {
    		OfficialRes recipient = new OfficialRes(details[1], details[2],details[3]); 
    		
    		//update the client list
    		ClientList.add(recipient);
    		return true;
    	}
        
    	else if (details[0].equals("Office_friend:")) {
    		OfficialFriend recipient = new OfficialFriend(details[1], details[2],details[3],details[4]);
    		
    		//update the client list
    		ClientList.add(recipient);
    		return true;
    	}
    	
    	else if (details[0].equals("Personal:")) {
    		PersonalRes recipient = new  PersonalRes(details[1], details[3],details[2],details[4]);
    		
    		//update the client list
    		ClientList.add(recipient);
    		return true;
		}
    	
    	else {
    		System.out.println("Invalid input, reenter your input\n");
    		return false;
    	}
   }
//method to create and send new mail 
public static void NewMailSend() {
	// input format - email, subject, content
	Scanner InputEmail = new Scanner(System.in);
	System.out.println("Eneter your email in 'eamil,subject,text' format :");
    String inputEmal = InputEmail.nextLine();
    
    String[] details = inputEmal.split(",");
    Boolean isSent =false;
	// code to send an email
    for (Recipient rec : ClientList) {
    	if (rec.getEmail().equals(details[0])) {
    		Email newEmail= new Email(rec,details[1],details[2]);
    		newEmail.SendMail();
    		SentEmails.add(newEmail);
    		isSent =true;
    		break;
    	}
    }
    if (!isSent) {
	System.out.println("There is no such kind of recipient in your recipient list or check again the email.\n");
    }
}

//method to print recipients with birthdays
public static void PrintBirthdayRecipient() {
	
	// input format - yyyy/MM/dd (ex: 2018/09/17)
	Scanner Birthday = new Scanner(System.in);
	System.out.println("Enter the date you want to check for birthdays in YYYY/MM/DD format :");
    String date = Birthday.nextLine();

    // code to print recipients who have birthdays on the given date
    ArrayList<Recipient> birthdayList = GetBirthday(date);
    if (birthdayList.size()==0){
    	System.out.println("There is no birthdays on " +  date +".\n");
    }
    for(Recipient person : birthdayList) {
    	System.out.println(person.getName()+"-"+person.getEmail());
    }
    
}



//method to get birthdays for a given date
public static ArrayList<Recipient> GetBirthday(String date){
    	ArrayList<Recipient> birthdayList =new ArrayList<>();
    	for (Recipient rec : ClientList) {
    		if ((rec instanceof Wishable) ) {
    			if (((Wishable) rec).getDateOfBirth().substring(6).equals(date.substring(6))) {
    				birthdayList.add(rec);
    			}
    		}
    	}
    	
    	return birthdayList;
    }

//method for print sent mails on specified day
public static void PrintSentEmails(ArrayList<Email> mailList) {
	
	// input format - yyyy/MM/dd (ex: 2018/09/17)
	Scanner Requireday = new Scanner(System.in);
	System.out.println("Enter the day that you want to check for emails in YYYY/MM/DD format :");
    String requireday = Requireday.nextLine();

    // code to print the details of all the e-mails sent on the input date
    for (Email mail : mailList) {
		if(mail.getDate().equals(requireday)) {
			System.out.println(mail.getRecipient().getName()+" :- "+mail.getRecipient().getEmail()+" : "+mail.getSubject());
			
		}
    }
    
}

//method for serialize mails to Email.ser before close the email client
public static void SerializeEmails() {
	
	//if the application is going to stop the objects of e-mails are stored in the Email.ser file
	try {						
		FileOutputStream EmailOut = new FileOutputStream("Email.ser");
		ObjectOutputStream emailout = new ObjectOutputStream(EmailOut);
		emailout.writeObject(SentEmails);
		emailout.close();
		EmailOut.close();
		System.out.println("You are Welcome.");
		} 
	catch (IOException e) {
		e.printStackTrace();
	} 
	
}


//method to deserialize the sent and receive files 
@SuppressWarnings("unchecked")
public static void DeserialiseEmail() {    	
    try{										
    	File emailFile =new File("Email.ser");	//if there is no file named Email.ser we create a file
    	if(emailFile.createNewFile()) {
    		
    	}
    	if(emailFile.length()!= 0) {									//put all sent e-mails into a ArrayList
    		FileInputStream Emailfile = new FileInputStream("Email.ser");
   			ObjectInputStream emails = new ObjectInputStream(Emailfile);
   			SentEmails = (ArrayList<Email>) emails.readObject();
   			emails.close();
   			Emailfile.close();
   			}
    	File resEmailFile =new File("ReceiveEmails.ser"); //if there is no file named ReceiveEmails.ser we create a file
    		
    	if(resEmailFile.length()!= 0) {									//put all sent e-mails into a ArrayList
	    	FileInputStream ResEmailFile = new FileInputStream(resEmailFile); 
	        ObjectInputStream resEmails = new ObjectInputStream(ResEmailFile);
	        boolean state =true;
	        while (state) {					//put all received e-mails into a ArrayList
	        	try {
	        		Email mail = (Email) resEmails.readObject();
	        		ReceiveEmails.add(mail);
	        	}catch(EOFException e) {	// if mails are over then raise EOFExeption and then we stop reading receive mails from file
	        		state =false;
	        	}
			}
	        	resEmails.close();
				ResEmailFile.close();
	    }
    }
    
    	catch(IOException ioe){
         	ioe.printStackTrace();
         	return;
      	}
    	catch(ClassNotFoundException c){
         	c.printStackTrace();
         	return;
    	}
    }

//method for get recipients from the saved list
public static void GetListOfRecipient() {
    try {//if there is no a file we create new one
		File recipientlist = new File("clientList.txt");
		if(recipientlist.createNewFile()) {
		}
		if(recipientlist.length()!= 0) {							//read all the lines, if file does not empty
			Scanner curretrecipient = new Scanner(recipientlist);
			while (curretrecipient.hasNextLine()) {					//we put recipients in a Array list by a for loop
				String data = curretrecipient.nextLine();
				CreateRecipient(data);
				CountOfrecipients+=1;								//update static type variable that equals to number of recipients
	    		}
			}
	    }
    	catch (FileNotFoundException e) {
		      e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }

/*sending Birthday wishes as a email.
 * when a person opens email client for the first time of the proper day.
 * if someone open it more than one time at a day,birthday wishes will not be sent*/
public static void SendBirthdaywishes() {	
	Date today = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	ArrayList<Recipient> todaysBirthday = GetBirthday(dateFormat.format(today));	//getting a arrayList of recipients those who have their birthdays 
	      File date = new File("date.txt");
	      try {
			if (date.createNewFile()) {		//if there is no file in the folder we create a file to store the last day that the API opened
			    System.out.println("File created: " + date.getName());
				for (Recipient person : todaysBirthday) {	//and if there is a person with birthday on that day sent the wishes
					Email birthdaywish = new Email(person,"Birthday greeting",((Wishable) person).SendBirthdayWish());
					birthdaywish.SendMail();
					SentEmails.add(birthdaywish);
					}
			    FileWriter myWriter = new FileWriter("date.txt");//update the final day we entered
		        myWriter.write(dateFormat.format(today));
			    myWriter.close();
			  } 
			  else {//read the last entered day from the file
					Scanner dateLine = new Scanner(date);
			        String Day = dateLine.next();
			      dateLine.close();
			      if (!Day.equals(dateFormat.format(today))) {//if final entered day is not today check for birthdays
			    	  FileWriter myWriter = new FileWriter("date.txt");
			    	  for (Recipient person : todaysBirthday) {
			    		  Email birthdaywish = new Email(person,"Birthday greeting",((Wishable) person).SendBirthdayWish());
			    		  birthdaywish.SendMail();
			    		  SentEmails.add(birthdaywish);
			    	  }
		    		  myWriter.write(dateFormat.format(today));//update last entered day
			    	  myWriter.close();
			      }
			  }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}