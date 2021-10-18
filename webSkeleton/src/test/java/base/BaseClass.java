package base;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.zeroturnaround.zip.ZipUtil;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentAventReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import config.Constants;
import utility.ExcelReader;
import utility.SendEmail;

public class BaseClass {

	protected ExtentTest test;
	protected static ExtentReports report;
	protected static ExtentSparkReporter spark;
	protected static ExtentAventReporter avent;
	protected ExcelReader xls;
	protected static ExtentTest node;
	protected String testName;
	protected boolean skip = false;


	@BeforeSuite(alwaysRun = true)
	public void initiateReport() throws IOException {
		report = new ExtentReports();
		spark = new ExtentSparkReporter("ExtentReport/"+getDate()+"/");
		avent = new ExtentAventReporter("/user/build/");
		
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle("TipWeb");

		report.attachReporter(spark, avent);
	}

	@AfterSuite(alwaysRun = true)
	public void closeReport() {
		report.flush();
		if(Constants.isEmailReport) {
			String path = System.getProperty("user.dir")+"/ExtentReport/"+getDate();
			SendEmail send = new SendEmail();
			ZipUtil.pack(new File(path), new File(path+".zip"));
			send.attach(path+".zip");
		}
	}

	public String getDate() {
		return java.time.LocalDate.now().toString();
	}
}
