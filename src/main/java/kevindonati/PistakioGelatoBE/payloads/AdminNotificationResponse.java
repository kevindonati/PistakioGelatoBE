package kevindonati.PistakioGelatoBE.payloads;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.User;

import java.util.List;

public record AdminNotificationResponse(
        long ordersCount,
        long customersCount,
        List<Order> orders,
        List<User> customers
) {
}