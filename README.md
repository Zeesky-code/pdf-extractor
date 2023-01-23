#README
This project is a Java program that extracts judgments and their metadata (chamber, docket no, and decision no) from a PDF document containing judgments from Danıştay (the High Court for administrative appeals) and stores the text and metadata in a database table. The database chosen for this project is SQLite or MySQL.

The program uses Apache PDFBox to crawl the document in sections, extract the html content from the url and then stores the metadata, url, and extracted content to a MySQL database.

## Getting Started
### Prerequisites
- Java 8 or later
- Maven
- MySQL

## Running the Program
1. Clone the repository to your local machine using:

```
git clone https://github.com/Zeesky-code/pdf-extractor.git
```
> **Warning**
> Before proceeding with the next step, add the `_.danistay.gov.tr.crt` certificate in the resources folder to your computer's security certificates to allow the program get information from the webiste without any error.
> Check out the accepted answer on https://stackoverflow.com/questions/21076179/pkix-path-building-failed-and-unable-to-find-valid-certification-path-to-requ


2.Add your database url, username, and password in a .env file in the root directory.
Example:
```
DB_URL = "jdbc:mysql://mydb"
USER = "testuser"
PASS = "testpassword"
```

3. Open a terminal and navigate to the root of the cloned repository.
Run the command `mvn clean install package` to build the project.

4. Execute the program using:
```
java -jar target\PDFExtractor.jar
```


