package com.challonge_elo_calculator.calculator;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/*
 * Simple class used to attach an authentication to the request to send an email.
 * 
 * Ideally, should be called by using the constructor with both arguments.
 *
 * Using it in another way has not been tested.
 */
public class SmtpAuthenticator extends Authenticator {
	private String username;
	private String password;
	
	/*
	 * No args - not sure if this will work. Will probably set the password to be an empty string,
	 * which would fail the authentication.
	 */
	public SmtpAuthenticator() {	
	    super();
	    username = "";
	    password = "";
	}
	
	/*
	 * Recommended usage. Provide the username and password here.
	 */
	public SmtpAuthenticator(String user, String pass) {
		super();
		username = user;
		password = pass;
	}
	
	/*
	 * Provided in case needed.
	 * 
	 * Getters are not given for security reasons.
	 */
	public void setUsername(String user) {
		username = user;
	}

	/*
	 * Provided in case needed.
	 * 
	 * Getters are not given for security reasons.
	 */
	public void setPassword(String pass) {
		password = pass;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 * 
	 * This method is used in the call to super() (I believe) during the constructor, which is why it is best to use
	 * the both - args constructors.
	 * 
	 * Should something be set incorrectly, null is returned in leiu of a useful object.
	 */
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		if ((username != null) && (username.length() > 0) && (password != null) && (password.length   () > 0)) {
			return new PasswordAuthentication(username, password);
		}
		
	    return null;
	}
}