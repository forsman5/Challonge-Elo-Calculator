import static org.junit.Assert.*;

import org.junit.Test;

public class SQLUtilitiesTest {

	@Test
	public void testGetLastCheckedDate() {
		SQLUtilities util = new SQLUtilities();
		
		String temp = util.getLastCheckedDate();
		
		assertEquals(temp, "2000-01-03");
	}

	@Test
	public void testInsertTournament() {
		SQLUtilities util = new SQLUtilities();
		
		Tournament x = new Tournament();
		
		x.name = "test2";
		x.link = "challonge.com";
		x.dateStarted = "2000-01-03";
		x.id = 5053;
		
		util.insertTournament(x);
		
		assertTrue(true);
	}
	
}
