package br.com.mpconnect.controller.datamodel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.mpconnect.controller.GenericCrudController;
import br.com.mpconnect.controller.datamodel.sorter.GenericLazySorter;

public class GenericDataModel<T> extends LazyDataModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<T> datasource;
	private GenericCrudController<T> controller;

	public GenericDataModel(GenericCrudController<T> controller) {
		this.controller = controller;
	}

	@Override
	public T getRowData(String rowKey) {
		for(T obj : datasource) {
			Object key = getRowKey(obj);
			if(key.equals(rowKey))
				return obj;
		}
		return null;
	}

	@Override
	public Object getRowKey(T obj) {

		try {
			Method method = obj.getClass().getMethod("getId", null);
			if(method!=null){
				Object teste = method.invoke(obj,null);
				return teste;
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
		List<T> data = new ArrayList<T>();
		
		//filter
//		for(T obj : datasource) {
//			boolean match = true;
//
//			if (filters != null) {
//				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
//					try {
//						String filterProperty = it.next();
//						Object filterValue = filters.get(filterProperty);
//						String fieldValue = String.valueOf(obj.getClass().getDeclaredField(filterProperty).get(obj));
//
//						if(filterValue == null || fieldValue.startsWith(filterValue.toString())) {
//							match = true;
//						}
//						else {
//							match = false;
//							break;
//						}
//					} catch(Exception e) {
//						match = false;
//					}
//				}
//			}
//
//			if(match) {
//				data.add(obj);
//			}
//		}

		//sort
		if(sortField != null) {
			Collections.sort(data, new GenericLazySorter(sortField, sortOrder));
		}

		//rowCount
		//int dataSize = data.size();
		//this.setRowCount(dataSize);

		//paginate
//		if(getRowCount() > pageSize) {
//
//		}
//		else {
//			return data;
//		}
//		
		try {
			//return data.subList(first, first + pageSize);
			return controller.paginacao(first,first + pageSize,filters);
		}
		catch(IndexOutOfBoundsException e) {
			return data.subList(first, first + (getRowCount() % pageSize));
		}
	}

	public List<T> getDatasource() {
		return datasource;
	}

	public void setDatasource(List<T> datasource) {
		this.datasource = datasource;
	}
	
}
