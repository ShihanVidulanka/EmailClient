package Email.Assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveReceiveEmail_Thread extends Thread{
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

	//methode to this thread close
	public void Stop() {
		//if this methode calls run field become false and then after all mails saved thread will close
		this.run = false;
	}
	
	
}
