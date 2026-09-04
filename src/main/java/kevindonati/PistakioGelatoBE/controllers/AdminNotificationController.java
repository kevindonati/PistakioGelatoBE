package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.payloads.AdminNotificationResponse;
import kevindonati.PistakioGelatoBE.services.AdminNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Notifications", description = "Administrative notifications for orders and customers")
public class AdminNotificationController {
    private final AdminNotificationService adminNotificationService;

    public AdminNotificationController(AdminNotificationService adminNotificationService) {
        this.adminNotificationService = adminNotificationService;
    }

    @GetMapping
    @Operation(summary = "Get admin notifications", description = "Returns unread order and customer notifications")
    public ResponseEntity<AdminNotificationResponse> getNotifications() {
        return ResponseEntity.ok(adminNotificationService.getNotifications());
    }

    @PatchMapping("/orders/read")
    @Operation(summary = "Mark order notifications as read")
    public ResponseEntity<Void> markOrdersAsRead() {
        adminNotificationService.markOrdersAsRead();

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/customers/read")
    @Operation(summary = "Mark customer notifications as read")
    public ResponseEntity<Void> markCustomersAsRead() {
        adminNotificationService.markCustomersAsRead();

        return ResponseEntity.noContent().build();
    }
}