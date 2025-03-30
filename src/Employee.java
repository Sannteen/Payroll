import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Employee implements Payable {
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;

    public Employee(String firstName, String lastName, String socialSecurityNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    // Getters and setters omitted for brevity

    @Override
    public void writeToFile() throws IOException {
        try (FileWriter writer = new FileWriter("paystub.txt", true)) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            writer.write(String.format("Name: %s %s%n", firstName, lastName));
            writer.write(String.format("Payment Amount: $%.2f%n", getPaymentAmount()));
            writer.write(String.format("Date: %s%n%n", date));
        }
    }
}