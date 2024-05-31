package mainPackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CommonMethods 
{
	public static String getCalculatedDate(String dateRaw)
	{
		try
		{
			
			// create instance of the SimpleDateFormat that matches the given date  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	          
	        //create instance of the Calendar class and set the date to the given date  
	        Calendar cal = Calendar.getInstance();  
	          cal.setTime(sdf.parse(dateRaw));
	          
	        System.out.println(sdf.format(cal.getTime())+" is the date before adding days");  
	          
	        // use add() method to add the days to the given date  
	        cal.add(Calendar.DAY_OF_MONTH, 62);  
	        String dateAfter = sdf.format(cal.getTime());  
	          
	        //date after adding three days to the given date  
	        System.out.println(dateAfter+" is the date after adding 62 days.");
	        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
	        Date date = sdf.parse(dateAfter.trim());
		    System.out.println(sdf2.format(date));
		    String d = sdf2.format(date).toString();;
			return sdf2.format(date).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	public static String getCurrentDate()
    {
    	 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");  
		 LocalDateTime now = LocalDateTime.now();  
		// System.out.println(dtf.format(now));
		String d =  dtf.format(now);
		return d;
    }
	
	public static boolean compareDates(String date1, String date2)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			System.out.println(sdf.parse(date1).before(sdf.parse(date2)));
			if(sdf.parse(date1).before(sdf.parse(date2)))
				return true;
				else return false;
		}
		catch(Exception e)
		{
		return false;
		}
	}
	
	
	static void excelCreationWithQuery() throws Exception {
		//Get Today's date in MMddyyyy format
		LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        String date = dateObj.format(formatter);
        System.out.println(date);
        String filename ;
		try   
		{  
		filename = AppConfig.excelFileLocation+"\\CheckNumberUpdate_"+date+".xlsx";  
		File file = new File(filename);
		//if file exists, delete and re create it
		if(file.exists())
		{
			file.delete();
		}
		Workbook wb = new XSSFWorkbook();
		Sheet sheet1 = wb.createSheet("Sheet 1");
		Row header = sheet1.createRow(0);
		header.createCell(0).setCellValue("Company");
		header.createCell(1).setCellValue("PaymentEntityID");
		header.createCell(2).setCellValue("CheckNumber");
		header.createCell(3).setCellValue("AutomationStatus");
		header.createCell(4).setCellValue("Automation_Notes");
		header.createCell(5).setCellValue("Automation_CompletionDate");

		boolean getBuildings =  DataBase.getCompletedBuildingsList();
		if(getBuildings==true&&RunnerClass.completedBuildingList.length !=0)
		{
			for(int i=0;i<RunnerClass.completedBuildingList.length;i++)
			{
				String company = RunnerClass.completedBuildingList[i][0];
				String PaymentEntityID = RunnerClass.completedBuildingList[i][1].trim();
				String CheckNumber = RunnerClass.completedBuildingList[i][2].trim();
				String AutomationStatus = RunnerClass.completedBuildingList[i][3];
				String Automation_Notes = RunnerClass.completedBuildingList[i][4];
				String Automation_CompletionDate = RunnerClass.completedBuildingList[i][5];
				Row row = sheet1.createRow(1+i);
				row.createCell(0).setCellValue(company);
				row.createCell(1).setCellValue(PaymentEntityID);
				row.createCell(2).setCellValue(CheckNumber);
				row.createCell(3).setCellValue(AutomationStatus);
				row.createCell(4).setCellValue(Automation_Notes);
				row.createCell(5).setCellValue(Automation_CompletionDate);
				
			}
		
		}
		
		System.out.println("Last row in the sheet = "+sheet1.getLastRowNum());
		FileOutputStream fileOut = new FileOutputStream(filename);  
		wb.write(fileOut);
		wb.close();
		fileOut.close();  
		System.out.println("Excel file has been generated successfully.");
		if(RunnerClass.completedBuildingList.length != 0) {
			
			CommonMethods.sendFileToMail(filename);
			}
		}   
		catch (Exception e)   
		{  
		e.printStackTrace();  
		}  
		
		//Send Email the attachment
	}

    static void sendFileToMail(String file) throws MessagingException {
        // SMTP configuration
        String smtpHost = "smtp.office365.com";
        String smtpPort = "587";
        String emailFrom = "naveen.p@beetlerim.com";
        String emailTo = "naveen.p@beetlerim.com,gopi.v@beetlerim.com,dahoffman@homeriver.com";

        // Sender's credentials
        final String username = "naveen.p@beetlerim.com";
        final String password = "Beetlerim@123";

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create a Session object
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Create a MimeMessage object
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
        message.setSubject("Exception Report For Payment Check Number");

        // Create MimeBodyPart and attach the Excel file
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeBodyPart.attachFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // Create Multipart object and add MimeBodyPart objects to it
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        // Set the content of the email
        message.setContent(multipart);

        // Send the email
        session.setDebug(true);
        Transport.send(message);

        System.out.println("Email sent successfully!");
    }
	

}
