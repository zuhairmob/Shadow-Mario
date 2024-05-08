import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Class that contains methods to read a CSV file and a properties file.
 * You may edit this as you wish.
 */
public class IOUtils {

    /***
     * Method that reads a CSV file and return a 2D String array
     * @param csvFile: the path to the CSV file
     * @return 2D String array
     */
    public static String[][] readCsv(String csvFile) {

        // Use first try-catch block to read the number of lines in the CSV
        int rowCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                rowCount++;  // Count the number of lines in the CSV
            }

        } catch (IOException e) {

            System.err.println("Error reading world file: " + e.getMessage());
            return null;

        }


        // Use second try-catch block to save the contents into a 2D array
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){

            String line;
            String[][] data = new String[rowCount][3];  // Create a 2D array of size #rows x 3
            int rowIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");  // Split lines with the comma delimiter, so they can be saved as an array
                data[rowIndex] = values;
                rowIndex++;
            }
            return data;

        } catch (IOException e) {
            System.err.println("Error reading world file: " + e.getMessage());
            return null;
        }

    }

    /***
     * Method that reads a properties file and return a Properties object
     * @param configFile: the path to the properties file
     * @return Properties object
     */
    public static Properties readPropertiesFile(String configFile) {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configFile));
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return appProps;
    }
}