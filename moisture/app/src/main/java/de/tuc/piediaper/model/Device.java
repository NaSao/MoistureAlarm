package de.tuc.piediaper.model;

import java.io.Serializable;

public class Device implements Serializable {
    private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String api_key;
	private String variable_id;
	private String type;
    private String state;
    private String percent;
    private double moisture;
	

	public Device() {
	}

	public Device(long id, String name, String api_key, String telefone,
                  String type,String state) {
		this.id = id;
		this.name = name;
		this.api_key = api_key;
		this.variable_id = telefone;
		this.type = type;
        this.state = state;
	}

	public Device(String name, String api_key, String telefone, String type,String state) {
		this.name = name;
		this.api_key = api_key;
		this.variable_id = telefone;
		this.type = type;
        this.state = state;
	}

	@Override
	public String toString() {
		return name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApi_key() {
		return api_key;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	public String getVariable_id() {
		return variable_id;
	}

	public void setVariable_id(String variable_id) {
		this.variable_id = variable_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String lastValue) {
        this.percent = lastValue;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }
}
