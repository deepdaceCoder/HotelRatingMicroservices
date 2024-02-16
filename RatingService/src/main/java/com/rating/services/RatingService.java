package com.rating.services;

import java.util.List;

import com.rating.entities.Rating;

public interface RatingService {

	public Rating addRating(Rating rating);
	
	public List<Rating> getAllRatings();
	
	public List<Rating> getAllRatingsByUserId(String userId);
	
	public List<Rating> getAllRatingsByHotelId(String hotelId);
	
}
