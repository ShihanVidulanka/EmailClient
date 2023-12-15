package Email.Assignment;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class EmailReceiver_Thread extends Thread {
	public static ArrayList<Email> Receiveemails;
	private Blocking_Queue queue;
	private static final String email_id = "*********@gmail.com";
	private static final String password = "*********";
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
				    	
				    	
				    	
			//	    	System.out.println( 
			//	    	"email " + email+
			//	          " sendDate: " + date
			//	          + " subject:" + subject + " content:" + content);
			//			message.setFlag(Flags.Flag.SEEN,true);
			//		
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
	        }else if (bodyPart.getContent() instanceof MimeMultipart){
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