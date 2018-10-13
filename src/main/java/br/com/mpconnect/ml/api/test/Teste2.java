package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.provider.NFeProvider;

public class Teste2 {

	public static void main(String[] args) throws Exception {

//		 ClassPathXmlApplicationContext ctx = new
//		 ClassPathXmlApplicationContext("spring.xml");
//		
//		 OrderRepository orderRepository = (OrderRepository)
//		 ctx.getBean("orderRepository");
//		
//		 List<String> ids = new ArrayList<String>();
//		 ids.add("350138563501");
//		 List<Venda> vendas = orderRepository.findAllById(ids);
//		 System.out.println();

//		try {
//			FileUtils.writeByteArrayToFile(new File("producao.cacerts"),
//					GeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.PRODUCAO, "123456"));
//			FileUtils.writeByteArrayToFile(new File("homologacao.cacerts"),
//					GeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.HOMOLOGACAO, "123456"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
		
		
		nfeProvider.testaServico();


	}

}
