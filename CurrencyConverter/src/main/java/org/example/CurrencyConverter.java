package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

public class CurrencyConverter {
    private static final HashMap<String, Double> exchangeRates = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("======= Real-Time Currency Converter =======");

        if (!fetchExchangeRates()) {
            System.out.println("✘ Failed to load exchange rates.");
            scanner.close();
            return;
        }

        while (true) {
            System.out.print("\nEnter base currency code (e.g., USD): ");
            String base = scanner.nextLine().toUpperCase();

            System.out.print("Enter target currency code (e.g., INR): ");
            String target = scanner.nextLine().toUpperCase();

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            if (!exchangeRates.containsKey(base) || !exchangeRates.containsKey(target)) {
                System.out.println("Invalid currency code(s). Try again.");
                continue;
            }

            double baseRate = exchangeRates.get(base);
            double targetRate = exchangeRates.get(target);
            double result = (targetRate / baseRate) * amount;

            System.out.printf("%.2f %s = %.2f %s%n", amount, base, result, target);

            System.out.print("Do you want to convert another? (yes/no): ");
            if (!scanner.nextLine().equalsIgnoreCase("yes")) break;
        }

        scanner.close();
    }

      // Fetch real-time exchange rates
    public static boolean fetchExchangeRates() {
        try {
            String apiUrl = "https://open.er-api.com/v6/latest/USD";  // Working public API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            // Check if 'rates' is present
            if (!json.has("rates")) {
                System.out.println("Error: 'rates' not found in response.");
                return false;
            }

            JSONObject rates = json.getJSONObject("rates");
            for (String key : rates.keySet()) {
                exchangeRates.put(key.toUpperCase(), rates.getDouble(key));
            }

            System.out.println("✔ Exchange rates loaded successfully.");
            return true;

        } catch (Exception e) {
            System.out.println("Error fetching rates: " + e.getMessage());
            return false;
 }
}
}
