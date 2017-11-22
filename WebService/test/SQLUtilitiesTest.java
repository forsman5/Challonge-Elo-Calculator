import static org.junit.Assert.*;

import org.junit.Test;

public class SQLUtilitiesTest {

	//for ensuring all tests match
	private final String TEST_NAME = "joe";
	private final String TEST_ALIAS = "ractor";
	
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
		x.id = 5054;
		
		util.insertTournament(x);
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertAlias() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_NAME;
		String alias = TEST_ALIAS;
		
		util.insertAlias(name, alias);
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertPlayerByName() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_NAME;
		
		util.insertPlayerByName(name);
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertPlayer() {
		SQLUtilities util = new SQLUtilities();
		
		Player x = new Player();
		
		x.player_id = 100;
		x.name = "joe 3";
		
		util.insertPlayer(x);
		
		assertTrue(true);
	}
	
	@Test
	public void testGetPlayerId() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_NAME;
		
		int returned = util.getPlayerID(name);
		
		assertEquals(101, returned);
	}
	
	@Test
	public void testGetPlayerIdFails() {
		SQLUtilities util = new SQLUtilities();
		
		String name = "yyz";
		
		int returned = util.getPlayerID(name);
		
		assertEquals(-1, returned);
	}
	
	@Test
	public void testGetNameFromAlias() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_ALIAS;
		
		String returned = util.getNameFromAlias(name);
		
		assertEquals(TEST_NAME, returned);
	}
	
	@Test
	public void testGetIdFromAlias() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_ALIAS;
		
		int returned = util.getIDFromAlias(name);
		
		assertEquals(101, returned);
	}
	
	@Test
	public void testGetIDFromName() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_NAME;
		
		int returned = util.getPlayerID(name);
		
		assertEquals(101, returned);
	}
}
