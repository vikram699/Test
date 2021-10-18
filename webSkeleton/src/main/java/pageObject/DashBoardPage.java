package pageObject;

import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class DashBoardPage extends AppGeneric{

	public DashBoardPage(String PropPath, ExtentTest log) {
		super(PropPath, log);
	}

	public DashBoardPage goToSales(String navigateFrom) {
		if(navigateFrom.equalsIgnoreCase("DashBoard")) {
			scrollForClick("salesOption_name");
			log.log(Status.INFO, "Clicking on Sales Menu Option");
		} else if(navigateFrom.equalsIgnoreCase("BurgerMenu")) {
			clickElement("burgerMenu_xpath");
			log.log(Status.INFO, "Clicking on Burger Menu");
			clickElement("SalesBurgerMenu_xpath");
			log.log(Status.INFO, "Clicking on Sales Menu Option");
		} return this;
	}

	public DashBoardPage goToOppurtunites(String navigateFrom) {
		if(navigateFrom.equalsIgnoreCase("DashBoard")) 
			clickElement("opportunitiesSection_xpath");
		else if(navigateFrom.equalsIgnoreCase("BurgerMenu"))
			clickElement("opportunitiesMenu_xpath");
		log.log(Status.INFO, "Clicking on Opportunities Section");
		return this;
	}
	
	public DashBoardPage createOppurtunities() {
		clickElement("createOpportunitiesBtn_xpath");
		Assert.assertEquals(myFind("headerCreateOpp_xpath").getText(), "Create Opportunity");
		log.log(Status.INFO, "Creating a Oppurtunity");
		clickElement("saveAndContinueButton_xpath");
		
		return this;
	}
	
	public DashBoardPage goToJobs(String navigateFrom) {
		if(navigateFrom.equalsIgnoreCase("DashBoard")) {
			scrollForClick("jobs_id");
			log.log(Status.INFO, "Clicking on Job Menu Option");
		} else if(navigateFrom.equalsIgnoreCase("BurgerMenu")) {
			clickElement("jobs_xpath");
			log.log(Status.INFO, "Clicking on Job Menu Option");
		} return this;
	}
	
	public DashBoardPage goToTickets(String navigateFrom) {
		if(navigateFrom.equalsIgnoreCase("DashBoard")) {
			scrollForClick("tickets_id");
			log.log(Status.INFO, "Clicking on Job Menu Option");
		} else if(navigateFrom.equalsIgnoreCase("BurgerMenu")) {
			clickElement("jobs_xpath");
			log.log(Status.INFO, "Clicking on Job Menu Option");
		} return this;
	}
	
}
