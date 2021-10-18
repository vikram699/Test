package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriver.When;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.locators.RelativeLocator.RelativeBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import config.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Util {

	private WebDriver driver;
	protected Properties prop;
	protected ExtentTest log;
	private FileInputStream fs;
	private DesiredCapabilities browserStackCap;

	/**
	 * Constructor accepts the Properties file Path
	 * @param PropPath : String the properties file Path
	 */
	public Util(String PropPath, ExtentTest log) {
		try {
			prop = new Properties();
			fs = new FileInputStream(PropPath);
			prop.load(fs);
			this.log = log;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			new SkipException("File Not Found");
		} catch (Exception e) {
			e.printStackTrace();
			new SkipException("Execption occured");
		}
	}

	/**
	 * Setting up the browser stack capabilities
	 * 
	 * @param data : {@link Hashtable} passed from the Data provider
	 * @param projectName : {@link String} Name to be displayed on the Project at Browser Stack
	 * @param testRunName : {@link String} TestRunName execution script Name
	 * @return {@link DesiredCapabilities} 
	 */
	public Util setBrowserStackCap(Hashtable<String, String> data, String projectName) {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("browser", data.get("browser"));
		caps.setCapability("browser_version", data.get("browserVersion"));
		caps.setCapability("project", projectName);
		caps.setCapability("name", data.get("TestName"));
		caps.setCapability("autoAcceptAlerts", "true");


		if (data.get("browser").equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("use-fake-device-for-media-stream");
			options.addArguments("use-fake-ui-for-media-stream");
			options.addArguments("--disable-notifications");
			caps.setCapability(ChromeOptions.CAPABILITY, options);
		} else if (data.get("browser").equalsIgnoreCase("Safari")) {
			HashMap<String, Object> safariOptions = new HashMap<String, Object>();
			safariOptions.put("enablePopups", "true");
			safariOptions.put("allowAllCookies", "true");
			safariOptions.put("driver", "2.48");
			caps.setCapability("browserstack.safari.enablePopups", "true");
			caps.setCapability("safari", safariOptions);
		}

		HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
		browserstackOptions.put("os", data.get("os"));
		browserstackOptions.put("osVersion", data.get("os_version"));
		browserstackOptions.put("seleniumVersion", "4.0.0-alpha-6");
		browserstackOptions.put("local", false);
		browserstackOptions.put("idleTimeout", Constants.BSTACK_IDLE_TIMEOUT.toString());
		browserstackOptions.put("autoWait", Constants.BSTACK_IDLE_AUTOWAIT.toString());
		browserstackOptions.put("resolution", "1920x1080"); 
		caps.setCapability("bstack:options", browserstackOptions);
		browserStackCap = caps;
		return this;
	}

	/**
	 * Adding up the browser stack capabilities
	 * 
	 * @param key    Capability KEY
	 * @param values Capability VALUE
	 */
	public void addCapabilities(String key, String values) {
		if (browserStackCap != null)
			browserStackCap.setCapability(key, values);
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Helper method to Launch the Browser instance based on the name passed as
	 * String
	 * 
	 * @param browser Accepts the browser Name
	 * @throws SkipException When No browser name is passed
	 * 
	 */
	public Util launchInstance(String browser) {
		try {
			// Check the Browser-Stack is enabled or not
			if (isBrowserStackEnable() && browserStackCap != null) {
				driver = new RemoteWebDriver(new URL(Constants.URL), browserStackCap);
				log.log(Status.INFO, "Created a new Remote Session for "+browser+ " browser");
			} else if (isGridEnable()) {
				/*public static DockerComposeContainer environment =
					    new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
					            .withExposedService("redis_1", REDIS_PORT, Wait.forListeningPort())
					            .waitingFor("db_1", Wait.forLogMessage("started", 1))
					            .withLocalCompose(true);*/
				// Checking the Grid Flag
				DesiredCapabilities caps = new DesiredCapabilities();
				if(browser.equalsIgnoreCase("Chrome"))
					caps.setBrowserName("chrome");
				else if (browser.equalsIgnoreCase("Firefox"))
					caps.setBrowserName("firefox");
				else if (browser.equalsIgnoreCase("Edge"))
					caps.setBrowserName("MicrosoftEdge");
				driver = new RemoteWebDriver(new URL(Constants.HUB_URL), caps);
				log.log(Status.INFO, "Created a new Remote Session for "+browser+ " browser on GRID");
			} else {
				// Local execution using browser Name
				if (browser.equalsIgnoreCase("Chrome")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("use-fake-device-for-media-stream");
					options.addArguments("use-fake-ui-for-media-stream");
					options.addArguments("--disable-notifications");
					WebDriverManager.chromedriver().setup();
					driver = new ChromeDriver(options);
				} else if (browser.equalsIgnoreCase("Firefox")) {
					WebDriverManager.firefoxdriver().setup();
					driver = new FirefoxDriver();
				} else if (browser.equalsIgnoreCase("Edge")) {
					WebDriverManager.edgedriver().setup();
					driver = new EdgeDriver();
				} else {
					System.err.println("BrowserName is NULL or wrong BrowserName is Passed");
				}
				log.log(Status.INFO, "Created a new Local Session on "+browser+ " browser");
			}
			driver.manage().window().maximize();
		} catch (Exception e) {
			e.printStackTrace();
			new SkipException("Browser did not launched");
		}
		return this;
	}

	/**
	 * Close the running session if any
	 * 
	 * @param driver Driver instance created
	 * @return {@link Boolean}
	 * @exception Exception Unable to Close the connection.
	 * @see Exception
	 */
	public boolean closeInstance() {
		try {
			if (driver != null) {
				if(isBrowserStackEnable() && browserStackCap != null) {
					updateBrowserStack("passed", "Successfully : Completed the Test");
				}
				//driver.close();
				driver.quit();
				log.log(Status.INFO, "Closing browser Instance");
				return true;
			} else
				return false;
		} catch (Exception e) {
			System.err.println("======Unable to close the connection======");
			log.log(Status.FAIL, "======Unable to close the connection======");
			return false;
		}
	}

	/**
	 * Helper Method to Verify the execution is on BrowserStack Environment
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isBrowserStackEnable() {
		if (Constants.isBrowserStackEnabled)
			return true;
		else
			return false;
	}

	/**
	 * Helper Method to Verify the execution is on Grid Environment
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isGridEnable() {
		if (Constants.isGridEnabled)
			return true;
		else
			return false;
	}

	/************************ Utility Functions ********************************/

	/**
	 * Use this method when you want to wait until the visibility of the element
	 * based on the <br>
	 * 
	 * @param locator {@link String} Sending the By instance from the Helper Class file
	 * @return {@link WebElement} 
	 * @exception When no Element is found
	 * @see NoSuchElementException
	 */
	public WebElement myFind(String locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
				.until(ExpectedConditions.presenceOfElementLocated(getBy(locator)));
	}

	public WebElement myFind(WebElement locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL)).until(ExpectedConditions.visibilityOf(locator));
	}

	/**
	 * Use this method when you want to wait until the visibility of the element
	 * based on the <br>
	 * 
	 * @param locator {@link String} Sending the By instance from the Helper Class file
	 * @return {@link WebElement} 
	 * @exception When no Element is found
	 * @see NoSuchElementException
	 */
	public WebElement myFind(String locator, int duration) {
		return new WebDriverWait(driver, Duration.ofSeconds(duration),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locator)));
	}

	public WebElement myFindVisibility(String locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locator)));
	}

	public WebElement myFindVisibility(String locator, int duration) {
		return new WebDriverWait(driver, Duration.ofSeconds(duration),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
				.until(ExpectedConditions.visibilityOfElementLocated(getBy(locator)));
	}

	public WebElement myFindClickable(String locators) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
				.until(ExpectedConditions.elementToBeClickable(getBy(locators)));
	}

	public WebElement myFindClickable(String locators, int duration) {
		return new WebDriverWait(driver, Duration.ofSeconds(duration),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
				.until(ExpectedConditions.elementToBeClickable(getBy(locators)));
	}

	public WebElement myFindClickable(WebElement locators) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
				.until(ExpectedConditions.elementToBeClickable(locators));
	}

	/**
	 * Common For Clicking on any Element -- Case when No element is present or the
	 * Element click InterceptedExpection waits and retry the event
	 * 
	 * @param locator {@link String} Sending the String from the property file
	 * @exception SkipException
	 */
	public void clickElement(String locator) {
		scrollForElementlocator(locator);
		myFind(locator).click();
	}

	public void clickVisibilityElement(String locator) {
		WebElement el = myFindVisibility(locator);
		scrollForElementlocator(el);
		el.click();
	}

	public void clickableElement(String locator) {
		WebElement el = myFindClickable(locator);
		scrollForElementlocator(el);
		el.click();
	}

	public void clickableElement(WebElement locator) {
		WebElement el = myFindClickable(locator);
		scrollForElementlocator(el);
		el.click();
	}

	public void actionClick(String locator) {
		Actions a = new Actions(driver);
		a.click(myFindVisibility(locator)).build().perform();
	}

	public void actionHoverClick(String locator) throws InterruptedException {
		Actions a = new Actions(driver);
		a.moveToElement(myFind(locator));
		Thread.sleep(2000);
		a.click(myFind(locator)).build().perform();
	}

	public void dragDrop(WebElement Sourcelocator, WebElement Destinationlocator) throws InterruptedException {
		Actions action = new Actions(driver);
		action.
		clickAndHold(Sourcelocator).build().perform();
		Thread.sleep(1000);
		action.moveToElement(Destinationlocator).build().perform();
		Thread.sleep(1000);
		action.release(Destinationlocator).build().perform();
		Thread.sleep(1000);
	}


	/**
	 * Common function for sending Data
	 *
	 * @param locator {@link String} The element property name form properties file
	 * @param data {@link String} Value to be set for the given element
	 * @return Nothing .
	 * @exception When no Element is found
	 * @see Exception
	 */
	public void sendKeys(String locator, String data) {
		clearText(locator);
		myFind(locator).sendKeys(data);
	}

	public Boolean verifyText(String locator, String text) {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
					Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.textToBePresentInElementLocated(getBy(locator), text));
		} catch (Exception e) {
			new SkipException("Text Does not Match");
			return false;
		}
	}

	public Boolean verifyText(WebElement locator, String text) {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
					Duration.ofMillis(Constants.DEFAULT_INTERVAL)).ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.textToBePresentInElement(locator, text));
		} catch (Exception e) {
			new SkipException("Text Does not Match");
			return false;
		}
	}

	public void waitUntilInvisible(String locator) {
		new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
		.until(ExpectedConditions.invisibilityOfElementLocated(getBy(locator)));
	}

	public void waitUntilInvisible(WebElement locator) {
		new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
		.until(ExpectedConditions.invisibilityOf(locator));
	}

	public Boolean switchFrame(String locator) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
					Duration.ofMillis(Constants.DEFAULT_INTERVAL))
			.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Common function for sending Data
	 *
	 * @param locator {@link WebElement} Web Element for the searched element
	 * @param data {@link String} Value to be set for the given element
	 * @return Nothing .
	 * @exception When no Element is found
	 * @see Exception
	 */
	public void sendKeys(WebElement locator, String data) {
		new Actions(driver).sendKeys(locator, data).build().perform();
	}

	public String getUrl() {
		return getDriver().getCurrentUrl();
	}

	public String getText(String locator) {
		scrollForCenterElementlocator(locator);
		return myFindVisibility(locator).getText();
	}

	public String getText(String locator, int duration) {
		scrollForCenterElementlocator(locator, duration);
		return myFindVisibility(locator).getText();
	}

	private Boolean scrollForCenterElementlocator(String locator, int duration) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});", myFind(locator, duration));
		return true;
	}

	public String getText(WebElement locator) {
		return myFind(locator).getText();
	}

	/**
	 * Scroll to a Specific Element on the DOM
	 * 
	 * @param locator {@link String} Accepts to locator string
	 * @return {@link Boolean}
	 * @exception When no Element is found
	 * @see Exception
	 */
	public boolean scrollForElementlocator(String locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(false);", myFind(locator));
		return true;
	}

	public boolean scrollForCenterElementlocator(String locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});", myFind(locator));
		return true;
	}
	public boolean scrollForCenterElementlocator(WebElement locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});", locator);
		return true;
	}


	public String getDate() {
		return java.time.LocalDate.now().toString();
	}

	/**
	 * Scroll to a Specific Element on the DOM
	 * 
	 * @param locator {@link String} Accepts to locator string
	 * @param duration {@link Integer} Polling duration
	 * @return {@link Boolean}
	 * @exception When no Element is found
	 * @see Exception
	 */
	public boolean scrollForElementlocator(String locator, int duration) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(false);", myFind(locator, duration));
		return true;
	}

	/**
	 * Scroll to a Specific Element on the DOM
	 * 
	 * @param locator {@link WebElement} Searched WebElement data
	 * @return {@link Boolean}
	 * @exception When no Element is found
	 * @see Exception
	 */
	public boolean scrollForElementlocator(WebElement locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(false);", locator);
		return true;
	}

	/**
	 * Scroll to Bottom of the Page
	 * 
	 */
	public void scrollToBottom() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
	}


	/**
	 * Mouse hover on a specific element
	 *
	 * @param locator Accepts to locator string
	 * @return {@link Boolean} true when successfully hover on the element False if not possible
	 * @exception When no Element is found
	 * @see Exception
	 */
	public boolean mouseoverForElement(String locator) {
		try {
			Actions a = new Actions(driver);
			a.moveToElement(myFind(locator)).build().perform();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Scrolling on a specific element and clicking
	 * 
	 * @param locator Accepts to locator string
	 * @exception When no Element is found
	 * @see Exception
	 */
	public void scrollForClick(String locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", myFind(locator));
	}

	public void scrollForClick(String locator, int timeout) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", myFind(locator, timeout));
	}

	public void scrollForClick(WebElement locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", locator);
	}

	/**
	 * Clearing the text based on the browser
	 * 
	 * @param driver  Sending the driver instance from the test file
	 * @param locator Accepts to locator string
	 * @exception When no Element is found
	 * @see Exception
	 */
	public void clearText(String locator) {
		WebElement el = myFind(locator);
		if (isBrowserStackEnable()) {
			String os = ((RemoteWebDriver) driver).getCapabilities().getCapability("platformName").toString();
			if (os.contains("MAC") || os.startsWith("i")) {
				el.click(); el.sendKeys(Keys.CONTROL + "A"); el.sendKeys(Keys.DELETE);
			} else {
				el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
			}
		} else { // Windows
			el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].value=\"\";", el);
		}
	}

	/**
	 * Returning the by instance as a method
	 * 
	 * @param driver Sending the driver instance from the test file
	 * @return {@link By}
	 * @exception When no Element is found
	 * @see Exception
	 */
	public By getBy(String locator) {
		try {
			if (locator.endsWith("_xpath")) {
				return By.xpath(prop.getProperty(locator));
			} else if (locator.endsWith("_id")) {
				return By.id(prop.getProperty(locator));
			} else if (locator.endsWith("_name")) {
				return By.name(prop.getProperty(locator));
			} else if (locator.endsWith("_className")) {
				return By.className(prop.getProperty(locator));
			} else {
				return By.xpath(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			new SkipException("Skipped because of no locator found");
			return null;
		}
	}

	public RelativeBy getByRelativeLocator(String locator) {
		return RelativeLocator.with(getBy(locator));
	}

	/**
	 * Clearing the text based on the browser
	 * 
	 * @param locator {@link String} Accepts to locator string
	 * @return {@link List} List of all the webElements found based on the element
	 * @exception NoSuchElementException When no Element is displayed
	 * @exception TimeoutException When unable to find withIn the given Time Frame
	 * @see Exception
	 */
	public List<WebElement> getLocators(String locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(Constants.DEFAULT_DURATION),
				Duration.ofMillis(Constants.DEFAULT_INTERVAL))
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getBy(locator)));
	}

	/**
	 * Clearing the text based on the browser
	 * 
	 * @param locator {@link String} Accepts to locator string
	 * @param duration {@link Integer} value for the polling time 
	 * @return {@link List} List of all the webElements found based on the element
	 * @exception NoSuchElementException When no Element is displayed
	 * @exception TimeoutException When unable to find withIn the given Time Frame
	 * @see Exception
	 */
	public List<WebElement> getLocatorsVisibility(String locator, int duration) {
		return new WebDriverWait(driver, Duration.ofSeconds(duration), Duration.ofMillis(duration))
				.until(driver -> isElementPresent(locator));
	}

	/**
	 * Clearing the text based on the browser
	 * 
	 * @param locator {@link String} Accepts to locator string
	 * @return {@link List} List of all the webElements found based on the element
	 * @exception NoSuchElementException When no Element is displayed
	 * @see Exception
	 */
	public List<WebElement> isElementPresent(String locator) {
		return driver.findElements(getBy(locator));
	}

	/**
	 * Switching the driver controls
	 * @param tabNumb {@link Integer} tab number to which switch
	 * @return {@link Boolean}
	 */
	public boolean switchDriverControl(Integer tabNumb) {
		ArrayList<String> numOfTabs = new ArrayList<String>(driver.getWindowHandles());
		if (numOfTabs.size() != 0) {
			driver.switchTo().window(numOfTabs.get(tabNumb));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Switching the driver controls
	 * @param tabNumb {@link String} name of the browser to which need the switch
	 * @return {@link Boolean}
	 */
	public boolean switchDriverControl(String tabName) {
		if (tabName != null) {
			driver.switchTo().window(tabName);
			return true;
		} else {
			return false;
		}
	}

	public int getCurrentTime() {
		return java.time.LocalTime.now().toSecondOfDay();
	}

	public String getTimeDifference(int inital, int second) {
		return String.valueOf(inital - second);
	}

	//This method is to capture the screenshot and return the path of the screenshot.
	public String getScreenShot(WebDriver driver, String screenshotName) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);

		// after execution, you could see a folder "FailedTestsScreenshots" under src folder
		String destination = System.getProperty("user.dir") + "/ExtentReport/" + getDate() + "/" + screenshotName + ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	public void updateBrowserStack(String status, String reason) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""+status+"\", \"reason\": \""+reason+"\"}}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateBrowserStack(String status, Throwable reason) {
		try {
			if(isBrowserStackEnable() && browserStackCap != null) {
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""+status+"\", \"reason\": \""+reason+"\"}}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
