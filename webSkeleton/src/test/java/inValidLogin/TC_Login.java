package inValidLogin;

import java.io.IOException;
import java.util.Hashtable;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import base.BaseClass;
import config.Constants;
import config.DataUtils;
import pageObject.SalesPage;
import utility.ExcelReader;
import utility.Util;

public class TC_Login extends BaseClass{
	
	private SalesPage page;

	public TC_Login() {
		testName = this.getClass().getSimpleName();
		xls = new ExcelReader(Constants.DATAFILE); // Loading the Excel Sheet 
	}

	@BeforeTest()
	public void setUpReport() throws IOException {
		if(spark==null) 
			initiateReport();
		spark.start();
		test = report
				.createTest(testName)
				.assignAuthor(System.getProperty("user.name"))
				.assignCategory("LOGIN");
	}

	@BeforeMethod()
	public void verifyTestExecution() {
		if (DataUtils.isSkip(xls, testName)){
			test.log(Status.SKIP, "Skipping the test as runmode is NO in the Excel Sheet");
			skip = true;
			throw new SkipException("Skipping test case" + testName + " as runmode set to NO in excel");
		}
	}

	@Test(dataProvider = "getWebData")
	public void LoginCheck(Hashtable<String,String> data) throws InterruptedException {
		if (!data.get("RunMode").equalsIgnoreCase("N")){
			node = test.createNode(data.get("TestName"));
			node.assignDevice(data.get("os")+"__"+data.get("browser"));
			page = new SalesPage(Constants.BadgerObjectRepository, node);

			ITestResult testResult = Reporter.getCurrentTestResult();
			testResult.setAttribute("Util", page);

			page
			.setBrowserStackCap(data, "Badger")
			.launchInstance(data.get("browser"));
			page
			.openApplication(data.get("Url"))
			.getLogin(data)
			.verifyLogin(data.get("PostLogin"))
			.closeInstance();
		}else {
			node.log(Status.SKIP, "Skipping the test as runmode is NO in the Excel Sheet");
		}
	}

	@DataProvider()
	public Object[][] getWebData(){
		return DataUtils.getData(xls, testName, "Web");
	}

	@AfterMethod
	public void getResult(ITestResult result) throws Exception{
		Util page = (Util) result.getAttribute("Util");

		if(result.getStatus() == ITestResult.FAILURE){
			node.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
			node.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));
			String screenshotPath = page.getScreenShot(page.getDriver(), result.getName());
			//To add it in the extent report 
			node.fail("Test Case Failed Snapshot is below " + node.addScreenCaptureFromPath(screenshotPath));
			page.updateBrowserStack("failed",result.getThrowable());
			page.getDriver().quit();
		}
		else if(result.getStatus() == ITestResult.SKIP)
			node.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped", ExtentColor.ORANGE));
		else if(result.getStatus() == ITestResult.SUCCESS)
			node.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
	}
	
	@AfterTest
	public void teardown() {
		spark.stop();
		report.flush();
	}
}
