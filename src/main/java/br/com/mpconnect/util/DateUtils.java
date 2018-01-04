package br.com.mpconnect.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static String getDataAnteriorString(Integer dias){
		String datePattern = "yyyy-MM-dd";
		SimpleDateFormat formater = new SimpleDateFormat(datePattern);
		Date dt = new Date();
		if(dias!=null&& dias!=0){
			Calendar c = Calendar.getInstance();
			c.setTime(dt);
			c.add(Calendar.DATE, -dias);
			dt = c.getTime();
			String dtStr = formater.format(dt);
			return dtStr;
		}
		else 
			return formater.format(dt);
	}

	public static String getDataFormatada(Date data){
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
		SimpleDateFormat formater = new SimpleDateFormat(datePattern);
		if(data!=null){
			String dtStr = formater.format(data);
			return dtStr;
		}
		else 
			return formater.format(new Date());
	}

	public static Date getDataFormatada(String data){

		try {
			String datePattern = "yyyy-MM-dd";
			SimpleDateFormat formater = new SimpleDateFormat(datePattern);
			if(data!=null){
				Date dt = formater.parse(data);
				return dt;
			}
			else 
				return formater.parse(new Date().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getDataFormatada(String data, String datePattern){

		try {
			SimpleDateFormat formater = new SimpleDateFormat(datePattern);
			if(data!=null){
				Date dt = formater.parse(data);
				return dt;
			}
			else 
				return formater.parse(new Date().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDataFormatada(Date data, String datePattern){

		SimpleDateFormat formater = new SimpleDateFormat(datePattern);
		if(data!=null){
			String formattedDate = formater.format(data);
			return formattedDate;
		}
		else 
			return null;
	}

	public static long getDiffDias(Date dataInicio, Date dataFim){

		long dt = (dataFim.getTime() - dataInicio.getTime()) + 3600000;      
		long dias = (dt / 86400000L);  
		return dias;
	}


	public static Date adicionaHoras(Date data, int qtHoras){

		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.add(Calendar.HOUR_OF_DAY, qtHoras);
		cal.getTime();

		return cal.getTime();
	}

	public static Date adicionaDias(Date data, int qtDias){

		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.add(Calendar.DATE, qtDias);
		cal.getTime();

		return cal.getTime();
	}

	public static Date adicionaMes(Date data, int qtMes){

		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.add(Calendar.MONTH, qtMes);
		cal.getTime();

		return cal.getTime();
	}

	public static Date setHoras(Date data, int horas){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, horas);
		return cal.getTime();
	}

	public static Date setDias(Date data, int dias){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.set(Calendar.DATE, dias);
		return cal.getTime();
	}

	public static Date setMeses(Date data, int meses){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.set(Calendar.MONTH, meses);
		return cal.getTime();
	}

	public static Date setMinutesSenconds(Date data, int minutes,int seconds){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, minutes);
		return cal.getTime();
	}

	public static Integer getMes(Date data){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		return cal.get(Calendar.MONTH)+1;
	}
	
	public static Integer getDia(Date data){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		return cal.get(Calendar.DATE);
	}
	
	public static Integer getAno(Date data){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		return cal.get(Calendar.YEAR);
	}
	
	public static Date criarData(Integer dia, Integer mes, Integer ano, Integer hora, Integer minuto){
		
		Calendar cal = Calendar.getInstance(); 
		Date data = new Date();
		cal.setTime(data);
		cal.set(ano, mes, dia);
		cal.set(Calendar.HOUR_OF_DAY, hora);
		cal.set(Calendar.MINUTE, minuto);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	public static Date getPrimeiroDiaSemana(){
		
		Date data = new Date();
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

}
