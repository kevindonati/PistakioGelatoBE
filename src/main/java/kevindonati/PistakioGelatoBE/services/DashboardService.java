package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.payloads.DashboardStatsDTO;
import kevindonati.PistakioGelatoBE.payloads.DashboardStatsDTO.SalesPointDTO;
import kevindonati.PistakioGelatoBE.repositories.OrderRepository;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public DashboardStatsDTO getStats(String period, int offset) {

        LocalDateTime start;
        LocalDateTime end;

        switch (period.toUpperCase()) {

            case "DAY" -> {
                LocalDate date = LocalDate.now().plusDays(offset);

                start = LocalDateTime.of(
                        date,
                        LocalTime.MIN
                );

                end = LocalDateTime.of(
                        date,
                        LocalTime.MAX
                );
            }

            case "WEEK" -> {
                LocalDate today = LocalDate.now();

                LocalDate monday = today
                        .with(DayOfWeek.MONDAY)
                        .plusWeeks(offset);

                LocalDate sunday = monday.plusDays(6);

                start = LocalDateTime.of(
                        monday,
                        LocalTime.MIN
                );

                end = LocalDateTime.of(
                        sunday,
                        LocalTime.MAX
                );
            }

            case "MONTH" -> {
                YearMonth month = YearMonth.now()
                        .plusMonths(offset);

                start = LocalDateTime.of(
                        month.atDay(1),
                        LocalTime.MIN
                );

                end = LocalDateTime.of(
                        month.atEndOfMonth(),
                        LocalTime.MAX
                );
            }

            case "YEAR" -> {
                int year = LocalDate.now()
                        .getYear() + offset;

                start = LocalDateTime.of(
                        LocalDate.of(year, 1, 1),
                        LocalTime.MIN
                );

                end = LocalDateTime.of(
                        LocalDate.of(year, 12, 31),
                        LocalTime.MAX
                );
            }

            default -> throw new IllegalArgumentException(
                    "Invalid period. Use DAY, WEEK, MONTH or YEAR"
            );
        }

        /*
         * =========================================================
         * ORDINI
         * =========================================================
         */

        long totalOrders =
                orderRepository.countByCreatedAtBetween(
                        start,
                        end
                );

        long cartOrders =
                orderRepository
                        .countByOrderStatusAndCreatedAtBetween(
                                OrderStatus.CART,
                                start,
                                end
                        );

        totalOrders -= cartOrders;

        /*
         * =========================================================
         * CLIENTI
         * =========================================================
         */

        long totalCustomers =
                userRepository.count();

        long newCustomers =
                userRepository.countByCreatedAtBetween(
                        start,
                        end
                );

        /*
         * =========================================================
         * PAGAMENTI
         * =========================================================
         */

        List<Payment> completedPayments =
                paymentRepository
                        .findByStatusAndPaymentDateBetween(
                                PaymentStatus.COMPLETED,
                                start,
                                end
                        );

        double revenue =
                completedPayments.stream()
                        .mapToDouble(Payment::getAmount)
                        .sum();

        long completedOrders =
                completedPayments.size();

        double averageOrderValue =
                completedOrders > 0
                        ? revenue / completedOrders
                        : 0;

        /*
         * =========================================================
         * STATI ORDINI
         * =========================================================
         */

        long pendingPayments =
                orderRepository
                        .countByOrderStatusAndCreatedAtBetween(
                                OrderStatus.PENDING_PAYMENT,
                                start,
                                end
                        );

        long preparingOrders =
                orderRepository
                        .countByOrderStatusAndCreatedAtBetween(
                                OrderStatus.PREPARING,
                                start,
                                end
                        );

        long shippedOrders =
                orderRepository
                        .countByOrderStatusAndCreatedAtBetween(
                                OrderStatus.SHIPPED,
                                start,
                                end
                        );

        long deliveredOrders =
                orderRepository
                        .countByOrderStatusAndCreatedAtBetween(
                                OrderStatus.DELIVERED,
                                start,
                                end
                        );

        /*
         * =========================================================
         * GRAFICO
         * =========================================================
         */

        List<SalesPointDTO> salesChart =
                buildSalesChart(
                        completedPayments,
                        period,
                        start,
                        end
                );

        return new DashboardStatsDTO(
                totalOrders,
                totalCustomers,
                revenue,
                averageOrderValue,
                pendingPayments,
                preparingOrders,
                shippedOrders,
                deliveredOrders,
                newCustomers,
                salesChart
        );
    }

    private List<SalesPointDTO> buildSalesChart(
            List<Payment> payments,
            String period,
            LocalDateTime start,
            LocalDateTime end
    ) {

        Map<String, SalesData> data =
                new LinkedHashMap<>();

        if (period.equalsIgnoreCase("DAY")) {

            LocalDate date = start.toLocalDate();

            data.put(
                    date.toString(),
                    new SalesData()
            );

        } else if (period.equalsIgnoreCase("WEEK")) {

            LocalDate monday =
                    start.toLocalDate();

            for (int i = 0; i < 7; i++) {

                LocalDate date =
                        monday.plusDays(i);

                data.put(
                        date.toString(),
                        new SalesData()
                );
            }

        } else if (period.equalsIgnoreCase("MONTH")) {

            YearMonth month =
                    YearMonth.from(start);

            for (
                    int day = 1;
                    day <= month.lengthOfMonth();
                    day++
            ) {

                LocalDate date =
                        month.atDay(day);

                data.put(
                        date.toString(),
                        new SalesData()
                );
            }

        } else if (period.equalsIgnoreCase("YEAR")) {

            int year =
                    start.getYear();

            for (int month = 1; month <= 12; month++) {

                String key =
                        String.format(
                                "%d-%02d",
                                year,
                                month
                        );

                data.put(
                        key,
                        new SalesData()
                );
            }
        }

        /*
         * Inserimento pagamenti nel grafico
         */

        for (Payment payment : payments) {

            LocalDateTime paymentDate =
                    payment.getPaymentDate();

            String key;

            if (period.equalsIgnoreCase("YEAR")) {

                key = String.format(
                        "%d-%02d",
                        paymentDate.getYear(),
                        paymentDate.getMonthValue()
                );

            } else {

                key = paymentDate
                        .toLocalDate()
                        .toString();
            }

            SalesData salesData =
                    data.get(key);

            if (salesData != null) {

                salesData.revenue +=
                        payment.getAmount();

                salesData.orders++;
            }
        }

        List<SalesPointDTO> result =
                new ArrayList<>();

        for (
                Map.Entry<String, SalesData> entry :
                data.entrySet()
        ) {

            result.add(
                    new SalesPointDTO(
                            entry.getKey(),
                            entry.getValue().revenue,
                            entry.getValue().orders
                    )
            );
        }

        return result;
    }

    private static class SalesData {

        private double revenue = 0;

        private long orders = 0;
    }
}