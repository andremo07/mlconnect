package br.com.mpconnect.ml.api.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.markethub.repository.OrderRepository;

public class Teste2 {

	public static void main(String[] args) throws Exception{
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		OrderRepository orderRepository = (OrderRepository) ctx.getBean("orderRepository");

		List<String> ids = new ArrayList<String>();
		ids.add("350138563501");
		List<Venda> vendas  = orderRepository.findAllById(ids);
		System.out.println();
	}


}
