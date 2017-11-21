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
	
	@Test
	public void testInsertAlias() {
		SQLUtilities util = new SQLUtilities();
		
		String name = "xy";
		String alias = "yz";
		
		util.insertAlias(name, alias);
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertPlayerByName() {
		SQLUtilities util = new SQLUtilities();
		
		String name = "xy";
		
		util.insertPlayerByName(name);
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertPlayer() {
		SQLUtilities util = new SQLUtilities();
		
		Player x = new Player();
		
		x.player_id = 100;
		x.name = "joe";
		
		util.insertPlayer(x);
		
		assertTrue(true);
	}
	
	@Test
	public void testGetPlayerId() {
		SQLUtilities util = new SQLUtilities();
		
		String name = "xy";
		
		int returned = util.getPlayerId(name);
		
		assertEquals(1, returned);
	}
	
	@Test
	public void testGetPlayerIdFails() {
		SQLUtilities util = new SQLUtilities();
		
		String name = "yyz";
		
		int returned = util.getPlayerId(name);
		
		assertEquals(-1, returned);
	}
}
