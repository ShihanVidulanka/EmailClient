package Email.Assignment;

import java.util.LinkedList;

public class Blocking_Queue {

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
