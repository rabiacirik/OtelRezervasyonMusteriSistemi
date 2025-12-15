package patterns.strategy;

// Fiyat hesaplama stratejisi için ortak arayüz
public interface PricingStrategy {
    double calculateTotal(double pricePerNight, long days);
}