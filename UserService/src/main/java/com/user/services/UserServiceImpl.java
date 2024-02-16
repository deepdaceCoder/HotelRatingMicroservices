package com.user.services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.user.dao.UserRepository;
import com.user.entities.Hotel;
import com.user.entities.Rating;
import com.user.entities.User;
import com.user.external.HotelService;
import com.user.services.exceptions.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HotelService hotelService;

	@Override
	public User saveUser(User user) {
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public User getUser(String userId) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found!!"));

		Rating[] ratingOfUser = restTemplate.getForObject("http://APIGATEWAY/ratings/user/" + user.getUserId(),
				Rating[].class);

		List<Rating> ratingListTemp = Arrays.stream(ratingOfUser).toList();
		List<Rating> ratingList = ratingListTemp.stream().map(rating -> {

//			ResponseEntity<Hotel> forEntity = restTemplate
//					.getForEntity("http://HOTELSERVICE/hotels/" + rating.getHotelId(), Hotel.class);
//			Hotel hotel = forEntity.getBody();
			
			Hotel hotel = hotelService.getHotelWithId(rating.getHotelId());
			rating.setHotel(hotel);
			return rating;

		}).collect(Collectors.toList());

		user.setRatings(ratingList);
		return user;
	}

}
