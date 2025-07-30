package in.foodies.foodiesapi.repository;

import in.foodies.foodiesapi.entity.FoodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface FoodRepository extends MongoRepository<FoodEntity, String> {
}
