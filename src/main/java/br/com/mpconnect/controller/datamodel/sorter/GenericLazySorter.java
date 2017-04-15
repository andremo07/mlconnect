package br.com.mpconnect.controller.datamodel.sorter;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

public class GenericLazySorter<T> implements Comparator<T> {
	 
    private String sortField;
     
    private SortOrder sortOrder;
     
    public GenericLazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    public int compare(T obj1, T obj2) {
        try {
            Object value1 = ((Class<T>)getClass()).getField(this.sortField).get(obj1);
            Object value2 = ((Class<T>)getClass()).getField(this.sortField).get(obj2);
 
            int value = ((Comparable)value1).compareTo(value2);
             
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
