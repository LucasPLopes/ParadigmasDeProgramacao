package jade01;

import jade.core.Agent;
import jade.core.AID;
import java.util.Iterator;

public class InfoAgent extends Agent{
	
	public void setup() {
		
		System.out.println("Hello world. I'M A AGENT!");
		System.out.println("Todas as minhas informações: "+ getAID());
		System.out.println("Meu nome local é: "+getLocalName());
		System.out.println("Meu nome global(GUID) é "+getAID().getName());
		System.out.println("Meus endereçoes são:");
		
		Iterator it = getAID().getAllAddresses();
		
		while(it.hasNext()) {
			System.out.println("-"+it.next());
		}		
		
		
	}

}
