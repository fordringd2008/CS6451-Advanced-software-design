package com.hotel.hotel_loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hotel Loyalty System — entry point.
 * <p>
 * Stack: Spring Boot 4 (WebMVC + JMS/Artemis + MyBatis + H2 + JSP + springdoc).
 * Layered per the architecture document: controller → service → mapper (dao) → domain.
 *
 * @author wukewei
 * @since 1.0
 */
@SpringBootApplication
public class HotelLoyaltyApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelLoyaltyApplication.class, args);
	}

}
