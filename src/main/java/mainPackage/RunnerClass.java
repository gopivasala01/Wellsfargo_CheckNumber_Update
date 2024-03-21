package mainPackage;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;


public class RunnerClass {
	
    public static String[][] pendingLeases;
    public static String[][] completedBuildingList;
	public static String downloadFilePath;
	public static WebDriverWait wait;
	
	public static String previousRecordCompany;
	public static boolean loggedOut = false;

    // Use ThreadLocal to store a separate ChromeDriver instance for each thread
    private static ThreadLocal<ChromeDriver> driverThreadLocal = new ThreadLocal<ChromeDriver>();
    private static ThreadLocal<String> failedReasonThreadLocal = new ThreadLocal<>();
    
    
    @BeforeMethod
    public boolean setUp(){
        // Set up WebDriverManager to automatically download and set up ChromeDriver
    	//System.setProperty("webdriver.http.factory", "jdk-http-client");
    	try {
    			WebDriverManager.chromedriver().clearDriverCache().setup();
    		 	//WebDriverManager.chromedriver().setup();
    	        RunnerClass.downloadFilePath = AppConfig.downloadFilePath;
    			Map<String, Object> prefs = new HashMap<String, Object>();
    		    // Use File.separator as it will work on any OS
    		    prefs.put("download.default_directory",RunnerClass.downloadFilePath);
    	        ChromeOptions options = new ChromeOptions();
    	        options.addArguments("--remote-allow-origins=*");
    	       // options.addArguments("--headless");
    	        options.addArguments("--disable-gpu");  //GPU hardware acceleration isn't needed for headless
    	        options.addArguments("--no-sandbox");  //Disable the sandbox for all software features
    	        options.addArguments("--disable-dev-shm-usage");  //Overcome limited resource problems
    	        options.addArguments("--disable-extensions");  //Disabling extensions can save resources
    	        options.addArguments("--disable-plugins");
    	        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
    	        // Create a new ChromeDriver instance for each thread
    	        ChromeDriver driver = new ChromeDriver(options);
    	        driver.manage().window().maximize();
    	        //test = extent.createTest("Login Page");
    	        // Store the ChromeDriver instance in ThreadLocal
    	        driverThreadLocal.set(driver);
    	        driver.get(AppConfig.URL);
    	        driver.findElement(Locators.userName).sendKeys(AppConfig.username);
    	        driver.findElement(Locators.password).sendKeys(AppConfig.password);
    	        Thread.sleep(2000);
    	        driver.findElement(Locators.signMeIn).click();
    	        Thread.sleep(3000);
    	        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    	        
    	        try
    	        {
    	        if(driver.findElement(Locators.loginError).isDisplayed())
    	        {
    	        	System.out.println("Login failed");
    				return false;
    	        }
    	        }
    	        catch(Exception e) {}
    	       
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
        
        
    }

    @Test(dataProvider = "testData")
    public void testMethod(String ID,String company, String paymentEntityID,String checkNumber) throws Exception {
    	String failedReason="";
    	
    	System.out.println("<-------- "+paymentEntityID+" -------->");
    	// Retrieve the thread-specific ChromeDriver instance from ThreadLocal
        ChromeDriver driver = driverThreadLocal.get();
        if(company.equalsIgnoreCase("Chicago PFW")) {
			   company = "Chicago";
		  }
		if(company.equalsIgnoreCase("California PFW")) {
			   company = "California pfw";
		  }
		try {
			String expiredURL = driver.getCurrentUrl();
			if(expiredURL.contains("https://app.propertyware.com/pw/expired.jsp") || expiredURL.equalsIgnoreCase("https://app.propertyware.com/pw/expired.jsp?cookie") || expiredURL.contains(AppConfig.URL)) {
				loggedOut = true;
				driver.navigate().to(AppConfig.URL);
				driver.findElement(Locators.userName).sendKeys(AppConfig.username); 
				driver.findElement(Locators.password).sendKeys(AppConfig.password);
			    Thread.sleep(2000);
			    driver.findElement(Locators.signMeIn).click();
			    Thread.sleep(3000);
			}
		}
		catch(Exception e) {}
		try {
			if (PropertyWare.selectLease(driver,company,paymentEntityID) == false) {
				failedReason = getFailedReason();
				String query = "Update WF_DailyPayments set AutomationStatus='Failed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
				DataBase.updateTable(query);
				previousRecordCompany = company;	
				
			}
			else {
					if (UpdatePaymentCheckNumber.updateCheckNumber(driver,checkNumber) == false) {
						failedReason = getFailedReason();
						String query = "Update WF_DailyPayments set AutomationStatus='Failed',Automation_Notes='"
								+ failedReason + "',Automation_CompletionDate = getdate() where ID = '" + ID + "'";
						DataBase.updateTable(query);
					}
					else {
						// Update table for successful lease
						try {
							System.out.println("Check Number Updated");
							failedReason = getFailedReason();
							if(failedReason == null) {
								failedReason="";
							}
							String query = "Update WF_DailyPayments set AutomationStatus='Completed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
							DataBase.updateTable(query);
							
						} catch (Exception e) {}
					}
				}
		}
		catch(Exception e) {}
		finally {
			setFailedReason(null);
			driver.quit();
		}
	
		}
		
    
    public static String getFailedReason() {
    	try {
        	return failedReasonThreadLocal.get();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
		
    }

    public static void setFailedReason(String failedReason) {
    	failedReasonThreadLocal.set(failedReason);
    }
 

    @DataProvider(name = "testData", parallel = true)
    public Object[][] testData() {
    	try {
			DataBase.getLeasesList(AppConfig.pendingLeasesQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return pendingLeases;
    }
    
    @AfterSuite
    public void sendMail(){
    	try {
			CommonMethods.excelCreationWithQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}