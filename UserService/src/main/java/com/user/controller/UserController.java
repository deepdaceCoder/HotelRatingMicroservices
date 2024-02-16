package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.entities.User;
import com.user.services.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	private int retryCount;
	
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User user1 = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user1);
	}
	
	@GetMapping("/{userId}")
//	@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
//	@Retry(name = "ratingHotelRetry", fallbackMethod = "ratingHotelFallback")
	@RateLimiter(name = "ratingHotelLimiter", fallbackMethod = "ratingHotelRateLimitFallback")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId) {
		System.out.println("Retry times - " + (++retryCount));
		User user = userService.getUser(userId);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping
	public ResponseEntity<List<User>> getAllUser() {
		List<User> allUser = userService.getAllUser();
		return ResponseEntity.ok(allUser);
	}
	
	// Creating rating hotel fallback for circuit breaker
	public ResponseEntity<User> ratingHotelFallback(String userId, Exception e) {
		System.out.println("Breaker is executed because service is down!! Exception - " + e.getMessage());
		User user = User.builder().name("Dummy").email("dummy@email").about("Dummy created!!").build();
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(user);
	}
	
	public ResponseEntity<User> ratingHotelRateLimitFallback(String userId, Exception e) {
		System.out.println("Breaker is executed because service is down!! Exception - " + e.getMessage());
		User user = User.builder().name("Limit Exceeded").email("limitexceeded@email").about("Limit Exceeded!!").build();
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(user);
	}
	
}
