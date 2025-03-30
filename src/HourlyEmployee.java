
public class HourlyEmployee extends Employee {
    private double wage;
    private double hours;

    public HourlyEmployee(String firstName, String lastName,
                          String ssn, double wage, double hours) {
        super(firstName, lastName, ssn);
        this.wage = wage;
        this.hours = hours;
    }

    @Override
    public double getPaymentAmount() {
        return wage * hours;
    }
}