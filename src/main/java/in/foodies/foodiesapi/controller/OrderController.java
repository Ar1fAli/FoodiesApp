package in.foodies.foodiesapi.controller;

import com.razorpay.RazorpayException;
import in.foodies.foodiesapi.io.OrderRequest;
import in.foodies.foodiesapi.io.OrderResponse;
import in.foodies.foodiesapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request) throws RazorpayException {
        OrderResponse response = orderService.createOrderWithPayment(request);
        return response;
    }
    @PostMapping("/verify")
    public void verifyPayment(@RequestBody Map<String, String> paymentData) throws RazorpayException {
        orderService.verifyPayment(paymentData,"Paid");
    }

    @GetMapping
    public List<OrderResponse> getOrder(){
       return orderService.getUserOrders();

    }

   @DeleteMapping("/{orderId}")
   @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void deleteOrder(@PathVariable String orderId){
       orderService.removeOrder(orderId);

    }

    //admin pannel
    @GetMapping("/all")
    public List<OrderResponse> getOrdersOfAllusers(){
        System.out.println("getOrdersOfAllusers");
        return orderService.getOrdersOfAllUsers();
    }


    //admin pannel
    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable String orderId,@RequestParam String status){
    orderService.updateOrderStatus(orderId,status);
    }
}
