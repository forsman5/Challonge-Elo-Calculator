import org.junit.Test;

/*
 * Simple test class for methods in Alias.java
 */
public class AliasTest {
	@Test
	public void testRun() {
		SQLUtilities sql = new SQLUtilities();
		
		//preapring for new alias run
		sql.wipeTables();
		
		Alias.run();
	}
}
