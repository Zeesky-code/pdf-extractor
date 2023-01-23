import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Extractor {
	static PDFTextStripper pdfStripper;

	static {
		try {
			pdfStripper = new PDFTextStripper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		DBConnector.createDB();
		System.out.println("Starting extraction of judgements");
		ArrayList<URL> urlList= new ArrayList<>();
		urlList.add(new URL("https://www.danistay.gov.tr/assets/pdf/yayinlar/dergi/15_03_2019_015742.pdf"));
		urlList.add(new URL("https://www.danistay.gov.tr/assets/pdf/yayinlar/dergi/23_10_2020_095628.pdf"));
		for (int i = 0; i < urlList.size(); i++) {
			try {

				URL pdfUrl = urlList.get(i);
				InputStream in = pdfUrl.openStream();
				BufferedInputStream bf = new BufferedInputStream(in);
				PDDocument doc = PDDocument.load(bf);
				ArrayList <Integer> sectionPages = getSectionPages(doc);
				
				PreparedStatement Pstmt = DBConnector.createConnection();

				for (int j = 0; j < sectionPages.size()-1; j++) {
					getText(sectionPages.get(j),sectionPages.get(j + 1),doc,Pstmt);
				}



			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
		System.out.println("Finished extraction of judgements");
	}
	public static ArrayList getSectionPages(PDDocument doc ) throws IOException {

		int i = 5;
		pdfStripper.setStartPage(i);
		pdfStripper.setEndPage(i);

		//Retrieving text from PDF document
		String text = pdfStripper.getText(doc);
		int start = text.indexOf("JUDGEMENTS OF TURKISH COUNCIL OF STATE");
		int end = text.indexOf("Publications of Turkish Council of State");
		text = text.substring(start,end);

		Pattern titlePattern = Pattern.compile("\\-{5}\\s\\d+");
		Matcher titleMatcher = titlePattern.matcher(text);

		ArrayList<Integer> sectionPages = new ArrayList<>();
		while (titleMatcher.find()) {
			String matchedText = titleMatcher.group();
			String digit = matchedText.replaceAll("\\D", "");
			int pageNumber = Integer.parseInt(digit);
			sectionPages.add(pageNumber);
		}
		/***
		 * remove first section to avoid duplicate storage of translated judgement
		 * Note: The English judgement is being removed.
		 */
		sectionPages.remove(0);
		return sectionPages;
	}
	public static void getText(int start, int stop, PDDocument doc,PreparedStatement Pstmt) throws IOException, SQLException {
		PDFTextStripper stripper =  new PDFTextStripper();
		stripper.setStartPage(start);
		stripper.setEndPage(stop-1);

		String text = stripper.getText(doc);
		int textStart = text.indexOf("T.C.");
		text = text.substring(textStart);


		String kararNo = "Karar No : \\s*(\\d+/\\d+)";
		String Daire = "(.*) Daire";
		String esasNo = "Esas No : \\s*(\\d+/\\d+)";

		String[] judgments = text.split("T.C.\\n*\\s*(.*)");

		for (String judgment : judgments) {
			Pattern pattern1 = Pattern.compile(kararNo);
			Matcher matcher1 = pattern1.matcher(judgment);

			Pattern pattern2 = Pattern.compile(Daire);
			Matcher matcher2 = pattern2.matcher(judgment);

			Pattern pattern3 = Pattern.compile(esasNo);
			Matcher matcher3 = pattern3.matcher(judgment);

			while (matcher1.find() && matcher2.find() && matcher3.find()) {

				String chamber = matcher2.group(1) +" Daire";
				Pstmt.setString(1,chamber);

				String docketNumber = matcher3.group(1);
				Pstmt.setString(2,docketNumber);

				String decisionNumber = matcher1.group(1);
				Pstmt.setString(3,decisionNumber);


				int judgmentStart = judgment.indexOf("Anahtar Kelimeler :");
				if(judgmentStart == -1){
					judgmentStart = judgment.indexOf("Anahtar Kelimeler:");
				}
				judgment= judgment.substring(judgmentStart);
				Pstmt.setString(4,judgment);

				Pstmt.executeUpdate();
			}


		}

	}

}

