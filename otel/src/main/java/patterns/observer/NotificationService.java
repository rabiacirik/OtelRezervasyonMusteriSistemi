package patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private List<Observer> customers = new ArrayList<>();

    public void subscribe(Observer customer) {
        customers.add(customer);
    }

    public void notifyAll(String message) {
        for (Observer customer : customers) {
            customer.update(message);
        }
    }
}