package org.jsweet.ionicexercise.webapi;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsweet.ionicexercise.Logger;
import org.jsweet.ionicexercise.client.model.AddCoffeeShopParameters;
import org.jsweet.ionicexercise.client.model.CoffeeShopDetailsDto;
import org.jsweet.ionicexercise.client.model.CoffeeShopDto;
import org.jsweet.ionicexercise.client.model.RateCoffeeShopParameters;

@Path("/coffeeshop")
@Singleton
public class CoffeeShopApi {

	private static final Logger logger = Logger.getLogger(CoffeeShopApi.class);

	private final static Map<String, CoffeeShopDto> coffeeShopsById = new HashMap<>();
	private final static Map<String, Integer> coffeeShopsRates = new HashMap<>();

	private static int lastId = 0;

	private static void addCoffeeShop(CoffeeShopDto coffeeShop) {

		Integer id = ++lastId;
		coffeeShop.id = id.toString();

		coffeeShopsById.put(coffeeShop.id, coffeeShop);
	}

	static {

		addCoffeeShop(new CoffeeShopDto() {
			{
				name = "Le Calumet";
				address = "30 Rue Notre Dame des Champs, 75006 Paris";
			}
		});

		addCoffeeShop(new CoffeeShopDto() {
			{
				name = "La Scala";
				address = "10 Rue du Général Leclerc, 92130 Issy-les-Moulineaux";
			}
		});

		addCoffeeShop(new CoffeeShopDto() {
			{
				name = "StarBucks ";
				address = "21 Rue Bréa, 75006 Paris";
			}
		});

	}

	@GET
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public CoffeeShopDto[] list() {
		logger.info("/coffeeshop/list - coffeeShopsById=" + coffeeShopsById);

		return coffeeShopsById.values().toArray(new CoffeeShopDto[0]);
	}

	@GET
	@Path("getDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public CoffeeShopDetailsDto getDetails(@QueryParam("id") String id) {
		logger.info("/coffeeshop/getDetails - id=" + id);

		CoffeeShopDto coffeeShopDto = coffeeShopsById.get(id);
		Integer coffeeRate = coffeeShopsRates.get(id);

		return new CoffeeShopDetailsDto() {
			{
				id = coffeeShopDto.id;
				name = coffeeShopDto.name;
				address = coffeeShopDto.address;
				rate = coffeeRate == null ? 0 : coffeeRate;
			}
		};
	}

	@POST
	@Path("add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response add(AddCoffeeShopParameters parameters) {
		logger.info("/coffeeshop/add");

		addCoffeeShop(new CoffeeShopDto() {
			{
				name = parameters.name;
				address = parameters.address;
			}
		});
		return Response.ok().build();
	}

	@POST
	@Path("rate")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response rate(RateCoffeeShopParameters parameters) {
		logger.info("/coffeeshop/rate");

		coffeeShopsRates.put(parameters.id, parameters.rate);

		return Response.ok().build();
	}
}
