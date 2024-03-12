package mainPackage;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;


public class RunnerClass {
	
    public static String[][] pendingLeases;
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
		if (PropertyWare.selectLease(driver,company,paymentEntityID) == false) {
			failedReason = getFailedReason();
			String query = "Update WF_DailyPayments set AutomationStatus='Failed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
			DataBase.updateTable(query);
			previousRecordCompany = company;	
	    	failedReason="";
			
		}
		else {
				if (UpdatePaymentCheckNumber.updateCheckNumber(driver,checkNumber) == false) {
					failedReason = getFailedReason();
					String query = "Update WF_DailyPayments set AutomationStatus='Failed',Automation_Notes='"
							+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
					DataBase.updateTable(query);
			    	failedReason="";
				}
				else {
					// Update table for successful lease
					try {
						System.out.println("Check Number Updated");
						failedReason = getFailedReason();
						String query = "Update WF_DailyPayments set AutomationStatus='Completed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
						DataBase.updateTable(query);
				    	failedReason="";
						
					} catch (Exception e) {}
				}
			}
		}
		
    
    public static String getFailedReason() {
    	if(failedReasonThreadLocal.get().isEmpty()) {
    		return "";
    	}
    	else {
    		 return failedReasonThreadLocal.get();
    	}
    }

    public static void setFailedReason(String failedReason) {
    	failedReasonThreadLocal.set(failedReason);
    }
    
        
        
        // Add your test code here

  /*  @AfterMethod
    public void getResult(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL,result.getThrowable());
        }
        else if(result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, result.getTestName());
        }
        else {
            test.log(Status.SKIP, result.getTestName());
        }
    } */
 
    
   @SuppressWarnings("deprecation")
   @AfterMethod
   public void tearDown() throws IOException, InterruptedException {
	   ChromeDriver driver = driverThreadLocal.get();
	   try {
	       if (driver != null) {
	           // Close the browser window
	           driver.close();
	           
	           // Check if the WebDriver process is still running
	           if (isProcessRunning(driver)) {
	               // If the process is still running, forcibly kill it
	               driver.close();
	               killChromeDriverProcess();
	           }
	       }
	   } catch (WebDriverException e) {
	       System.out.println("WebDriverException occurred while quitting the driver: " + e.getMessage());
	   } finally {
	       // Ensure proper cleanup
	       try {
	           // Wait for a short period for the browser process to terminate
	           Thread.sleep(2000);
	       } catch (InterruptedException ignored) {
	           Thread.currentThread().interrupt();
	       }
	       // Remove the driver from the thread local
	       driverThreadLocal.remove();
	   }
   }
   
  /* @AfterTest
   public void tearDownReports() {
       //to write or update test information to reporter
       extent.flush();
   }*/

	private boolean isProcessRunning(WebDriver driver) {
	    try {
	        // Check if the WebDriver process is still running
	        driver.getTitle(); // Accessing a method to check if the WebDriver is still alive
	        return true;
	    } catch (Exception e) {
	        // WebDriver process has terminated
	        return false;
	    }
	}
	
	public void killChromeDriverProcess() {
	    String os = System.getProperty("os.name").toLowerCase();
	    String processName = "";

	    // Determine the process name based on the operating system
	    if (os.contains("win")) {
	        processName = "chromedriver.exe";
	    } else if (os.contains("mac")) {
	        processName = "chromedriver";
	    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
	        processName = "chromedriver";
	    } else {
	        System.out.println("Unsupported operating system: " + os);
	        return;
	    }

	    try {
	        // Execute platform-specific command to kill the process
	        Runtime.getRuntime().exec("pkill -f " + processName); // For Unix-like systems
	        // Runtime.getRuntime().exec("taskkill /F /IM " + processName); // For Windows
	    } catch (IOException e) {
	        System.out.println("Error killing ChromeDriver process: " + e.getMessage());
	    }
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
}