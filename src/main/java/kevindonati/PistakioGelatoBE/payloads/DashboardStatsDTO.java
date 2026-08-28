package kevindonati.PistakioGelatoBE.payloads;

public record DashboardStatsDTO(
        long totalOrders,
        long totalCustomers,
        double revenue,
        long pendingPayments,
        long preparingOrders,
        long shippedOrders,
        long deliveredOrders
) {
}