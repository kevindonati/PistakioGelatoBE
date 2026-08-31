package kevindonati.PistakioGelatoBE.payloads;

import java.util.List;

public record DashboardStatsDTO(
        long totalOrders,
        long totalCustomers,
        double revenue,
        double averageOrderValue,
        long pendingPayments,
        long preparingOrders,
        long shippedOrders,
        long deliveredOrders,
        long newCustomers,
        List<SalesPointDTO> salesChart
) {
    public record SalesPointDTO(
            String date,
            double revenue,
            long orders
    ) {
    }
}