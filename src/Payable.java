
import java.io.IOException;

public interface Payable {
    double getPaymentAmount();
    void writeToFile() throws IOException;
}