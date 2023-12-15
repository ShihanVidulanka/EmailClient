package pack;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Properties;
import java.util.Date;
import java.util.LinkedList;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;


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
    	File emailFile =new File("Email.ser");	
    	
    	//if there is no file named Email.ser we create a file
    	if(emailFile.createNewFile()) {
    		
    	}
    	
    	//put all sent e-mails into a ArrayList
    	if(emailFile.length()!= 0) {									
    		FileInputStream Emailfile = new FileInputStream("Email.ser");
   			ObjectInputStream emails = new ObjectInputStream(Emailfile);
   			SentEmails = (ArrayList<Email>) emails.readObject();
   			emails.close();
   			Emailfile.close();
   			}
    	
    	File resEmailFile =new File("ReceiveEmails.ser");
    	
    	//put all sent e-mails into a ArrayList
    	if(resEmailFile.length()!= 0) {									
	    	FileInputStream ResEmailFile = new FileInputStream(resEmailFile); 
	        ObjectInputStream resEmails = new ObjectInputStream(ResEmailFile);
	        boolean state =true;
	        
	      //put all received e-mails into a ArrayList
	        while (state) {					
	        	try {
	        		Email mail = (Email) resEmails.readObject();
	        		ReceiveEmails.add(mail);
	        	}catch(EOFException e) {	
	        		// if mails are over then raise EOFExeption and then we stop reading receive mails from file
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
    try {
    	//if there is no a file we create new one
		File recipientlist = new File("clientList.txt");
		if(recipientlist.createNewFile()) {
		}
		
		//read all the lines, if file does not empty
		if(recipientlist.length()!= 0) {							
			Scanner curretrecipient = new Scanner(recipientlist);
			
			//we put recipients in a Array list by a for loop
			while (curretrecipient.hasNextLine()) {					
				String data = curretrecipient.nextLine();
				CreateRecipient(data);
				
				//update static type variable that equals to number of recipients
				CountOfrecipients+=1;								
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
 * if someone open it more than one time at a day,birthday wishes will not be sent
 */
public static void SendBirthdaywishes() {	
	Date today = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	ArrayList<Recipient> todaysBirthday = GetBirthday(dateFormat.format(today));	//getting a arrayList of recipients those who have their birthdays 
	File date = new File("date.txt");
		try {
			//if there is no file in the folder we create a file to store the last day that the API opened
			if (date.createNewFile()) {		
			    System.out.println("File created: " + date.getName());
			    
			    //and if there is a person with birthday on that day sent the wishes
				for (Recipient person : todaysBirthday) {	
					Email birthdaywish = new Email(person,"Birthday greeting",((Wishable) person).SendBirthdayWish());
					birthdaywish.SendMail();
					SentEmails.add(birthdaywish);
					}
				
				//update the final day we entered
			    FileWriter myWriter = new FileWriter("date.txt");
		        myWriter.write(dateFormat.format(today));
			    myWriter.close();
			  } 
				
			  else {		//read the last entered day from the file
					Scanner dateLine = new Scanner(date);
			        String Day = dateLine.next();
			      dateLine.close();
			      
			      //if final entered day is not today check for birthdays
			      if (!Day.equals(dateFormat.format(today))) {
			    	  FileWriter myWriter = new FileWriter("date.txt");
			    	  
			    	  for (Recipient person : todaysBirthday) {
			    		  Email birthdaywish = new Email(person,"Birthday greeting",((Wishable) person).SendBirthdayWish());
			    		  birthdaywish.SendMail();
			    		  SentEmails.add(birthdaywish);
			    	  }
			    	  
			    	  //update last entered day
		    		  myWriter.write(dateFormat.format(today));
			    	  myWriter.close();
			      }
			  }
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}


//class of e-mails have a own method to send the mail  
class Email implements Serializable{

		public  final String username = "hdvidulanka@gmail.com";
		public  final  String password = "hd1999sv";
		private Recipient recipient;
		private String subject;
		private String text;
		private String Date;
		
        public Email(Recipient recipient,String subject,String text) {
			this.recipient = recipient;
			this.subject=subject;
			this.text =text;
		}
		public String getSubject() {
			return subject;
		}
		public String getText() {
			return text;
		}
		public void setDate(String date) {
			Date = date;
		}
		public String getDate() {
			return Date;
		}
		public Recipient getRecipient() {
			return recipient;
		}
		
		public  void SendMail() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			this.Date = dateFormat.format(date);
			
        	Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); 
            
            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
            	//creating a massage 
                Message message = new MimeMessage(session);
            	//Define who send the email
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
            			//set the receiver of the massage
                        Message.RecipientType.TO,
                        InternetAddress.parse(recipient.getEmail())
                );
                message.setSubject(subject);//add massage subject
                message.setText(text);//add massage body

            	//send the massage 
                Transport.send(message);

               //print massage sent and display the sender and subject
               System.out.println("Email sent to "+recipient.getEmail()+" on -"+subject+"\n");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }

//create a super class to implement recipients
class Recipient implements Serializable {
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


//create a sub class toOfficial recipients
class OfficialRes extends Recipient {
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


/*create a subclass to implement Official friends from Official recipient 
*and implement a wishable interface to implement the ability of birthday wish 
 */
class OfficialFriend extends OfficialRes implements Wishable{
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


/*create a subclass to implement personal recipients from the Recipient superclass 
*and implement a wishable interface to implement the ability of birthday wish 
 */
class PersonalRes extends Recipient implements Wishable {
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

//make a interface to implement the ability of wishing 
interface Wishable {

	public String SendBirthdayWish( );
	public String getDateOfBirth();
}


/*
 * create a email receiver to receive e-mails.
 * receiving e-mails take lot of time and it slow down the main program.
 * because of that it create in a separate thread.
 */

class EmailReceiver_Thread extends Thread {
	public static ArrayList<Email> Receiveemails;
	private Blocking_Queue queue;
	private static final String email_id = "hdvidulanka@gmail.com";
	private static final String password = "hd1999sv";
	private ObserverObj[] observers;
	private boolean run;
	
	
  public EmailReceiver_Thread(Blocking_Queue queue, ObserverObj[] observers) {
		this.queue = queue;
		this.observers = observers;
		this.run=true;
	}


  public void run() {
	  /*
	   * run the thread until run is not true 
	   * when the email client close run will false and then stop the receiving e-mails
	   */
	  while(run) {			

        try {
        	//code for receiving mails
			Session session = Session.getDefaultInstance(new Properties( ));
			Store store = session.getStore("imaps");
			store.connect("imap.googlemail.com", 993, email_id, password);
		    Folder inbox = store.getFolder( "INBOX" );
		    inbox.open( Folder.READ_WRITE );
			
		    // Fetch unseen messages from inbox folder
		    Message[] messages = inbox.search(
		    		new FlagTerm(new Flags(Flags.Flag.SEEN), false));
		    
		    //if count of unseen messages is not empty mail receiving happen
			if (inbox.getUnreadMessageCount() > 0) {

			    // Sort messages from recent to oldest
			    Arrays.sort( messages, new Comparator<Message>() {
					@Override
					public int compare(Message m1, Message m2) {
						try {
							return m2.getSentDate().compareTo( m1.getSentDate() );
						} catch ( MessagingException e ) {
							throw new RuntimeException( e );
						}
					}
			    } );
				    
				//code read all receive mails 
			    for ( Message message : messages ) {
			    	
			    	//get sender name and email address for create a recipient object
			    	String[] from = message.getFrom()[0].toString().split("[<>]");
			    	String name = from[0];
			    	String email ;
			    	
			    	//when the receiver doesnot have a name on the mail then we set the name as email 
			    	if(from.length>1)
			    		email = from[1];
			    	else {
			    		email = from[0];
			    	}
		
				   	//get other details from message; subject and content
			    	String subject = message.getSubject().toString();
				   	String content;
				   	//call getTextFromMessage() to get content 
					content = getTextFromMessage(message);
					
					//get today date to set mail objects date field  
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			    	String date =  dateFormat.format(new Date());
			    	
			    	//create recipient and email objects
			    	Recipient recipient = new Recipient(name, email);
			    	Email new_mail = new Email(recipient,subject,content);
			    	
			    	//set date
				    new_mail.setDate(date);
				    
				    //get current date for pass to update method of observers
				    LocalTime currentTime = LocalTime.now();
    
				    //enqueue the massage to the blocking queue
				   	queue.enqueue(new_mail);
				   	
				   	//after enqueue the mail set the mail as read 
				    message.setFlag(Flags.Flag.SEEN,true);
				    
				    //notify to observers to send the notification and save the notification
				   	observers[0].update(currentTime);
				   	observers[1].update(currentTime);
					    	
				   	//finally add the received email to the static receive e-mail list in the email client object
				   	Email_Client.ReceiveEmails.add(new_mail);
				    	
			    }
			}
			
			/*sleep the thread 10s
			 * then after receiving e-mails email receiver thread waits 10s to check for unread mails from the server 
			 */
			sleep(10000);

		    
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	//send a null object to notify to the save mail thread that the email receiving is ended
	queue.enqueue(null);
  }

  //method for get content from message
  private String getTextFromMessage(Message message) throws MessagingException, IOException {
	  
	    String text = "";
	    //if massage is text type get the massage directly
	    if (message.isMimeType("text/plain")) {
	        text = message.getContent().toString();
	    } 
	    
	    //else if it is a Multypart type call for getTextFromMimeMultipart() to get text of it
	    else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mime = (MimeMultipart) message.getContent();
	        text = getTextFromMimeMultipart(mime);
	    }
	    return text;
	}
  
  //method for get text from mime type massages
  private String getTextFromMimeMultipart(
	        MimeMultipart mime)  throws MessagingException, IOException{
	  
	    String text = "";
	    int count = mime.getCount();
	    
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mime.getBodyPart(i);
	        
	        if (bodyPart.isMimeType("text/plain")) {
	            text = text + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        }
	        else if (bodyPart.getContent() instanceof MimeMultipart){
	            text = text + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return text;
}


//method for stopping receiving email thread
  public void Stop() {
	  
	  	/*if call stop the run field of this thread become false
	  	 * stop the receiving e-mails 
	  	 * close the thread
	  	 */
		run = false;
  }

}


/*
 * saving e-mails is also a highly cost program.
 * it reduce the speed of the.
 * to prevent that saving e-mails put into a separate thread.
 */
class SaveReceiveEmail_Thread extends Thread{
	private Blocking_Queue queue;
	private boolean run;

	public SaveReceiveEmail_Thread(Blocking_Queue queue) {
		this.queue = queue;
		this.run=true;
	}
	
	public void run(){
		
		//run the save mail thread if run is true and queue is not empty
		while(run || !queue.isEmpty()) {
			try {
				
				//dequeue blocking queue and get mail
				Email email = queue.dequeue();
				
				//if email is null because the email receiver thread is closed then exiting from the saving email process 
				if(email==null) {
					break;
				}
				
				//dequeue e-mails save in the ReceiveEmails.ser file 
				File emailFile =new File("ReceiveEmails.ser");
				FileOutputStream ReceiveEmaiFile;
				
			        boolean append = emailFile.exists(); // if file exists then append, otherwise create new

			        	//make a Appedable object and append the emails to the file
			            FileOutputStream ReceiveEmailFile = new FileOutputStream(emailFile, append);
			            AppendableObjectOutputStream mails = new AppendableObjectOutputStream(ReceiveEmailFile, append);
			        mails.writeObject(email);
			        
			        //System.out.println("Received email is saved");
			       
			}
						
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			
			}
        
	}

	//method to this thread close
	public void Stop() {
		//if this method calls run field become false and then after all mails saved thread will close
		this.run = false;
	}
	
	
}


//Class to create a own blocking queue
class Blocking_Queue {

	private LinkedList<Email> queue;
	private int  max_size;
	
	//Creating a blocking queue with given maximum size of elements 
	public Blocking_Queue(int max_size){
		this.max_size = max_size;
		this.queue = new LinkedList<Email>();
	}

	//Method for enqueue mails
	public synchronized void enqueue(Email email){
		
		//if the size of the blocking queue is maximum wait until not full
		while(this.queue.size() == this.max_size) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//if blocking queue is not full add the email to the queue
		this.queue.add(email);
		notifyAll();	//Notify to all synchronized methods
	}


	public synchronized Email dequeue() {
		
		//if the size of the blocking queue is minimum(zero) wait until not empty
		while(this.queue.size() == 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//if blocking queue is not empty get the email from the queue
		Email mail = this.queue.remove();
		notifyAll();	//Notify to all synchronized methods
		
		//return the mail
		return mail;
	}
	
	//method for check queue is empty or not
	public boolean isEmpty() {
		return queue.size()==0;
	}
	
	//method for check queue is full or not
	public boolean isFull() {
		return queue.size()==max_size;
	}
}


/*
 * if we use normal append mode for save objects to the serialize files we get some error when the file deserialize.
 * the error is "java.io.StreamCorruptedException: invalid stream header: 79737200"
 * because when adding objects add a header for each object 
 * in here it solve by creating a new class extends ObjectOutputStream 
 * when call the AppendableObjectOutputStream() stream header will reset 
 * then the  is solved 
 */
class AppendableObjectOutputStream extends ObjectOutputStream {

    private boolean append;
    private boolean initialized;
    private DataOutputStream dout;

    protected AppendableObjectOutputStream(boolean append) throws IOException, SecurityException {
        super();
        this.append = append;
        this.initialized = true;
    }

    public AppendableObjectOutputStream(OutputStream out, boolean append) throws IOException {
        super(out);
        this.append = append;
        this.initialized = true;
        this.dout = new DataOutputStream(out);
        this.writeStreamHeader();
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        if (!this.initialized || this.append) return;
        if (dout != null) {
            dout.writeShort(STREAM_MAGIC);
            dout.writeShort(STREAM_VERSION);
        }
    }

}


//create a interface ObserverObj to inherit observer classes
interface ObserverObj {
	public void update(LocalTime currentTime);
}


//class to notify to the console email received 
class EmailStatRecorder implements ObserverObj {
	
	@Override
	public void update(LocalTime currentTime) {		//method of printing the notification informing email has received 

		System.out.println("Notification : an email is received at " +  currentTime.toString().substring(0, 8)  +".");
		
	}
	
}


//class to save the notifications those are printed to the console.
class EmailStatPrinter implements ObserverObj {

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