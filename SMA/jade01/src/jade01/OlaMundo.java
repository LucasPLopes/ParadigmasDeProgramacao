package jade01;

import jade.core.Agent;

public class OlaMundo extends Agent {

	
		public void setup() {
			System.out.println("Olá mundo");
			System.out.println("Meu nome  é "+ getLocalName());
		}
}
