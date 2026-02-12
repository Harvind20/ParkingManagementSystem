package FineModule;

public class FixedFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        return 50;
    }
}