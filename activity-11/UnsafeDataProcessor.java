import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UnsafeDataProcessor {

    public static void main(String[] args) {
        String fileName = "data.txt";

        try {
            double total = processFile(fileName);
            System.out.println("Financial calculation completed successfully.");
            System.out.println("Total amount: " + total);
        } catch (IOException e) {
            // This catch block handles checked exceptions related to file reading,
            // such as when the file does not exist or cannot be opened.
            System.out.println("Error: The file could not be read.");
            System.out.println("Details: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // This catch block handles invalid data found inside the file,
            // such as text values that are not valid numbers.
            System.out.println("Error: Invalid data format.");
            System.out.println("Details: " + e.getMessage());
        }
    }

    public static double processFile(String fileName) throws IOException {
        double total = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                try {
                    double value = Double.parseDouble(line.trim());
                    total += value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Line " + lineNumber + " is not a valid number: " + line
                    );
                }

                lineNumber++;
            }
        }

        return total;
    }
}