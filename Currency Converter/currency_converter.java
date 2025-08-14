import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class currency_converter {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Supported currencies include: USD, INR, EUR, GBP, JPY, CAD, AUD, CNY, CHF, AED, etc.");
        System.out.println("You must enter valid 3-letter currency codes (e.g., USD, INR)\n");

        while (true) {
            System.out.print("Enter the source currency code: ");
            String from = in.next().trim().toUpperCase();

            System.out.print("Enter the target currency code: ");
            String to = in.next().trim().toUpperCase();

            double amount = 0.0;
            System.out.print("Enter amount: ");
            try {
                amount = in.nextDouble();
                if (amount < 0) {
                    System.out.println("Amount cannot be negative. Try again.\n");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.\n");
                in.nextLine(); // Clear the scanner buffer
                continue;
            }

            double rate = getConversionRate(from, to);
            if (rate == -1) {
                System.out.println("Conversion failed. Please check the currency codes.\n");
            } else {
                double result = amount * rate;
                System.out.printf(" %.2f %s = %.2f %s%n", amount, from, result, to);
            }

            System.out.print("\nDo you want to convert again? (Y/N): ");
            String choice = in.next();
            if (!choice.equalsIgnoreCase("Y")) {
                System.out.println("Thank you for using the Currency Converter!");
                break;
            }
            System.out.println();
        }

        in.close();
    }

    // Fetch conversion rate using open.er-api.com
    public static double getConversionRate(String from, String to) {
        try {
            String urlStr = "https://open.er-api.com/v6/latest/" + from;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject json = new JSONObject(responseBuilder.toString());

            if (json.getString("result").equals("success")) {
                JSONObject rates = json.getJSONObject("rates");
                if (rates.has(to)) {
                    return rates.getDouble(to);
                } else {
                    System.out.println("Target currency not found.");
                    return -1;
                }
            } else {
                System.out.println("API returned failure status.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }
}
