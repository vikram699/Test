package pageObject;

import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class SalesPage extends DashBoardPage{

	public SalesPage(String PropPath, ExtentTest log) {
		super(PropPath, log);
	}

	public SalesPage selectJobTicket(String companyName) {
		myFindVisibility(prop.getProperty("selectJob1_xpath")+companyName+prop.getProperty("selectJob2_xpath"), 180).click();
		log.log(Status.INFO, "Clicking on First Job by company "+companyName);
		return this;
	}

	public SalesPage updateTicketandCreate(String DurationDays, String NumberofTrucks) {
		sendKeys("durationDays_xpath", DurationDays);
		sendKeys("numberofTrucks_xpath", NumberofTrucks);

		log.log(Status.INFO, "Updated the ticket details by "+DurationDays+" of days and "+NumberofTrucks+" of Trucks");

		clickableElement("SaveBtn_xpath");
		try {
			clickableElement("createTicketBtn_xpath");
		} catch (Exception e) {				
			for(WebElement el : getLocators("//img//parent::div[contains(@role,'button')]")) {
				if(el.isDisplayed()) {
					el.click();
				}
			}
			clickableElement("//button[contains(text(),'Create Tickets')]");
		}
		clickableElement("SaveBtn_xpath");
		return this;
	}

	public Hashtable<String,String> returnTicketNumber(Hashtable<String,String> data) throws InterruptedException{
		clickableElement("ticketMenu_xpath");
		myFind("//span[contains(text(),'Ticket Number')]", 120);
		actionHoverClick("//span[contains(text(),'Ticket Number')]/../..//table");
		//myFindClickable("//span[contains(text(),'Ticket Number')]/../..//child::a[contains(@title,'Sort Ascending')]/..", 120).click();
		clickableElement("SaveBtn_xpath");
		Thread.sleep(6000);
		String[] arrOfStr = java.time.LocalDate.now().toString().split("-");
		String date = arrOfStr[1]+arrOfStr[2]+arrOfStr[0].substring(2, 4);
		String xpath = "//table[contains(@summary,'Tickets')]//child::span[contains(text(),'Pending')]//../../../..//td[1]//child::a[contains(text(),'"+date+"')]";
		searchTicket(date);
		Thread.sleep(6000);
		List<WebElement> el = getLocators(xpath);
		data.put("TicketNumber", el.get(el.size()-1).getText());
		return data;
	}

	private void searchTicket(String date) {
		clickableElement("//span[contains(text(),'Add')]/..");
		clickableElement("//tr[contains(@role,'menuitem')]//child::td[contains(text(),'Ticket Number')]");
		clickableElement("//input[contains(@value,'Starts')]");
		clickableElement("//li[contains(text(),'Contains')]");
		sendKeys("//input[contains(@maxlength,'80')]", date);
		clickableElement("//span[contains(text(),'Search')]/..");
	}

}
