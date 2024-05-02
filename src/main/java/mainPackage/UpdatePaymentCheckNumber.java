package mainPackage;




import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;


public class UpdatePaymentCheckNumber {
	
	public static boolean updateCheckNumber(WebDriver driver,String checkNumber) {
		String failedReason = "";
		Actions actions = new Actions(driver);
		try {
			actions.moveToElement(driver.findElement(Locators.RefNumber)).build().perform();
			driver.findElement(Locators.RefNumber).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
			driver.findElement(Locators.RefNumber).sendKeys(checkNumber);
			String updatedCheckNumberFieldValue = driver.findElement(Locators.RefNumber).getAttribute("value");
			String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
			String subWindowHandler = null;
			// RunnerClass.baseRentFromPW = UpdatedBaseRentFieldValue;
			Thread.sleep(2000);
			try {
				actions.moveToElement(driver.findElement(Locators.attachFileButton)).build().perform();
				driver.findElement(Locators.attachFileButton).click();
				Thread.sleep(2000);
				Set<String> handles = driver.getWindowHandles(); // get all window handles
				Iterator<String> iterator = handles.iterator();
				while (iterator.hasNext()){
				    subWindowHandler = iterator.next();
				    if(!subWindowHandler.equalsIgnoreCase(parentWindowHandler))			
		            { 
				    	driver.switchTo().window(subWindowHandler); // switch to popup window
						//driver.findElement(Locators.cancelChooseFilePopUp).click();
				    	String fileName = AppConfig.pdfUploadFilePath+checkNumber+".pdf";
						driver.findElement(Locators.chooseFile).sendKeys(fileName);
						Thread.sleep(500);
						driver.findElement(Locators.uploadChooseFilePopUp).click();
						break;
		            }
				}
				
				driver.switchTo().window(parentWindowHandler); 
			}
			catch (Exception e) {
				failedReason = failedReason + "Error in File attachment";
				RunnerClass.setFailedReason(failedReason);
				return false;
			}
			
			if (AppConfig.saveButtonOnAndOff == false) {
				actions.moveToElement(driver.findElement(Locators.cancelPayment)).build().perform();
				driver.findElement(Locators.cancelPayment).click();
				return true;
			} else {
				actions.moveToElement(driver.findElement(Locators.savePayment)).build().perform();
				driver.findElement(Locators.savePayment).click();
				PropertyWare.evictionPopUp(driver);
				Thread.sleep(2000);
				try {
					driver.switchTo().alert().accept();
					failedReason = "";
					System.out.println("Updated Check Number = " + updatedCheckNumberFieldValue);
					RunnerClass.setFailedReason(failedReason);
				} catch (Exception e) {
				}

				try {
					if (driver.findElement(Locators.errorMessage).isDisplayed()) {
						failedReason = failedReason + "There is an error while saving Check Number";
						System.out.println("Check Number = " + updatedCheckNumberFieldValue);
						RunnerClass.setFailedReason(failedReason);
						return false;
					}
				} catch (Exception e) {
				}
				return true;
			}

		} catch (Exception e) {
			failedReason = failedReason + "Check Number could not be saved";
			RunnerClass.setFailedReason(failedReason);
			return false;
		}
	}

}
