package org.jsweet.ionicexercise.client;

import static def.jquery.Globals.$;
import static def.knockout.Globals.ko;
import static jsweet.dom.Globals.console;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.string;

import org.jsweet.ionicexercise.client.model.AddCoffeeShopParameters;
import org.jsweet.ionicexercise.client.model.CoffeeShopDto;

import def.jquery_datatables.datatables.DataTable;
import def.knockout.KnockoutObservable;
import def.knockout.KnockoutObservableArray;
import jsweet.dom.Event;
import jsweet.dom.File;
import jsweet.dom.FileList;
import jsweet.dom.FileReader;
import jsweet.dom.HTMLInputElement;
import jsweet.dom.Node;
import jsweet.lang.Array;
import jsweet.util.StringTypes;

class CoffeeShopController {

	protected final static Logger logger = Logger.get("CoffeeShopController");

	KnockoutObservableArray<CoffeeShopDto> datas = ko.observableArray((CoffeeShopDto[]) new Object[0]);
	int selectedDataIndex = -1;
	KnockoutObservable<CoffeeShopDto> selectedData = ko.<CoffeeShopDto> observable();
	DataTable dataTable = null;
	String dataTableSelector = null;
	String modalSelector = null;

	KnockoutObservable<Boolean> isOpen = ko.observable(false);

	public CoffeeShopController() {
		logger.info("new controller");
		this.dataTableSelector = "#coffeeshopsDT";
		this.modalSelector = "#modalcoffeeshops";
		init();
		fetchDatas();

		$(modalSelector).on("show.bs.modal", (event, object) -> {
			console.info("cloning selected coffee: " + event.relatedTarget.id);
			switch (event.relatedTarget.getAttribute("data-action")) {
			case "modifyData":
				selectedData.apply(cloneData(datas.apply()[selectedDataIndex]));
				break;
			case "createData":
				selectedData.apply(createEmptyData());
				break;
			default:
				System.out.println("ERROR: action not handled" + event.relatedTarget.getAttribute("data-action"));
			}
			return event;
		});

		$(modalSelector).on("shown.bs.modal", (event, object) -> {
			((Runnable) $(modalSelector + " :input").$get("inputmask")).run();
			return event;
		});

		selectedData.subscribe(this::onDataSelected);
	}
	
	protected void onDataSelected(CoffeeShopDto data) {
	}
	
	protected void init() {
	}

	
	protected void fetchDatas() {
		logger.info("fetching cares");
		Server.instance().<CoffeeShopDto[]>get("/coffeeshop/list", null, (CoffeeShopDto[] coffeeShops) -> {
			logger.info("fetched coffeeShops");
			System.out.println(coffeeShops);
			this.fillTable(coffeeShops);
		});

	}
	
	protected void pushData() {
		Server.instance().post("/coffeeshop/add", new AddCoffeeShopParameters() {{
			name = selectedData.apply().name;
			address = selectedData.apply().address;
		}}, coffeeShop -> {
			updateData();
		});
	}
	
	protected CoffeeShopDto cloneData(CoffeeShopDto care) {
		CoffeeShopDto clone = CoffeeShopDto.clone(care);
		return clone;
	}

	protected CoffeeShopDto createEmptyData() {
		CoffeeShopDto care = new CoffeeShopDto(null, "New", null);
		return care;
	}

	protected Object[] dataToRow(CoffeeShopDto coffeeShop) {
		return new Object[] { coffeeShop.id, coffeeShop.name, coffeeShop.address };
	}

	final protected void fillTable(CoffeeShopDto[] data) {
		logger.info("fillTable: " + dataTableSelector);
		logger.info(data);
		System.out.println(data);

		if (dataTable != null) {
			dataTable.destroy();
			$(dataTableSelector + " > tbody").html("");
		}
		datas.apply(data);
		dataTable = $(dataTableSelector).DataTable(App.getDefaultDataTableConfiguration());
		dataTable.on("select", function((Event e, DataTable dt, Object type, int[] indexes) -> {
			selectedDataIndex = indexes[0];
			console.info(selectedDataIndex);
		}));

	}
	
	final protected void updateData() {
		logger.info("updateData saved");
		System.out.println(selectedData);
		if (selectedData.apply().id == null) {
			System.out.println("adding row");
			System.out.println(dataToRow(selectedData.apply()));
			dataTable.row.add(dataToRow(selectedData.apply())).draw(false);
		} else {
			System.out.println("updating row");
			dataTable.row(selectedDataIndex).data(dataToRow(selectedData.apply()));
			datas.apply()[selectedDataIndex] = selectedData.apply();
		}
	}
}
