import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Invoice implements Payable {
    private String partNumber;
    private String partDescription;
    private int quantity;
    private double pricePerItem;

    public Invoice(String partNumber, String partDescription,
                   int quantity, double pricePerItem) {
        this.partNumber = partNumber;
        this.partDescription = partDescription;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    @Override
    public double getPaymentAmount() {
        return quantity * pricePerItem;
    }

    @Override
    public void writeToFile() throws IOException {
        try (FileWriter writer = new FileWriter("paystub.txt", true)) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            writer.write(String.format("Contractor: %s%n", partDescription));
            writer.write(String.format("Payment Amount: $%.2f%n", getPaymentAmount()));
            writer.write(String.format("Date: %s%n%n", date));
        }
    }
}