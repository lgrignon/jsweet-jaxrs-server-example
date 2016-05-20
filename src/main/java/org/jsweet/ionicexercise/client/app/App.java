package org.jsweet.ionicexercise.client;

import static def.knockout.Globals.ko;
import static jsweet.dom.Globals.document;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.object;

import def.jquery_datatables.datatables.LanguageSettings;
import def.jquery_datatables.datatables.Settings;
import def.knockout.KnockoutObservable;
import jsweet.dom.Event;

public class App {

	private final static Logger logger = Logger.get("App");

	public static KnockoutObservable<String> activePage = ko.observable("coffeeshops");

	static CoffeeShopController coffeeShopController;

	public static void setActivePage(Event event, String name) {
		logger.info("set active page: " + name);
		activePage.apply(name);
	}

	public static boolean isActivePage(String name) {
		logger.info("is active page: " + name + " (active: " + activePage.apply() + ")");
		return activePage.apply() == name;
	}

	public static void main(String[] args) {
		logger.info("starting app");

//		Server.instance().setWebRoot("http://localhost:8080/ionic-exercise-server/api");
		Server.instance().setWebRoot("http://jsweet.org/ionic-exercise-server/api");
		Server.instance().addPersistentHeader("Content-Type", "application/json");
		
		// TODO : set ping service path to enable connectivity listening
		Server.instance().setPingPath(null);

		ko.punches.enableAll();

		coffeeShopController = new CoffeeShopController();
		
		ko.applyBindings(coffeeShopController, document.getElementById("navbar"));
		ko.applyBindings(coffeeShopController, document.getElementById("coffeeshops"));
	}

	public static Settings getDefaultDataTableConfiguration() {
		Settings settings = new Settings() {
			{
				language = new LanguageSettings() {
					{
						search = "Search:";
					}
				};
				$set("select", true);
			}
		};
		return settings;
	}

}
