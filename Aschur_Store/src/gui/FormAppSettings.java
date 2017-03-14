package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

import org.hibernate.Session;
import org.hibernate.Transaction;

import management.AppSetting;
import management.Manager;
import person.StatesConsumer;
import product.Product;

@SuppressWarnings("serial")
public class FormAppSettings extends JFrame {

	private FormAppSettingsPanel formAppSettingsPanel;

	private JButton SaveAndCloseButton = new JButton("Save and close");
	private ActionListener SaveAndCloseAL = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			boolean failure = false;
			
			Long countConsumers = (Long) formAppSettingsPanel.getCountConsumersFieldValue();
			if (countConsumers <= 0 || countConsumers > 20) {
				System.out.println("count consumers must be 1 - 20");
				failure = true;
			}
			
			Long maxCountEnterStore = (Long) formAppSettingsPanel.getMaxCountEnterStoreFieldValue();
			if (maxCountEnterStore <= 0 || maxCountEnterStore > 4) {
				System.out.println("max count enter store must be 1 - 4");
				failure = true;
			}
			
			Long timeWorkStore = (Long) formAppSettingsPanel.getTimeWorkStoreFieldValue();
			if (timeWorkStore < 1 || timeWorkStore > 60) {
				System.out.println("count consumers must be 1 min - 60 min");
				failure = true;
			}
			
			if (failure) {
				return;
			}
			
			
			Session session = Manager.getSession();
			
			Transaction tx = session.beginTransaction();
			
			AppSetting countConsumersAppSettings = new AppSetting("countConsumers", countConsumers);
			try {
				session.save(countConsumersAppSettings);
			} catch (Exception e2) {
				System.out.println(countConsumersAppSettings.getValue());
			}
//			AppSetting maxCountEnterStoreAppSettings = new AppSetting("maxCountEnterStore", maxCountEnterStore);
//			try {
//				session.save(maxCountEnterStoreAppSettings);
//			} catch (Exception e2) {
//				System.out.println("");
//			}
//			AppSetting timeWorkStoreAppSettings = new AppSetting("timeWorkStore", timeWorkStore);
//			try {
//				session.save(timeWorkStoreAppSettings);
//			} catch (Exception e2) {
//				System.out.println("");
//			}
//		
			tx.commit();
			
			
			
		}

	};

	public FormAppSettings() {

		super("please enter application parametrs");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridLayout());

		// Add contents to the window.
		formAppSettingsPanel = new FormAppSettingsPanel();
		add(formAppSettingsPanel);

		SaveAndCloseButton.addActionListener(SaveAndCloseAL);
		add(SaveAndCloseButton);

		// Display the window.
		pack();
		setVisible(true);

	}

	private class FormAppSettingsPanel extends JPanel {

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
			countEditFormat = NumberFormat.getInstance();

		}

		private JFormattedTextField createTextField() {

			JFormattedTextField field = new JFormattedTextField(
					new DefaultFormatterFactory(new NumberFormatter(
							countDisplayFormat), new NumberFormatter(
							countDisplayFormat), new NumberFormatter(
							countEditFormat)));
			field.setValue(new Long(0));
			field.setColumns(10);

			return field;

		}

		public Object getCountConsumersFieldValue() {
			return countConsumersField.getValue();
		}

		public Object getMaxCountEnterStoreFieldValue() {
			return maxCountEnterStoreField.getValue();
		}

		public Object getTimeWorkStoreFieldValue() {
			return timeWorkStoreField.getValue();
		}

	}

//	public static void main(String[] args) {
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//
//				new FormAppSettings();
//
//			}
//
//		});
//
//	}

}
