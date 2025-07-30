package in.foodies.foodiesapi.controller;

import in.foodies.foodiesapi.io.CartRequest;
import in.foodies.foodiesapi.io.CartResponse;
import in.foodies.foodiesapi.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
@CrossOrigin("*")
public class CartController {

    private final CartService cartservice;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request){
        String foodId = request.getFoodId();
        if(foodId == null || foodId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Food Id not found");
        }

      return cartservice.addToCart(request);
    }

    @GetMapping
    public CartResponse getCart(){
        return cartservice.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(){
        cartservice.clearCart();

    }
    @PostMapping("/remove")
    public CartResponse removeFromCart(@RequestBody CartRequest request){
        String foodId = request.getFoodId();
//        System.out.println(request);
        if(foodId == null || foodId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Food Id not  is found");
        }
        return cartservice.removeFromCart(request);

    }
}
