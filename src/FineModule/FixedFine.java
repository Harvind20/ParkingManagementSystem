package FineModule;

// fixed fine strategy which is the same amount regardless of overstayed duration
public class FixedFine implements FineScheme {

    @Override
    public double calculateFine(int hoursOverstayed) {
        // ignores hours and returns a constant penalty
        return 50;
    }
}