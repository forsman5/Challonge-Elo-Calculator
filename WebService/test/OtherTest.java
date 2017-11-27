import org.junit.Test;

/*
 * Simple test class for methods in Alias.java
 */
public class OtherTest {
	@Test
	public void testRun() {
		SQLUtilities sql = new SQLUtilities();
		
		//preapring for new alias run
		sql.wipeTables();
		
		Alias.run();
	}
	
	@Test
	public void testSendMail() {
		Utility.sendEmail(Constants.ERROR_ALERT_DESTINATION, Constants.ERROR_ALERT_ORIGINATION, "subject test", "body test \n hi:)");
	}
}
