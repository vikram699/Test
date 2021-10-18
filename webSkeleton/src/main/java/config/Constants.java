package config;


public class Constants {


	/*** Util variables for WebDriver wait***/
	public static final int DEFAULT_DURATION = 20; // seconds
	public static final int DEFAULT_INTERVAL = 2000; // milliseconds
	
	public static final String BadgerObjectRepository = System.getProperty("user.dir")+"/src/test/resources/ObjectRepository/BadgerObjectRepository.properties";
	
	
	//EMAIL DETAILS
	public static final String SENDTO = "ajinkyamacho@gmail.com";
	public static final String SENDFROM = "ajinkyamobile93@gmail.com";
	public static final String EMAILPASSWORD = "Ajiswap@2817";
	

	//Ajinkya browser stack
	public static final String AUTOMATE_USERNAME = "ajinkya9";
	public static final String AUTOMATE_ACCESS_KEY = "zxTAQpf1Vt9Hneq9geEV";
	public static final String URL = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

	/*** Docker hub url ***********/
	public static final String HUB_URL = "http://localhost:4444";

	/*** Data Provider variables for browserStack**/
	public static final Long BSTACK_IDLE_TIMEOUT = 120L; // default is 90 seconds
	public static final Long BSTACK_IDLE_AUTOWAIT = 20L; // milli Second

	public static final Long TIMEOUT = 80L;
	public static final Long POOLTIME = 2000L;
	public static final boolean isGridEnabled = false;
	
	public static final boolean isBrowserStackEnabled = true; 
	public static final boolean isEmailReport = true;

	
	//Path
	public static final String SCREENSHOT= "target//Screenshot";
	public static final String DATAFILE = System.getProperty("user.dir")+"//src//test//resources//dataRepository//Badger.xlsx";

	//All the Excel Related Values
	public static final String TESTCASES_SHEET = "TestCases";

	public static final String TCID_COL = "TCID";
	public static final String RESULTS_COL = "Result";
	public static final String ACTUALRESULTS_COL = "Actual Result";

	public static final String RUNMODE_COL = "RunMode";
	public static final String DESCRIPTION_COL = "Description";

}
