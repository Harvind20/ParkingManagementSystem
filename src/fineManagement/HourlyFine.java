package fineManagement;

public class HourlyFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        return hoursOverstayed * 20;
    }
}