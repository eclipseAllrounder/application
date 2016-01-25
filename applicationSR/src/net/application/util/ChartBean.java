package net.application.util;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
 
@ManagedBean
@RequestScoped
public class ChartBean {
     
    private List<Double> values;
     
    @PostConstruct
    public void init() {
        values = new ArrayList<>();
        values.add(10d);
        values.add(12d);
        values.add(11.5);
        values.add(13d);
        values.add(12.5);
        values.add(15d);
    }
 
    public List<Double> getValues() {
        return values;
    }
 
    public void setValues(List<Double> values) {
        this.values = values;
    }
}