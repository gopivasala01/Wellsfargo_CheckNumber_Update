package mainPackage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class SplitPDFFileFromSharedFolder {
	public static void SplitPDFFromSharedFolder() throws Exception 
	{
	        String pdfFilePath = AppConfig.downloadFilePath+"2_20240530.pdf";
	       // String outputFolder = "path/to/output/folder";

	        PDDocument document = PDDocument.load(new File(pdfFilePath));
            
	            PDFTextStripper stripper = new PDFTextStripper();
	            // Iterate through each page and extract text
	            for (int i = 1; i <= document.getNumberOfPages(); i++) 
	            {
	            	
	                stripper.setStartPage(i);
	                stripper.setEndPage(i);
	                String text = stripper.getText(document);
	                // Do whatever you want with the extracted text
	                //System.out.println("Page " + i + " text: " + text);
	                if(text.contains("Check #:"))
	                {
	                	text = text.substring(text.indexOf("Check #:"));
	                Pattern pattern = Pattern.compile("\\b\\d{10}\\b"); //Check #:\\s*(\\d+)
	                Matcher matcher = pattern.matcher(text);

	                if(matcher.find()) 
	                {
	                    String number = matcher.group(0);
	                    System.out.println("Extracted number: " + number.trim());
	                    String checkNumber =number.trim();
	                    // Write the extracted text to a new PDF file
	                    PDDocument singlePageDocument = new PDDocument();
	                    singlePageDocument.addPage(document.getPage(i-1));

	                    // Save the single page as a separate PDF file
	                    String outputFileName = AppConfig.pdfUploadFilePath + checkNumber + ".pdf";
	                    singlePageDocument.save(new File(outputFileName));

	                    // Close the single page document
	                    singlePageDocument.close();
	                    
	                }
	                }
	            }
	        

	}

}
