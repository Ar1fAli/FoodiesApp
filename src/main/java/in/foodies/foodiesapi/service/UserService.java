package in.foodies.foodiesapi.service;

import in.foodies.foodiesapi.io.UserRequest;
import in.foodies.foodiesapi.io.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);


    String findByUserId();
}
