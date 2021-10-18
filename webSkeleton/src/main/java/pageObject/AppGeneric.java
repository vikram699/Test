package pageObject;

import java.io.IOException;
import java.util.Hashtable;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.SkipException;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import utility.Util;

public class AppGeneric extends Util{

	public AppGeneric(String PropPath, ExtentTest log) {
		super(PropPath, log);
	}

	/**
	 * Custom method to launch the Application underTest
	 * @param driverAgent : WebDriver instance
	 */
	public AppGeneric openApplication(String url){
		if(url.equalsIgnoreCase("Prod")) {
			navigateTo(prop.getProperty("badger_Prod_Url"));
			log.log(Status.INFO, "Navigating to PRODUCTION env "+prop.getProperty("badger_Prod_Url"));
		} else if(url.equalsIgnoreCase("POC")) {
			navigateTo(prop.getProperty("badger_POC_Url"));
			log.log(Status.INFO, "Navigating to POC env "+prop.getProperty("badger_POC_Url"));
		} else if(url.equalsIgnoreCase("FO_Staging")) {
			navigateTo(prop.getProperty("FO_Badger_Url"));
			log.log(Status.INFO, "Navigating to POC env "+prop.getProperty("FO_Badger_Url"));
		} else {
			log.log(Status.FAIL, "Unable to Open the application");
			new SkipException("Unable to Open the application");
		}
		return this;
	}

	/**
	 * Navigate to given appUrl and return Null pointer exception
	 * @param appUrl : String application under test URL
	 * @throws SkipException if URL passed is NULL
	 */
	public AppGeneric navigateTo(String appUrl){
		if(getDriver()!=null && appUrl !=null) {
			if(prop.getProperty(appUrl)!=null) {
				getDriver().get(prop.getProperty(appUrl).toString());
			}else {
				getDriver().get(appUrl);
				log.log(Status.INFO, "Navigating to URL "+appUrl);
			}
		}else
			new SkipException("Driver instance is null");
		return this;
	}

	/**
	 * Go to Home button of the application
	 * @return {@link AppGeneric} class object
	 */
	public AppGeneric goToHome() {
		clickElement("homeIcon_xpath");
		Assert.assertEquals(getDriver().getTitle(), "Oracle Applications");
		log.log(Status.PASS, "Clicking on Home Button and Navigating to Dashboard");
		return this;
	}

	/**
	 * Logs the given User in the application
	 * @param data : HashTable passed from the Data provider
	 * @return {@link AppGeneric} class object
	 */
	public AppGeneric getLogin(Hashtable<String, String> data) {
		if(getUrl().contains(prop.get("FO_Badger_Url").toString())) {
			sendKeys("userID_id", data.get("username"));
			log.log(Status.INFO, "Entering the UserName");
			sendKeys("password_id", data.get("password"));
			log.log(Status.INFO, "Entering the Password");
			clickElement("Sign_id");
			log.log(Status.INFO, "Click on SignIn button");
		}else {
			sendKeys("username_id", data.get("PocUsername"));
			log.log(Status.INFO, "Entering the UserName");
			sendKeys("password_id", data.get("PocPassword"));
			log.log(Status.INFO, "Entering the Password");
			clickElement("signInButton_id");
			log.log(Status.INFO, "Click on SignIn button");
		}
		return this;
	}

	public void postLogin(Hashtable<String, String> data) throws IOException {
		if(isElementPresent("//span[contains(text(),'Delete the oldest user session and login')]").size()>0) {
			log.info("Deleting the Previous Session", MediaEntityBuilder.createScreenCaptureFromPath(getScreenShot(getDriver(),"Previous Session")).build());
			clickableElement("//span[contains(text(),'Delete the oldest user session and login')]/../..");
			log.log(Status.INFO, "Click on SignIn button");
			getLogin(data);
			postLogin(data);
		}else {
			myFindVisibility("homeheader_xpath", 200).isDisplayed();
			Assert.assertEquals(myFind("homeheader_xpath").getText(), "Dispatch Console | Badger Inc.");
		}
	}

	public AppGeneric createNewWindow(int tab) {
		((JavascriptExecutor) getDriver()).executeScript("window.open()");
		log.info("Opened a new Tab");
		switchDriverControl(tab);
		return this;
	}

	public AppGeneric verifyLogin(String PostLogin) {
		try {
			Assert.assertEquals(
					PostLogin.equalsIgnoreCase("Pass"),
					myFindVisibility("homeIcon_xpath", 10).isDisplayed()
				);
			log.pass("Successfully Logged IN");
		} catch (Exception e) {
			log.fail("Unable to login with the given login details");
			Assert.fail("Unable to login with the given login details");
		}
		return this;
	}

}
