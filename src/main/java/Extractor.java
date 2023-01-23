import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

	public static void main(String[] args) {
		try {
			URL pdfUrl = new URL("https://www.danistay.gov.tr/assets/pdf/yayinlar/dergi/15_03_2019_015742.pdf");
			InputStream in = pdfUrl.openStream();
			BufferedInputStream bf = new BufferedInputStream(in);
			PDDocument doc = PDDocument.load(bf);
			ArrayList <Integer> sectionPages = getSectionPages(doc);
			for (int i = 0; i < sectionPages.size()-1; i++) {
				getText(( sectionPages.get(i)),sectionPages.get(i+1),doc);
			}



		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList getSectionPages(PDDocument doc ) throws IOException {

		int i = 5;
		pdfStripper.setStartPage(i);
		pdfStripper.setEndPage(i);

		//Retrieving text from PDF document
		String text = pdfStripper.getText(doc);
		int start = text.indexOf("JUDGEMENTS OF TURKISH COUNCIL OF STATE WHICH REFER");
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
	public static void getText(int start, int stop, PDDocument doc) throws IOException {
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
				String decisionNumber = matcher1.group(1);
				String chamber = matcher2.group(1) +" Daire";
				String docketNumber = matcher3.group(1);
				System.out.println("Decision Number: " + decisionNumber);
				System.out.println("Chamber: " + chamber);
				System.out.println("Docket Number: " + docketNumber);
				int judgmentStart = judgment.indexOf("Anahtar Kelimeler :");
				judgment= judgment.substring(judgmentStart);
				System.out.println("Judgment:" + judgment);
				System.out.println("------------------------------------------------");
			}


		}

	}
	public static void saveJudgement(String chamber){

	}
}

