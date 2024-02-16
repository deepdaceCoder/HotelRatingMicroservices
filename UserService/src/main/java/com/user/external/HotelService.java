package com.user.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.user.entities.Hotel;

@FeignClient(name= "APIGATEWAY")
public interface HotelService {

	@GetMapping("/hotels/{hotelId}")
	public Hotel getHotelWithId(@PathVariable String hotelId);
	
}
