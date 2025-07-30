package in.foodies.foodiesapi.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.foodies.foodiesapi.controller.UserController;
import in.foodies.foodiesapi.entity.CartEntity;
import in.foodies.foodiesapi.entity.FoodEntity;
import in.foodies.foodiesapi.entity.OrderEntity;
import in.foodies.foodiesapi.entity.UserEntity;
import in.foodies.foodiesapi.io.OrderRequest;
import in.foodies.foodiesapi.io.OrderResponse;
import in.foodies.foodiesapi.repository.CartRespository;
import in.foodies.foodiesapi.repository.FoodRepository;
import in.foodies.foodiesapi.repository.OrderRepository;
import in.foodies.foodiesapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.json.XMLTokener.entity;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRespository cartRepository;
    @Autowired
    private UserRepository userController;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserService userService;
@Autowired
private CartRespository cartRespository;
    @Value("${razorpay_key}")
    private String RAZORPAY_KEY;
    @Value("${razorpay_secret}")
    private String RAZORPAY_SECRET;

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) throws RazorpayException {
        System.out.println(request);
        OrderEntity newOrder = convertToEntity(request);
        newOrder = orderRepository.save(newOrder);


        // Create razorpay payment order

        RazorpayClient razorpayClient = new  RazorpayClient(RAZORPAY_KEY,RAZORPAY_SECRET);
        System.out.println(razorpayClient);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",newOrder.getAmount()*100);
        orderRequest.put("currency","INR");
        orderRequest.put("payment_capture",1);


        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        newOrder.setRazorpayOrderId(razorpayOrder.get("id"));
        String loggedInUserId = userService.findByUserId();
        newOrder.setUserId(loggedInUserId);
        newOrder = orderRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {

        String razorpayOrderId = paymentData.get("razorpay_order_id");
        OrderEntity existingOrder = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setPaymentStatus(status);
        existingOrder.setRazorpaySignature(paymentData.get("razorpay_signature"));
        existingOrder.setRazorpayPaymentId(paymentData.get("razorpay_payment_id"));
        orderRepository.save(existingOrder);
        if("paid".equalsIgnoreCase(status)) {
            cartRespository.deleteByUserId(existingOrder.getUserId());
        }


    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
       List<OrderEntity> list  = orderRepository.findByUserId(loggedInUserId);
       return list.stream().map(entity ->  convertToResponse(entity)).collect(Collectors.toList());

    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);

    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        System.out.println("getOrdersOfAllUsers");
        List<OrderEntity> list;
//        System.out.println(list);
     try{
         List<UserEntity> cart =  userController.findAll();
         List<CartEntity> cart2 = cartRepository.findAll();

         List<FoodEntity> cart3 = foodRepository.findAll();
         System.out.println(cart);
         System.out.println(cart2);
         System.out.println(cart3);
         list = orderRepository.findAll();
     } catch (Exception e) {
         System.out.println(e.getMessage());
         throw new RuntimeException(e);
     };
        System.out.println();
       return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {

        OrderEntity entity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
         entity.setOrderStatus(status);
         orderRepository.save(entity);

    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userId(newOrder.getUserId())
                .razorpayOrderId(newOrder.getRazorpayOrderId())
                .orderStatus(newOrder.getOrderStatus())
                .paymentStatus(newOrder.getPaymentStatus())
                .email(newOrder.getEmail())
                .phoneNumber(newOrder.getPhoneNumber())
                .orderItems(newOrder.getOrderedItems())
                .build();
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()

                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderedItems(request.getOrderedItem())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}
