package jade01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jade.core.AID;

public class GUIVendalivros extends JFrame {

		private AgenteVendedorLivros myAgent;
		
		private JTextField campoTitulo, campoPreco;

		public GUIVendalivros(AgenteVendedorLivros a) {
			super(a.getLocalName());
			myAgent = a;
			
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(2,2));
			p.add(new JLabel("Titulo do livro: "));
			campoTitulo = new JTextField(15);
			p.add(campoTitulo);
			p.add(new JLabel("Preco: "));
			campoPreco = new JTextField(15);
			p.add(campoPreco);
			getContentPane().add(p, BorderLayout.CENTER);
			
			JButton addButton = new JButton("ADD");
			addButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String title = campoTitulo.getText().trim();
						String price = campoPreco.getText().trim();
						myAgent.atualizaCatalago(title, Integer.parseInt(price));
						campoTitulo.setText("");
						campoPreco.setText("");
						
						
						
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(GUIVendalivros.this, "Valor invalido" + e2.getMessage());
					}
					
				}
			});
			p = new JPanel();
			p.add(addButton);
			getContentPane().add(p, BorderLayout.SOUTH);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					myAgent.doDelete();
				}
			});
			
			setResizable(false);
			
		}
		public void showGui() {
			pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int ) screenSize.getWidth();
			int y = (int ) screenSize.getHeight();
			setLocation(x - getWidth()/2, y - getHeight()/2);
			setVisible(true);
			
		
		}
		
		
		
	
	
}
