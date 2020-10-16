package com.moviecatalogueservice.resources;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import com.moviecatalogueservice.model.CatalogueItem;
import com.moviecatalogueservice.model.Movie;
import com.moviecatalogueservice.model.UserRating;

@RestController
@RequestMapping("/catalogue")
public class MovieCatalogueResource {

	@Autowired
	private RestTemplate restTemplate;

	// asynchronous call alternative of RestTemplate
	@Autowired
	private WebClient.Builder webClientBuilder;

	@RequestMapping("/{userId}")
	public List<CatalogueItem> getCatalogue(@PathVariable String userId) {

		UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratings/users/" + userId,UserRating.class);

		return ratings.getUserRating().stream().map(rating -> {

			// for each movie id call movie info & get all details
			
			// use Eureka to get the Service discovery  
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

			// put all together
			return new CatalogueItem(movie.getName(), "Test", rating.getRating());

		}).collect(Collectors.toList());

	}
}

//using webclient	to call REst API asynchronously	
/*
 * Movie movie=webClientBuilder.build() //http get method for Movie info API
 * .get() .uri("http://localhost:8081/movies/"+ rating.getMovieId()) .retrieve()
 * // convert the received data to a Movie class type, Mono is a promise()
 * .bodyToMono(Movie.class) // block means converting asynchronous to synchronous .block();
 */
