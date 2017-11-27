import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Utility {

   public static void sendEmail(String dest, String originator, String subject, String mess) {    
      // Recipient's email ID needs to be mentioned.
      String to = dest;

      // Sender's email ID needs to be mentioned
      String from = originator;

      // Assuming you are sending email from localhost
      String host = "smtp.gmail.com";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);
      properties.put("mail.smtp.port", "587");
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable","true"); 
      properties.put("mail.smtp.EnableSSL.enable","true");
      properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
      
      // Get the default Session object.
	  SmtpAuthenticator authentication = new SmtpAuthenticator(Constants.ERROR_ALERT_ORIGINATION, Constants.EMAIL_PASSWORD);
      Session session = Session.getInstance(properties, authentication);

      try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);
         
         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject(subject);

         // Now set the actual message
         message.setText(mess);

         // Send message
         Transport.send(message);
      } catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
   
	/*
	 * checks if a string is essentially null
	 */
	public static boolean isNull(String in) {
		boolean toReturn = false;
		
		if (in == null || in.isEmpty() || in.trim().isEmpty() || in.equals("null")) 
			toReturn = true;
		
		return toReturn;
	}
}