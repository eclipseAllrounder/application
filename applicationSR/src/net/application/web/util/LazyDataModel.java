package net.application.web.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

 
public abstract class LazyDataModel<T> extends ExtendedDataModel<T> {
	
    private Integer cachedRowCount;
    private SequenceRange cachedRange;
    private List<T> cachedList;
    private Map<Object, T> cachedMap = new HashMap<Object, T>();
    private Object rowKey;
 
    public abstract List<T> getDataList(int firstRow, int numRows);
 
    public abstract Object getKey(T t);
 
    public abstract int getTotalCount();
 
    @Override
    public void walk(FacesContext ctx, DataVisitor dv, Range range, Object argument) {
        SequenceRange sr = (SequenceRange) range;
        List<T> list = getList(sr);
        for (T t : list) {
            Object key = getKey(t);
            if (key == null) {
                throw new IllegalStateException("found null key");
            }
            cachedMap.put(key, t);
            dv.process(ctx, key, argument);
        }
 
    }
 
    private List<T> getList(SequenceRange sr) {
        //Richfaces for some reason executes walk method several times for same range, lets take data from cache than
        List<T> list;
        if (cachedRange != null && sr.getFirstRow() == cachedRange.getFirstRow() && sr.getRows() == cachedRange.getRows()) {
            list = cachedList;
        } else {
            cachedRange = sr;
            list = getDataList(sr.getFirstRow(), sr.getRows());
            cachedList = list;
        }
        return list;
    }
 
 
    /*
    * Uninque key fro each row, same that used in dv.process
    */
    @Override
    public void setRowKey(Object rowKey) {
        this.rowKey = rowKey;
    }
 
    @Override
    public Object getRowKey() {
        return rowKey;
    }
 
    @Override
    public boolean isRowAvailable() {
        return (getRowData() != null);
    }
 
    @Override
    public int getRowCount() {
        if (cachedRowCount == null) {
            cachedRowCount = getTotalCount();
        }
        return cachedRowCount;
    }
 
    @Override
    public T getRowData() {
        return cachedMap.get(getRowKey());
    }
 
 
    //This methods never called by framework.
    @Override
    public int getRowIndex() {
    	if(cachedRowCount!=null)return cachedRowCount;
    	return 0;
    }
 
    @Override
    public void setRowIndex(int rowIndex) {
    }
 
    @Override
    public Object getWrappedData() {
		return cachedList;
    }
 
    @Override
    public void setWrappedData(Object data) {
 
    }
    
	


}
