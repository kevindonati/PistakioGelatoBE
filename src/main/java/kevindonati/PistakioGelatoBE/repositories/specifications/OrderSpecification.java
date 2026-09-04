package kevindonati.PistakioGelatoBE.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderSpecification {

    public static Specification<Order> hasId(UUID id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Order> hasCustomer(String customer) {
        return (root, query, cb) -> {
            if (customer == null || customer.isBlank()) {
                return null;
            }

            Join<Order, User> user = root.join("user", JoinType.INNER);

            String search = "%" + customer.trim().toLowerCase() + "%";

            return cb.or(cb.like(cb.lower(user.get("name")), search),
                    cb.like(cb.lower(user.get("surname")), search),
                    cb.like(cb.lower(user.get("email")), search)
            );
        };
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(
                        root.get("orderStatus"),
                        status
                );
    }

    public static Specification<Order> totalGreaterThanOrEqual(Double minTotal) {
        return (root, query, cb) ->
                minTotal == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("total"),
                        minTotal
                );
    }

    public static Specification<Order> totalLessThanOrEqual(Double maxTotal) {
        return (root, query, cb) ->
                maxTotal == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("total"),
                        maxTotal
                );
    }

    public static Specification<Order> createdAtGreaterThanOrEqual(LocalDate dateFrom) {
        return (root, query, cb) -> {

            if (dateFrom == null) {
                return null;
            }

            LocalDateTime start = dateFrom.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
        };
    }

    public static Specification<Order> createdAtLessThan(LocalDate dateTo) {
        return (root, query, cb) -> {

            if (dateTo == null) {
                return null;
            }

            LocalDateTime end = dateTo.plusDays(1).atStartOfDay();
            return cb.lessThan(root.get("createdAt"), end);
        };
    }

    public static Specification<Order> notCart() {
        return (root, query, cb) ->
                cb.notEqual(root.get("orderStatus"), OrderStatus.CART);
    }

    public static Specification<Order> hasUserId(UUID userId) {
        return (root, query, cb) ->
                userId == null
                        ? null
                        : cb.equal(
                        root.get("user").get("id"),
                        userId
                );
    }
}