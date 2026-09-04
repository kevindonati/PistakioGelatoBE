package kevindonati.PistakioGelatoBE.payloads;

import jakarta.validation.constraints.NotNull;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;

import java.util.UUID;

public record OrderDTO(
        @NotNull(message = "The order status is mandatory")
        OrderStatus orderStatus,

        @NotNull(message = "The shippiing cost is mandatory")
        double shippingCost,

        @NotNull(message = "The total is mandatory")
        double total,

        String notes,

        @NotNull(message = "The user is mandatory")
        UUID user,

        @NotNull(message = "The address is mandatory")
        UUID address
) {
}
