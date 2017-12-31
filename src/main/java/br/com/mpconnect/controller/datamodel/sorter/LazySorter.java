package br.com.mpconnect.controller.datamodel.sorter;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import br.com.mpconnect.model.Funcionario;

public class LazySorter implements Comparator<Funcionario> {
	 
    private String sortField;
     
    private SortOrder sortOrder;
     
    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    @Override
	public int compare(Funcionario funcionario1, Funcionario funcionario2) {
        try {
            Object value1 = Funcionario.class.getField(this.sortField).get(funcionario1);
            Object value2 = Funcionario.class.getField(this.sortField).get(funcionario2);
 
            int value = ((Comparable)value1).compareTo(value2);
             
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
