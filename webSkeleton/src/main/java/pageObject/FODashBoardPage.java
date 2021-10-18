package pageObject;

import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class FODashBoardPage extends DashBoardPage{

	public FODashBoardPage(String PropPath, ExtentTest log) {
		super(PropPath, log);
	}

	public FODashBoardPage searchRegion(String Region) {
		sendKeys("searchByNameId_xpath", Region);
		log.log(Status.INFO, "Searching the region Name");
		return this;
	}

	public FODashBoardPage selectRegion(String Region, int num) {
		if(num>=1)
			getLocatorsVisibility(prop.getProperty("spantext_xpath")+Region+prop.getProperty("endingParent_xpath"), 20).get(num).click();
		else
			clickableElement(prop.getProperty("spantext_xpath")+Region+prop.getProperty("endingParent_xpath"));
		log.log(Status.INFO, "Clicking on searched Region "+Region);
		return this;
	}

	public FODashBoardPage clearSearchRegion() {
		clickElement("clearSearchText_name");
		log.log(Status.INFO, "Clicked on Clear Search Region");
		return this;
	}

	public FODashBoardPage expandRegion(String Region) {
		clickVisibilityElement(prop.getProperty("spantext_xpath")+Region+prop.getProperty("expandButton_xpath"));
		try {
			waitUntilInvisible(prop.getProperty("spantext_xpath")+Region+prop.getProperty("expandButton_xpath"));
		} catch (Exception e) {
			expandRegion(Region);
		}
		log.log(Status.INFO, "Expanding the "+Region);
		return this;
	}

	public FODashBoardPage collapseRegion(String Region) {
		clickElement(prop.getProperty("spantext_xpath")+Region+prop.getProperty("collapseButton_xpath"));
		log.log(Status.INFO, "Collapsing the "+Region);
		return this;
	}

	public FODashBoardPage sliptScreen() {
		if(isElementPresent("SplitScreenSection_xpath").size()>0) {
			clickVisibilityElement("SplitScreenSection_xpath");
		}else {
			clickableElement("//button[contains(@title,'Options')]");
			clickableElement("//button[contains(@title,'Split Screen')]");
		}
		log.log(Status.INFO, "Clicked on Split Screen");
		return this;
	}

	public FODashBoardPage assignTruckToOperator(String Truck, String Operator) throws InterruptedException {
		List<WebElement> operator = getLocators("operatorExpand_xpath");
		clickableElement(operator.get(0));
		waitUntilInvisible(operator.get(0));
		List<WebElement> truck = getLocators("truckExpand_xpath");
		clickableElement(truck.get(1));
		waitUntilInvisible(truck.get(1));
		log.log(Status.INFO, "Expanded the operator and Truck drop down");

		clickableElement("//span[contains(text(),'Trucks')]/..//parent::div//following-sibling::div//child::span[contains(text(),'"+Truck+"')]/..");
		clickableElement("//span[contains(text(),'Operator')]/..//parent::div//following-sibling::div//child::span[contains(text(),'"+Operator+"')]/..");
		log.log(Status.INFO, "Clicked on Operator and Trucks");
		Thread.sleep(2000);
		dragDrop(
				myFindVisibility("//span[contains(text(),'Trucks')]/..//parent::div//following-sibling::div//child::span[contains(text(),'"+Truck+"')]/.."), 
				myFindVisibility("//span[contains(text(),'Operator')]/..//parent::div//following-sibling::div//child::span[contains(text(),'"+Operator+"')]/.."));
		log.log(Status.INFO, "Dragged Truck to Operator");
		return this;
	}

	public FODashBoardPage updateActivity() {
		clickableElement("//label[contains(text(),'Job 1')]/../input");
		log.log(Status.INFO, "Selected the first Job");
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "Submitted the Activity");
		return this;
	}

	public FODashBoardPage verifyAssigned(String Truck, String Operator) throws InterruptedException {
		Thread.sleep(5000);
		String text = getText(prop.getProperty("spantext_xpath")+Operator+prop.getProperty("endingParent_xpath"));
		verifyText(prop.getProperty("spantext_xpath")+Operator+prop.getProperty("endingParent_xpath"), text);
		System.out.println(text.contains(Truck));
		Assert.assertEquals(text.contains(Truck), true);
		System.out.println(text.contains(Operator));
		Assert.assertEquals(text.contains(Operator), true);
		log.log(Status.INFO, "Verified the Drag is successfull");
		return this;
	}

	public FODashBoardPage clickListView() {
		getLocatorsVisibility("listView_xpath", 20).get(1).click();
		log.log(Status.INFO, "Clicked on List View");
		return this;
	}

	public FODashBoardPage selectCalendar() {
		try {
			getLocatorsVisibility("dateButton_xpath", 20).get(1).click();
			String[] ary = getDate().split("-");
			System.out.println(ary[2]);
			for(WebElement el : getLocators("//a[starts-with(text(),'"+ary[2]+"')]")) {
				if(el.isDisplayed()) {
					el.click();
				}
			}
			log.log(Status.INFO, "Clicked on Date Button");
			return this;
		} catch (Exception e) {
			return this;
		}
	}

	public FODashBoardPage nonScheduled() {
		clickableElement("noscheduleButton_xpath");
		log.log(Status.INFO, "Clicked on Non Scheduled");
		return this;
	}

	public String dragFirstJob(Hashtable<String, String> data) throws InterruptedException {
		try {
			Thread.sleep(2000);
			actionHoverClick("ticketTd_xpath");
			Thread.sleep(2000);
			actionHoverClick("ticketTd_xpath");
			Thread.sleep(2000);
			System.out.println(data.get("TicketNumber"));
			String ActivityName = getText("//td[contains(text(),'"+data.get("TicketNumber")+"')]//..//td//child::div[contains(@role,'button')]");
			WebElement unSchedule = myFindVisibility("//td[contains(text(),'"+data.get("TicketNumber")+"')]//..//td//child::div[contains(@role,'button')]");
			WebElement operator = myFindVisibility("//div[contains(text(),'"+data.get("Operator")+"')]");
			dragDrop(unSchedule, operator);
			log.log(Status.INFO, "Dragging the "+ActivityName+" job to Operator");
			return ActivityName;
		} catch (Exception e) {
			log.log(Status.FAIL, "No TICKET Found by "+data.get("TicketNumber")+" Name");
			Assert.fail("No TICKET Found by "+data.get("TicketNumber")+" Name");
			return null;
		}
	}

	public Hashtable<String, String> verifyAssignedJob(Hashtable<String, String> data, String ActivityName) {
		Assert.assertEquals(getText("providerName_xpath").trim(), data.get("Operator"));
		System.out.println(ActivityName);
		System.out.println(getText("formheader_xpath").trim());
		data.put("FormHeader", getText("formheader_xpath").trim());
		clickableElement("formSubmit_xpath");
		data.put("ActivityName", ActivityName);
		List<WebElement> el = getLocatorsVisibility("//div[contains(text(),'Close Split Screen')]", 5);
		clickableElement(el.get(el.size()-1));
		log.log(Status.INFO, "Receivied the Confirmation Pop Up");
		return data;
	}

	public Boolean verifyJobAndChangeTime(Hashtable<String, String> data, String timeSlot){
		try {
			System.out.println("Activity Name : "+data.get("ActivityName"));
			scrollForCenterElementlocator("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]");
			WebElement el = myFindVisibility("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]");
			if(el.getText().startsWith("❌")) {
				scrollForCenterElementlocator("//div[contains(@aria-label,'"+data.get("ActivityName")+"')]");
				actionHoverClick("//div[contains(@aria-label,'"+data.get("ActivityName")+"')]//div");
				log.log(Status.INFO, "Closing the split screen and Changing the time slot");
				clickableElement("//span[contains(text(),'Details') and contains(@role,'link')]");

				if(!myFind("selectIsDispatched_xpath").isSelected())
					clickableElement("selectIsDispatched_xpath");
				if(!myFind("selectShowPrice_xpath").isSelected())
					clickableElement("selectShowPrice_xpath");

				clickableElement("//select[contains(@class,'form-item')]");
				Select drpCountry = new Select(myFind("//select[contains(@class,'form-item')]"));
				drpCountry.selectByVisibleText(timeSlot);
				clickableElement("submitButn_xpath");
				log.log(Status.INFO, "Selected the Job and Changed the time Slot");
				scrollForCenterElementlocator("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]");
				actionHoverClick("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]//div");
				clickableElement("//span[contains(text(),'History') and contains(@role,'link')]");
				Thread.sleep(5000);
				getDriver().navigate().refresh();
				Thread.sleep(2000);
				try {
					myFindVisibility("//li[contains(text(),'success:')]", 120);
				}catch (Exception e) {
					getDriver().navigate().refresh();
					myFindVisibility("//li[contains(text(),'success:')]", 120);
				}
				Assert.assertEquals(myFind("//li[contains(text(),'success:')]").isDisplayed(), true);
				clickableElement("//div[contains(@id,'back-button')]");
				return true;
			}else {
				el = myFindVisibility("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]");
				System.out.println(el.getText());
				scrollForCenterElementlocator(el);
				actionHoverClick("//div[contains(@aria-label,'"+data.get("ActivityName").substring(1, data.get("ActivityName").length())+"')]//div");
				clickableElement("//span[contains(text(),'History') and contains(@role,'link')]");
				Assert.assertEquals(myFind("//li[contains(text(),'success:')]").isDisplayed(), true);
				clickableElement("//div[contains(@id,'back-button')]");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public FODashBoardPage setUpRoute(Hashtable<String, String> data) throws Exception {
		clickableElement("selectOperatorHeader_xpath");
		clickableElement("operatorHeaderRoute_xpath");
		myFindVisibility("//span[contains(text(),'My Route')]");
		try {
			if(isElementPresent("AdjustBtn_xpath").size()>0) {
				clickableElement("AdjustBtn_xpath");
				Select drpCountry = new Select(myFind("//select[contains(@class,'form-item')]"));
				drpCountry.selectByVisibleText(" 5 minutes");
				clickableElement("submitButn_xpath");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(isElementPresent("ActiviateRoute_xpath").size()>0) {
				clickableElement("ActiviateRoute_xpath");
				clickableElement("submitButn_xpath");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		clickableElement("activiateInfoRoute_xpath");
		try {
			if(isElementPresent("ticketNumberVerify_xpath").size()==0)
				Thread.sleep(60000);
			getDriver().navigate().refresh();
			myFindVisibility("ticketNumberVerify_xpath", 120);

		}catch (Exception e) {
			List<WebElement> el = getLocators("//div[contains(@class,'back-button__icon')]");
			el.get(el.size()-1).click();
			el = getLocators("//div[contains(@class,'back-button__icon')]");
			el.get(el.size()-1).click();
			if(verifyJobAndChangeTime(data, "11:00AM")) {
				verifyJobAndChangeTime(data, "2:00PM");
				verifyJobAndChangeTime(data, "4:00PM");
			}
			setUpRoute(data);
		}
		Assert.assertEquals(getText("ticketNumberVerify_xpath"), data.get("TicketNumber"));
		log.log(Status.INFO, "Clicked on Operator and the Route header");
		return this;
	}

	public FODashBoardPage acknowledgements() {
		clickableElement("acknowledgement_xpath");
		Assert.assertEquals(myFind("acknowlegeText_xpath").getText(), "I acknowledge that I have read and understood following prior to leaving for job:");
		for(WebElement el : getLocators("checkBoxInput_xpath")) {
			el.click();
		}
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "Entered the acknowledgement");
		return this;
	}

	public FODashBoardPage Notes() {
		clickableElement("Notes_xpath");
		for(WebElement el : getLocators("textArea_xpath")) {
			if(el.isDisplayed())
				el.sendKeys("Test");
		}
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "Entered the Notes");
		return this;
	}

	public FODashBoardPage fieldLevelRisk(Hashtable<String, String> data) {
		clickableElement("FLRA_xpath");
		clickableElement("manualCheckbox_xpath");
		clickableElement("availableCheckbox_xpath");
		clickableElement("acknowledgeCheckbox_xpath");
		scrollForCenterElementlocator("canvas_xpath");
		for (WebElement element : getLocators("canvas_xpath")) {
			Actions builder = new Actions(getDriver());
			builder.moveToElement(element).perform();
			builder.clickAndHold(element).perform();
			builder.moveByOffset(20, 50).perform();
			builder.moveToElement(element).perform();
			builder.clickAndHold(element).perform();
			builder.moveByOffset(100, 50).perform();
			builder.moveToElement(element).perform();
			builder.moveByOffset(10, 50).perform();
			builder.moveToElement(element).perform();
			builder.clickAndHold(element).perform();
			builder.moveByOffset(200, 50).perform();
			builder.moveToElement(element).perform();
		}
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "Entered the FLRA deatils");
		return this;
	}

	public FODashBoardPage startTime() {
		clickableElement("start_xpath");
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "Starting the ticket");
		return this;
	}

	public FODashBoardPage ticket() {
		try {
			clickableElement("ticketDiv_xpath");
			getDriver().switchTo().frame(myFindVisibility("//iframe[contains(@title,'ticket')]"));
			for(WebElement el : getLocators("ticketQuantityText_xpath")) {
				el.sendKeys("1");
			}
			clickableElement("SaveBtn_xpath");
			getDriver().switchTo().defaultContent();
			log.log(Status.INFO, "Enter the ticket details and amount");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public FODashBoardPage signAndApprove() {
		clickableElement("signAndApprove_xpath");
		getDriver().switchTo().frame(myFindVisibility("//iframe[contains(@title,'approve')]"));
		clickableElement("SaveBtn_xpath");
		getDriver().switchTo().defaultContent();
		log.log(Status.INFO, "Sign and Approve the Ticket");
		return this;
	}

	public FODashBoardPage endTicket() {
		clickableElement("endTicket_xpath");
		clickableElement("submitButn_xpath");
		log.log(Status.INFO, "End the ticket");
		return this;
	}

	public void verifyCompleted(Hashtable<String, String> data) throws Exception {
		switchDriverControl(0);
		goToHome();
		goToSales("DashBoard");
		goToTickets("Dashboard");
		verifyTicketStatus(data.get("TicketNumber"));

	}

	public void verifyTicketStatus(String TicketNumber) throws Exception {
		sendKeys("ticketNumber_xpath", TicketNumber);
		Thread.sleep(2000);
		clickableElement("findButton_xpath");

		myFindVisibility("//a[contains(text(),'"+TicketNumber+"')]", 60).click();
		myFindVisibility("//h1[contains(text(),'"+TicketNumber+"')]", 60).isDisplayed();
		scrollForCenterElementlocator("statusFlag_xpath");
		myFindVisibility("statusFlag_xpath", 60);
		try {
			myFindVisibility("//td[contains(@title,'Status')]//following-sibling::td//span[contains(text(),'Completed')]", 30);
			System.out.println(myFindVisibility("statusFlag_xpath").getText());
			Assert.assertEquals(myFindVisibility("//td[contains(@title,'Status')]//following-sibling::td").getText(), "Completed W/O Sign");
		}catch (Exception e) {
			getDriver().navigate().refresh();
			verifyTicketStatus(TicketNumber);
		}
	}
}
