package FineModule;

// interface representing a fine calculation strategy
// different implementations being fixed, hourly and progressive fines provide their own logic
public interface FineScheme {
    double calculateFine(int hoursOverstayed);
}