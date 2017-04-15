package br.com.mpconnect.controller.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.mpconnect.controller.datamodel.sorter.LazySorter;
import br.com.mpconnect.model.Funcionario;

public class FuncionarioDataModel extends LazyDataModel<Funcionario> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Funcionario> datasource;

	public FuncionarioDataModel(List<Funcionario> datasource) {
		this.datasource = datasource;
	}

	@Override
	public Funcionario getRowData(String rowKey) {
		for(Funcionario funcionario : datasource) {
			if(funcionario.getId().equals(rowKey))
				return funcionario;
		}

		return null;
	}

	@Override
	public Object getRowKey(Funcionario funcionario) {
		return funcionario.getId();
	}

	@Override
	public List<Funcionario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
		List<Funcionario> data = new ArrayList<Funcionario>();

		//filter
		for(Funcionario funcionario : datasource) {
			boolean match = true;

			if (filters != null) {
				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = String.valueOf(funcionario.getClass().getField(filterProperty).get(funcionario));

						if(filterValue == null || fieldValue.startsWith(filterValue.toString())) {
							match = true;
						}
						else {
							match = false;
							break;
						}
					} catch(Exception e) {
						match = false;
					}
				}
			}

			if(match) {
				data.add(funcionario);
			}
		}

		//sort
		if(sortField != null) {
			Collections.sort(data, new LazySorter(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		//paginate
		if(dataSize > pageSize) {
			try {
				return data.subList(first, first + pageSize);
			}
			catch(IndexOutOfBoundsException e) {
				return data.subList(first, first + (dataSize % pageSize));
			}
		}
		else {
			return data;
		}
	}
}
