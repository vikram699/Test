package config;

import org.testng.IRetryAnalyzer;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements IRetryAnalyzer, ITestListener{
	
	private int counter = 0;
	private int retryLimit = 0;
	
	public void onTestFailure(ITestResult result) {
		if(result.isSuccess()) {
			System.out.println("Test Pass Successfull");
		}else {
			for(StackTraceElement data: result.getThrowable().getStackTrace()) {
				System.out.println(data.toString());
			}
		}
	}

	@Override
	public boolean retry(ITestResult result) {
		if(counter < retryLimit)
		{
			counter++;
			return true;
		}
		return false;
	}
}
