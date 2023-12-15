package Email.Assignment;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Date;

public class Email implements Serializable{

		public  final String username = "**********@gmail.com";
		public  final  String password = "**********";
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
            properties.put("mail.smtp.starttls.enable", "true"); //TLS
            
            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(recipient.getEmail())
                );
                message.setSubject(subject);
                message.setText(text);

                Transport.send(message);

                System.out.println("Email sent to "+recipient.getEmail()+" on -"+subject+"\n");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }