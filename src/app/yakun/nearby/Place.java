package app.yakun.nearby;

public class Place {

	private String name;
	private String address;
	private String latLon;
	private Boolean status;

	public Place(String name, String address, String latLon, Boolean status) {
		super();
		this.name = name;
		this.address = address;
		this.latLon = latLon;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
	
	public Boolean getStatus() {
		return status;
	}
	
	public String getLatLon(){
		return latLon;
	}
	
}
