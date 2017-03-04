package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import person.StatesConsumer;
import product.Product;

@SuppressWarnings("serial")
public class FormAppSettings extends JFrame {

	
	private JButton SaveAndCloseButton = new JButton("Save and close");
    private ActionListener SaveAndCloseAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {


        }

    };

	
	public FormAppSettings() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridLayout());
		
		
		
		// Add contents to the window.
		add(new FormAppSettingsPanel());
		
		SaveAndCloseButton.addActionListener(SaveAndCloseAL);
		add(SaveAndCloseButton);
		
		// Display the window.
		pack();
		setVisible(true);
		

	}
	
	
	private class FormAppSettingsPanel extends JPanel
	{
		
		// Labels to identify the fields
		private JLabel countConsumersLabel;
		private JLabel maxCountEnterStoreLabel;
		private JLabel timeWorkStoreLabel;

		// Strings for the labels
		private String countConsumersString = "Count consumers: ";
		private String maxCountEnterStoreString = "Maximum count enter store: ";
		private String timeWorkStoreString = "Time work store: ";

		// Fields for data entry
		 private JFormattedTextField countConsumersField;
		 private JFormattedTextField maxCountEnterStoreField;
		 private JFormattedTextField timeWorkStoreField;
		
		// Formats to format and parse numbers
		private NumberFormat countDisplayFormat;
		private NumberFormat countEditFormat;
		

		public FormAppSettingsPanel() {
			super(new BorderLayout());
			setUpFormats();

			// Create the labels.
			countConsumersLabel = new JLabel(countConsumersString);
			maxCountEnterStoreLabel = new JLabel(maxCountEnterStoreString);
			timeWorkStoreLabel = new JLabel(timeWorkStoreString);
			
			// Create the text fields and set them up.			
			countConsumersField = createTextField();	
			maxCountEnterStoreField = createTextField();
			timeWorkStoreField = createTextField();
			
			
			// Tell accessibility tools about label/textfield pairs.
			countConsumersLabel.setLabelFor(countConsumersField);
			maxCountEnterStoreLabel.setLabelFor(maxCountEnterStoreField);
			timeWorkStoreLabel.setLabelFor(timeWorkStoreField);
			

			// Lay out the labels in a panel.
			JPanel labelPane = new JPanel(new GridLayout(0, 1));
			labelPane.add(countConsumersLabel);
			labelPane.add(maxCountEnterStoreLabel);
			labelPane.add(timeWorkStoreLabel);

			// Layout the text fields in a panel.
			JPanel fieldPane = new JPanel(new GridLayout(0, 1));
			fieldPane.add(countConsumersField);
			fieldPane.add(maxCountEnterStoreField);
			fieldPane.add(timeWorkStoreField);

			// Put the panels in this panel, labels on left,
			// text fields on right.
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			add(labelPane, BorderLayout.CENTER);
			add(fieldPane, BorderLayout.LINE_END);
		}


		
		// Create and set up number formats. These objects also
		// parse numbers input by user.
		private void setUpFormats() {
		
			countDisplayFormat = NumberFormat.getInstance();
			countDisplayFormat.setMinimumFractionDigits(0);
			countEditFormat = NumberFormat.getNumberInstance();

		}
		
		private JFormattedTextField createTextField() {
			
			JFormattedTextField field = new JFormattedTextField(new DefaultFormatterFactory(
					new NumberFormatter(countDisplayFormat),
					new NumberFormatter(countDisplayFormat),
					new NumberFormatter(countEditFormat)));
			field.setValue(new Integer(0));
			field.setColumns(10);
			
			return field;
			
		}
	}


	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				new FormAppSettings();

			}

		});

	}

}
