import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/*
 * TODO
 * Document this class
 */
public class SmtpAuthenticator extends Authenticator {
	private String username;
	private String password;
	
	public SmtpAuthenticator() {	
	    super();
	    username = "";
	    password = "";
	}
	
	public SmtpAuthenticator(String user, String pass) {
		super();
		username = user;
		password = pass;
	}
	
	public void setUsername(String user) {
		username = user;
	}
	
	public void setPassword(String pass) {
		password = pass;
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		if ((username != null) && (username.length() > 0) && (password != null) && (password.length   () > 0)) {
			return new PasswordAuthentication(username, password);
		}
		
	    return null;
	}
}