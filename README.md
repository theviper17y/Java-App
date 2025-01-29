# Cross Site Scripting (XSS) Scanner

## Overview

The Cross Site Scripting (XSS) Scanner is a Java-based application designed to identify potential XSS vulnerabilities in web applications. It scans specified URLs for common XSS payloads and logs any vulnerabilities found.

## Prerequisites

Before using the XSS Scanner, ensure you have the following:

- **Apache Netbeans**: We will be testing here.
- **Java Development Kit (JDK)**: Make sure you have JDK 8 or higher installed on your machine.
- **Mariadb Database**: A running instance of MySQL or MariaDB to store vulnerability logs.
- **Jsoup Library**: This library is used for parsing HTML and making HTTP requests , you can use the jsoup jar file.
- **MariaDB JDBC Driver**: Required for database connectivity.

## Installation

1. **Clone the Repository**: Clone the repository containing the XSS Scanner code to your local machine.

   ```bash
   git clone https://github.com/theviper17y/Java-App.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
   cd Java-App
   ```

3. **Add/Install Dependencies**: In Netbeans Add New Java with Ant project:
   - In **Projects** -> New Project -> Java with Ant -> Java Application
   - Right click on **Libraries** -> Add Jar file -> choose mariadb connector and jsoup jar.
   - Copy **XSSScannerApp.java** to your Source Packages -> Name of packege.
   - Copy **xss.jpg** to the project dirctory.

6. **Run the Application**:
  - Clean and build
  - Run the application

## Usage

1. **Launch the Application**: Run the application following steps above. The GUI will open in full-screen mode.

2. **Enter Target URL**: In the "Target URL" field, enter the URL of the web application you want to scan for XSS vulnerabilities. Ensure the URL starts with `http://` or `https://`.

3. **Start Scanning**: Click the "Scan for XSS" button to begin the scanning process. The application will send various XSS payloads to the specified URL and check for vulnerabilities.

4. **View Results**: The results will be displayed in the table below, showing any vulnerabilities found along with the payload used. The status area will also provide updates during the scanning process.

5. **Database Logging**: All detected vulnerabilities will be logged into the specified MySQL database for future reference.

## Important Notes

- **Ethical Use**: Ensure you have permission to scan the target website for vulnerabilities. Unauthorized scanning can be illegal and unethical.
- **Payloads**: The application uses a predefined set of XSS payloads. You can modify or extend this list as needed.
- **Error Handling**: If the application encounters an error (e.g., invalid URL, connection issues), it will display an appropriate message.

## Conclusion

The XSS Scanner is a powerful tool for identifying potential XSS vulnerabilities in web applications. By following the steps outlined above, you can effectively use the scanner to enhance the security of your web applications.

## ScreenShots

   [Xss scanner screenshot](https://github.com/theviper17y/Java-App/blob/master/xss_scanner_screenshot.jpg)
