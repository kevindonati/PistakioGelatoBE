package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.OrderItem;
import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.OrderItemDTO;
import kevindonati.PistakioGelatoBE.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FlavorService flavorService;
    @Autowired
    private TubService tubService;


    private void recalculateOrderTotal(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getQuantity() * item.getUnitPrice();
        }
        total += order.getShippingCost();
        order.setTotal(total);
        orderService.saveOrder(order);
    }

    public OrderItem save(OrderItemDTO payload) {
        Order foundedOrder = orderService.findById(payload.order());
        if (foundedOrder.getOrderStatus() != OrderStatus.CART) {
            throw new BadRequestException("You can only modify a cart");
        }

        Flavor foundedFlavor = flavorService.findById(payload.flavor());
        Tub foundedTub = tubService.findById(payload.tub());

        if (!foundedTub.isAvailable()) {
            throw new BadRequestException("This tub is not available");
        }

        if (!foundedFlavor.isAvailable()) {
            throw new BadRequestException("This flavor is not available");
        }

        Optional<OrderItem> foundedOrderItem = orderItemRepository.findByOrderAndFlavorAndTub(foundedOrder, foundedFlavor, foundedTub);

        if (foundedOrderItem.isPresent()) {
            OrderItem item = foundedOrderItem.get();
            int newQuantity = item.getQuantity() + payload.quantity();

            if (newQuantity > foundedFlavor.getStockPortions()) {
                throw new BadRequestException("Not enough stock");
            }

            item.setQuantity(newQuantity);
            OrderItem saved = orderItemRepository.save(item);
            recalculateOrderTotal(foundedOrder);
            return saved;
        }

        if (payload.quantity() > foundedFlavor.getStockPortions()) {
            throw new BadRequestException("Not enough stock");
        }

        double tubPrice = foundedTub.getPrice();

        OrderItem newOrderItem = new OrderItem(
                payload.quantity(),
                tubPrice,
                foundedOrder,
                foundedFlavor,
                foundedTub
        );

        OrderItem saved = orderItemRepository.save(newOrderItem);
        recalculateOrderTotal(foundedOrder);
        return saved;
    }

    public Page<OrderItem> findAll(int page, int size, String sortBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return orderItemRepository.findAll(pageable);
    }

    public OrderItem findById(UUID id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new NotFoundException("OrderItem with id " + id + " not found"));
    }

    public OrderItem findByIdAndUpdate(UUID id, OrderItemDTO payload) {
        OrderItem foundedOrderItem = findById(id);
        if (foundedOrderItem.getOrder().getOrderStatus() != OrderStatus.CART) {
            throw new BadRequestException("You can only modify a cart");
        }

        if (payload.quantity() > foundedOrderItem.getFlavor().getStockPortions()) {
            throw new BadRequestException("Not enough stock");
        }

        foundedOrderItem.setQuantity(payload.quantity());
        OrderItem saved = orderItemRepository.save(foundedOrderItem);
        recalculateOrderTotal(foundedOrderItem.getOrder());
        return saved;
    }

    public void findByIdAndDelete(UUID id) {
        OrderItem foundedOrderItem = this.findById(id);
        if (foundedOrderItem.getOrder().getOrderStatus() != OrderStatus.CART) {
            throw new BadRequestException("You can only modify a cart");
        }

        Order order = foundedOrderItem.getOrder();
        orderItemRepository.delete(foundedOrderItem);
        recalculateOrderTotal(order);
    }
}
