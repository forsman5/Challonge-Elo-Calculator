import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/*
 * Test class to make running single methods easier.
 * 
 * TODO
 * split class - one test insert, one test gets.
 * Easier to rerun all the gets multiple times, with only one running of the inserts.
 * Add a drop test case at the beginning of all the inserts.
 * 		-add a drop method in sqlUtilities
 */
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
		
		assertEquals(1, returned);
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
		
		assertEquals(1, returned);
	}
	
	@Test
	public void testGetIDFromName() {
		SQLUtilities util = new SQLUtilities();
		
		String name = TEST_NAME;
		
		int returned = util.getPlayerID(name);
		
		assertEquals(1, returned);
	}
	
	@Test
	public void testWipeTables() {
		SQLUtilities sql = new SQLUtilities();
		
		sql.wipeTables();
		
		assertTrue(true);
	}
	
	@Test
	public void testInsertMatch() {
		SQLUtilities sql = new SQLUtilities();
		
		Match m = new Match();
		m.winner_id = 1;
		m.loser_id = 2;
		m.match_id = 1;
		m.winner_score = 5;
		m.loser_score = 3;
		m.tourney_id = 180727;
		
		sql.insertMatch(m);
		
		assertTrue(true);
	}
	
	@Test
	public void testGetNameFromID() {
		SQLUtilities util = new SQLUtilities();
		
		int id = 19;
		
		String returned = util.getPlayerName(id);
		
		assertEquals("Sahil K", returned);
	}
	
	@Test
	public void testLog() {
		Settings settings = new Settings();
		
		SQLUtilities sql = new SQLUtilities(settings);
		
		sql.startLog("Test", "nil", "nil");
		
		sql.stopLog("Test", "nil", "nil", "nil");
	}
	
	@Test
	public void testMethodCounts() {
		Settings settings = new Settings();
		
		SQLUtilities sql = new SQLUtilities(settings);
		
		Map<String, Integer> returned = sql.getDailyMethodCounts();
	}
}
