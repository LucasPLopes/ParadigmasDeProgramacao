package jade01;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AgenteCompradorLivros extends Agent {
	
	private String TituloLivroComprar;
	private AID[] AgentesVendedores;

	@Override
	public void setup() {
		//mensagem de boas vindas ao comprador
		System.out.println("Olá, Comprador "+getAID().getName()+", qual livro você gostaria? ");
		Object[] args = getArguments();
		if(args != null && args.length >0 ) {
			TituloLivroComprar = (String )args[0];
			System.out.println("Estou querendo ler "+ TituloLivroComprar);
			
			//adiciona um comportamento: programar o comproador para verificar um vendedor a cada minuto
			
			addBehaviour(new TickerBehaviour(this,10000) {
				@Override
					protected void onTick() {
						System.out.println("Quero comprar" + TituloLivroComprar);
						DFAgentDescription template  = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						template.addServices(sd);
						try {
							DFAgentDescription [] result = DFService.search(myAgent, template);
							System.out.println("Vendedores:");
							for(int i = 0 ; i< result.length ; i++)
							{
								AgentesVendedores[i] = result[i].getName();
								System.out.println(AgentesVendedores[i].getName());
							}
							
							
						} catch (FIPAException e) {
							e.printStackTrace();
						}
						myAgent.addBehaviour(new PedidoCompra());
				}
			});
		}else {
			System.out.println("Livro não está disponível");
			doDelete();
		}
	}
	//Coloca o agente para limpar as operações
	@Override
	protected void takeDown() {
		System.out.println("Agente comprador "+ getAID().getName() + ", obrigado e volte sempre");
	}
	private class PedidoCompra extends Behaviour{
		private AID bestSeller;
		private int bestPrice;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		private int step  = 0;
		
		@Override
		public void action() {
			switch(step) {
				
				case 0:{
					
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					for(int i = 0 ; i<AgentesVendedores.length;i++) {
						cfp.addReceiver(AgentesVendedores[i]);
					}
					cfp.setContent(TituloLivroComprar);
					cfp.setConversationId("comercio-livros");
					cfp.setReplyWith("cfp"+System.currentTimeMillis());
					myAgent.send(cfp);
					
					mt =  MessageTemplate.and(MessageTemplate.MatchConversationId("comercio-livros"),
							MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					
					step =1;
					break;
				}
				case 1:{
					ACLMessage reply = myAgent.receive(mt);
					if(reply != null) {
						
						if(reply.getPerformative() == ACLMessage.PROPOSE) {
							int price = Integer.parseInt(reply.getContent());
							if( bestSeller == null || price < bestPrice) {
								bestPrice = price;
								bestSeller = reply.getSender();
								
							}
						}
						repliesCnt++;
						if(repliesCnt >= AgentesVendedores.length)
							step= 2;
					}else {
						block();
					}
					break;
					
				}
				case 2:{
					ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					order.addReceiver(bestSeller);
					order.setConversationId("comercio-livros");
					order.setReplyWith("Order "+System.currentTimeMillis());
					myAgent.send(order);
					
					mt =  MessageTemplate.and(MessageTemplate.MatchConversationId("comercio-livros"),
							MessageTemplate.MatchInReplyTo(order.getReplyWith()));
					
					step =3;
					break;
					
				}
				case 3:{
					ACLMessage reply = myAgent.receive(mt);
					if( reply != null) {
						//resposta da ordem de compra recebidad
						if(reply.getPerformative() == ACLMessage.INFORM) {
							System.out.println("Livro "+ TituloLivroComprar+ " comprado por "+ reply.getSender().getName());
							System.out.println("Preco "+bestPrice);
							myAgent.doDelete();
							
							
						}else {
							
						}
						step =4;
						
						
					}else {
						block();
					}
					break;
				}
			}
			
			
		
		}
		@Override
		public boolean done(){
				if(step == 2 && bestSeller == null) {
					System.out.println("Desculpe mas o livro"+ TituloLivroComprar+"já foi comprado ");
					
				}
				return ((step == 2 && bestSeller == null)|| step == 4);
		}//fim done()		
	}
}
