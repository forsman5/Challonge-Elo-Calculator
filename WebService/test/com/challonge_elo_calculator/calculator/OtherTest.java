package com.challonge_elo_calculator.calculator;
import org.junit.Test;

import com.challonge_elo_calculator.calculator.Alias;
import com.challonge_elo_calculator.calculator.SQLUtilities;
import com.challonge_elo_calculator.calculator.Settings;
import com.challonge_elo_calculator.calculator.Utility;

/*
 * Simple test class for methods in Alias.java
 */
public class OtherTest {
	@Test
	public void testRun() {
		SQLUtilities sql = new SQLUtilities();
		
		//preapring for new alias run
		sql.wipeTables();

		Settings settings = new Settings();
		
		Alias.run(settings.getString("ALIAS_FILE"), settings.getString("ALIAS_OLD"), sql);
	}
	
	@Test
	public void testSendMail() {
		Settings settings = new Settings();
		
		Utility.sendEmail(settings.getString("ERROR_ALERT_DESTINATION"), settings.getStringArr("ERROR_ALERT_ORIGINATION"), "subject test", "body test \n hi:)");
	}
	
	@Test
	public void testGetStringArr() {
		Settings settings = new Settings();
		
		String[] result = settings.getStringArr("DISCARD_FLAGS");
		
		for (String s : result) {
			System.out.println(s);
		}
	}
}
