package mainPackage;

import org.openqa.selenium.By;

public class Locators 
{
	public static By userName = By.id("loginEmail");
	public static By password = By.name("password");
	public static By signMeIn = By.xpath("//*[@value='Sign Me In']");
	public static By loginError = By.xpath("//*[@class='toast toast-error']");
	
	public static By marketDropdown = By.id("switchAccountSelect");
	public static By buildingTitle = By.id("summaryTitleBuilding");
	public static By buildingDeactivatedMessage = By.xpath("//*[text()='This Building has been deactivated']");
	public static By leaseStatusInApplication = By.xpath("//div[@class='customLeaseStatus']");
	public static By leasesTab = By.xpath("//*[@class='tabbedSection']/a[4]");	
    public static By leasesTab2 = By.xpath("(//a[text()='Leases'])[2]");
    public static By tenantContact = By.xpath("//*[@id='buildingLeaseList']/tbody/tr/td[2]/a");
    public static By leaseList = By.xpath("//*[@id='buildingLeaseList']/tbody/tr/td[1]/a");
    public static By popUpAfterClickingLeaseName = By.xpath("//*[@id='viewStickyNoteForm']");
    public static By scheduledMaintanancePopUp = By.xpath("//*[text()='Scheduled Maintenance Notification']");
    public static By scheduledMaintanancePopUpOkButton = By.id("alertDoNotShow");
    public static By popupClose = By.xpath("//*[@id='editStickyBtnDiv']/input[2]");
    public static By permissionDenied = By.xpath("//*[contains(text(),'Permission Denied')]");
    public static By evictionPopUp = By.xpath("//div[@id='evictionRestrictionsForm']");
    public static By evictionNotAcceptPaymentCheckbox = By.xpath("//input[@id='notAcceptPayments']");
    public static By evictionNotAllowportalCheckbox = By.xpath("//input[@id='notAllowPortalAccess']");
    public static By saveEvictionPopUp = By.xpath("//div[@classname='primaryButtons']//input[@value='Save']");
    
    public static By scheduleMaintananceIFrame = By.xpath("//iframe[@srcdoc='<meta name=\"referrer\" content=\"origin\" />']");
    public static By scheduleMaintanancePopUp2 = By.xpath("//section[@role='dialog']");
    public static By maintananceCloseButton = By.xpath("//a[@aria-label='Close modal']");
    
    public static By portfolioName = By.xpath("//div[@class='summaryPanelSection3']/a[1]");
    
    public static By summaryEditButton = By.xpath("//*[@value='Edit']");
    public static By BaseRentFieldinPW = By.xpath("//input[@name='entity.baseRentAsString']");
    public static By utilityConnectionRequest = By.xpath("//*[text()='Utility Connection Request']/following::Select[1]");
    public static By lockBoxCode = By.xpath("//*[text()='Lockbox Code']/following::input[1]");
    public static By filter_Other = By.xpath("//*[text()='Filter - Other']/following::input[1]");
    public static By MOIInspectionDate = By.xpath("//*[text()='MOI Inspection Date']/following::input[1]");
    public static By turnOverHandledBy = By.xpath("//*[text()='Turnover Handled By']/following::Select[1]");
    public static By turnEstimateSubmissionDate = By.xpath("//*[text()='Turn Estimate Submission Date']/following::input[1]");
    public static By turnEstimateCost = By.xpath("//*[text()='Turn Estimate Cost']/following::input[1]");
    public static By turnApprovalDate = By.xpath("//*[text()='Turn Approval Date']/following::input[1]");
    public static By turnStartDate = By.xpath("//*[text()='Turn Start Date']/following::input[1]");
    public static By turnTargetCompletionDate = By.xpath("//*[text()='Turn Target Completion Date']/following::input[1]");
    public static By turnActualCompletionDate = By.xpath("//*[text()='Turn Actual Completion Date']/following::input[1]");
    public static By turnActualCost = By.xpath("//*[text()='Turn Actual Cost']/following::input[1]");
    public static By turnQCCompletedDate = By.xpath("//*[text()='Turn QC Completed Date']/following::input[1]");
    public static By codeBoxActive = By.xpath("//*[text()='Codebox Active']/following::input[1]");
    public static By lastVacantVisit = By.xpath("//*[text()='Last Vacant Visit']/following::input[1]");
    
    public static By status = By.xpath("//*[@id='infoTable']/tbody/tr[6]/td[2]");
    
    public static By savePayment = By.xpath("//*[@value='Save']");
    public static By cancelPayment = By.xpath("//input[@value='Cancel']");
    
    public static By saveBuilding = By.xpath("(//*[@class='primaryButtons'])[2]/input[1]");
    public static By cancelBuilding = By.xpath("(//*[@class='primaryButtons'])[2]/input[4]");
    
    public static By newAutoCharge = By.xpath("//*[@value='New Auto Charge']");
    public static By rcField = By.xpath("//*[text()='RC']/following::input[1]");
    public static By autoCharge_Description = By.name("charge.description");
    public static By autoCharge_List = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[1]");
    public static By autoCharge_List_Amounts = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[3]");
    public static By autoCharge_StartDate = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[5]");
    public static By autoCharge_EndDate = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[6]");
    public static By autoCharge_Amount = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[3]");
    public static By autoCharge_description = By.xpath("//*[@id='autoChargesTable']/tbody/tr/td[8]");
    public static By autoCharge_CancelButton = By.xpath("//*[@id='editAutoChargeForm']/descendant::div[4]/input[2]");
    public static By autoCharge_SaveButton = By.xpath("(//*[@class='primaryButtons'])[3]/input[1]");
    
    public static By portfolioText = By.xpath("//*[@class='summaryPanelSection3']/a[1]");
    public static By noAutoCharges = By.xpath("//*[text()='No Charges']");
    public static By RefNumber = By.name("check.refNo");
    public static By Memo = By.name("check.comments");
    public static By errorMessage =By.id("errorMessages");
    public static By attachFileButton =By.id("showAttachmentsFormButton");
    public static By chooseFile = By.name("file(1)");
    public static By cancelChooseFilePopUp = By.xpath("//*[@value='Cancel']");
    public static By uploadChooseFilePopUp = By.xpath("//*[@value='Upload']");
    		

}
