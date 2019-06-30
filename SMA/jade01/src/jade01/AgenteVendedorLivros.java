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


import java.util.*;
public class AgenteVendedorLivros extends Agent {
	private Hashtable catalogo;
	private GUIVendalivros gui;
	
	@Override 
	protected void setup() {
		System.out.println("Bem vindo agente vendedor "+getAID().getName()+"tenha boas vendas");
		catalogo =  new Hashtable();
		
		gui = new GUIVendalivros(this);
		gui.showGui();
		
		DFAgentDescription df = new DFAgentDescription();
		df.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("venda-livros");
		sd.setName("Carteira Jade");
		df.addServices(sd);
		
		try {
			DFService.register(this,df);			
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		//adiciona o comportamento para servir as consultas dos compradores
		addBehaviour(new DemandaOfertaServidor());
		//adiciona o comportamento servindo ordens de compra dos compradores
		addBehaviour(new DemandaCompraServidor());
	}
	@Override
	protected void takeDown() {
		
		try {
			//
			DFService.deregister(this);
		}catch(FIPAException e) {
			e.printStackTrace();
		}
		gui.dispose();
		//
		System.out.println("Agente Vendedor "+getAID().getName()+" Sua missao termina por aqui!!");
	
	}
	public void atualizaCatalago(final String titulo, final int preco) {
		addBehaviour(new OneShotBehaviour() 
		{
			public void action() {
				catalogo.put(titulo, new Integer(preco));
				System.out.println("Livro: "+titulo+", está disponível por "+ preco); 
				 
			}
			
		});
	}
	
	private class DemandaOfertaServidor extends CyclicBehaviour{
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage  msg = myAgent.receive(mt);
			if(msg != null) {
				String titulo = msg.getContent();
				ACLMessage resposta = msg.createReply();
				
				Integer preco = (Integer) catalogo.get(titulo);
				
				if(preco != null) {
					resposta.setPerformative(ACLMessage.PROPOSE);
					resposta.setContent(String.valueOf(preco.intValue()));
					
				}
				else
				{
					resposta.setPerformative(ACLMessage.REFUSE);
					resposta.setContent("Não disponível");
				}
					
				myAgent.send(resposta);
				
				}else {
					block();
				}
			}
		}//fim da classe
	private class DemandaCompraServidor extends CyclicBehaviour{
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg =  myAgent.receive(mt);
			if(msg != null) {
				String titulo = msg.getContent();
				ACLMessage resposta = msg.createReply();
				
				Integer preco = (Integer ) catalogo.remove(titulo);
				if(preco !=null) {
					resposta.setPerformative(ACLMessage.INFORM);
					System.out.println("O livro "+titulo+ "foi vendido ao agente: "+msg.getSender().getName());
				}
				else{
					resposta.setPerformative(ACLMessage.FAILURE);
					resposta.setContent("Não disponivell");
				}
				myAgent.send(resposta);
				
				}else {
					block();
				}
			}
		}
	//fim da classe	
	}


	
	
