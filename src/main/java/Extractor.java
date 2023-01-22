import com.itextpdf.kernel.pdf.PdfDocument;
import static com.itextpdf.kernel.pdf.PdfName.BaseFont;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Extractor {
	public static void main(String[] args) {
		try {
			URL pdfUrl = new URL("https://www.danistay.gov.tr/assets/pdf/yayinlar/dergi/15_03_2019_015742.pdf");
			InputStream in = pdfUrl.openStream();
			BufferedInputStream bf = new BufferedInputStream(in);
			PDDocument doc = PDDocument.load(bf);

			PDFTextStripper pdfStripper = new PDFTextStripper();
			int i = 5;
			pdfStripper.setStartPage(i);
			pdfStripper.setEndPage(i);

			//Retrieving text from PDF document
			String text = pdfStripper.getText(doc);
			System.out.println(text);




		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

