package dto;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;

public class StopDTO implements Serializable {

    @CsvBindByName(column = "stop_id")
    private String id;
    @CsvBindByName(column = "stop_code")
    private String code;
    @CsvBindByName(column = "stop_name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
