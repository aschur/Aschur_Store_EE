package management;

import equipment.BarcodeReader;
import equipment.CashRegister;
import person.*;
import gui.FormAppSettings;
import gui.TellerWorkingPlace;
import product.*;

import javax.swing.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Aschur on 22.11.2016.
 */
public class Manager {

	private static String modeDataStorage = "";
	private static volatile Session session;

	public static void main(String[] args) throws Exception {

		Map<String, String> primarySettings = SettingsLoader
				.getPrimarySettings();

		String modeDataStorage = primarySettings.get("modeDataStorage");
		if (modeDataStorage.equals("fileApp")
				|| modeDataStorage.equals("dataBase")) {
			setModeDataStorage(modeDataStorage);
		} else {
			System.out.println("modeDataStorage parametr is undefined");
			return;
		}

		String usageMode = primarySettings.get("usageMode");
		if (!(usageMode.equals("handMode") || usageMode.equals("automaticMode"))) {
			System.out.println("usageMode parametr is undefined");
			return;
		}

		if (modeDataStorage.equals("dataBase")) {
			session = createHibernateSession();
			if (session == null) {
				System.out.println("failed to create hibernate session");
				return;
			}
		}
		
		Map<String, String> settings = SettingsLoader.getSettings();

		int countConsumers = 0;
		try {
			countConsumers = Integer.parseInt(settings.get("countConsumers"));
		} catch (Exception e) {

		}
		
		if (countConsumers == 0) {
			System.out.println("countConsumers parametr is undefined");
		}

		int maxCountEnterStore = 0;
		try {
			maxCountEnterStore = Integer.parseInt(settings
					.get("maxCountEnterStore"));
		} catch (Exception e) {

		}

		if (maxCountEnterStore == 0) {
			System.out.println("maxCountEnterStore parametr is undefined");
		}
		
		int timeWorkStore = 0;
		try {
			timeWorkStore = Integer.parseInt(settings.get("timeWorkStore"));
		} catch (Exception e) {

		}
		
		if (timeWorkStore == 0) {
			System.out.println("timeWorkStore parametr is undefined");
		}
		
		if (countConsumers == 0 || maxCountEnterStore == 0 || timeWorkStore == 0) {
			
			if (modeDataStorage.equals("fileApp")) {
				
				System.out.println("not all parameters are defined");
				return;
				
			}else{
				
				Thread formAppSettings = new Thread(new Runnable() {
					public void run() {

						new FormAppSettings();

					}

				});

				SwingUtilities.invokeAndWait(formAppSettings);

				return;
	
			}
			
		}

		
		ShelfOfConsumer shelfOfConsumer = new ShelfOfConsumer();
		ProductsDataBaseOfSeller productsDataBaseOfSeller = new ProductsDataBaseOfSeller();
		DelayProducts delayProducts = new DelayProducts();

		if (modeDataStorage.equals("fileApp")) {

			String fileNameProcurementPlan = settings
					.get("fileNameProcurementPlan");
			if (fileNameProcurementPlan.equals("")) {
				System.out
						.println("fileNameProcurementPlan parametr is undefined");
				return;
			}

			String fileNameProducts = settings.get("fileNameProducts");
			if (fileNameProducts.equals("")) {
				System.out.println("fileNameProducts parametr is undefined");
				return;
			}

			ProductsGeneratorFromTextFile.readAllString(fileNameProducts);
			RandomGeneratorProcurementPlan
					.readAllStringFromFile(fileNameProcurementPlan);

			while (ProductsGeneratorFromTextFile.hasNext()) {
				TupleOfProducts tupleOfProducts = ProductsGeneratorFromTextFile
						.next();
				shelfOfConsumer.put(tupleOfProducts.productOfConsumer,
						tupleOfProducts.productOfConsumerCount);
				productsDataBaseOfSeller.put(tupleOfProducts.productOfSeller,
						tupleOfProducts.productOfSellerCount);
			}

			if (shelfOfConsumer.isEmpty() || productsDataBaseOfSeller.isEmpty()) {

				System.out.println("failed to load products from a file");
				return;

			}

		}

		ArrayBlockingQueue<Consumer> waitingStoreConsumers = new ArrayBlockingQueue<Consumer>(
				countConsumers);
		final ConsumersQueueTeller standingLineConsumers = new ConsumersQueueTeller(
				countConsumers);

		ArrayList<Consumer> consumerArrayList = new ArrayList<Consumer>();

		for (int i = 0; i < countConsumers; i++) {
			Consumer consumer = new Consumer(standingLineConsumers,
					shelfOfConsumer);
			Thread consumerThread = new Thread(consumer, consumer.getName());
			waitingStoreConsumers.add(consumer);

			consumerArrayList.add(consumer);

			consumerThread.start();
		}

		final BarcodeReader barcodeReader = new BarcodeReader();

		ArrayList<Observable> observables = new ArrayList<Observable>();
		observables.add(barcodeReader);

		final CashRegister cashRegister = new CashRegister(observables,
				productsDataBaseOfSeller, delayProducts);

		Teller teller = null;
		Thread tellerThread = null;

		if (usageMode.equals("handMode")) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					new TellerWorkingPlace("TellerWorkingPlace",
							standingLineConsumers, cashRegister, barcodeReader);

				}

			});

		} else {

			teller = new Teller(standingLineConsumers, cashRegister,
					barcodeReader);
			tellerThread = new Thread(teller, teller.getName());

		}

		Date beginWork = new Date();

		if (usageMode.equals("automaticMode")) {

			teller.setState(StatesTeller.WORKS);
			tellerThread.start();

		}

		int countcon = countConsumers;
		while (countcon != 0) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				stopWork(teller, standingLineConsumers, waitingStoreConsumers);
				return;
			}

			int maxCountEnterStore1 = StrictMath.min(countcon,
					maxCountEnterStore);

			Random rand = new Random();
			int countEnterStore = rand.nextInt(maxCountEnterStore1 + 1);

			int countEnterStoreGood = 0;

			for (int i = 0; i < countEnterStore; i++) {
				Consumer consumer = waitingStoreConsumers.poll();
				if (consumer != null) {

					consumer.setState(StatesConsumer.TAKE);
					countEnterStoreGood++;

				}

			}

			countcon -= countEnterStoreGood;

		}

		while (true) {

			Date currentDate = new Date();
			long elapsedTime = currentDate.getTime() - beginWork.getTime();
			if (elapsedTime < (long) timeWorkStore) {

				try {
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e) {
					stopWork(teller, standingLineConsumers,
							waitingStoreConsumers);
					return;
				}

			} else {

				stopWork(teller, standingLineConsumers, waitingStoreConsumers);
				return;
			}

		}

	}

	public static void stopWork(Teller teller,
			ConsumersQueueTeller standingLineConsumers,
			ArrayBlockingQueue<Consumer> waitingStoreConsumers) {

		if (teller != null) {
			teller.setState(StatesTeller.RESTING);
		}

		while (!standingLineConsumers.isEmpty()) {

			Consumer consumer = standingLineConsumers.poll();
			if (consumer == null)
				continue;

			consumer.setState(StatesConsumer.GO_HOME);

		}

		while (!waitingStoreConsumers.isEmpty()) {

			Consumer consumer = waitingStoreConsumers.poll();
			if (consumer == null)
				continue;

			consumer.setState(StatesConsumer.GO_HOME);

		}

	}

	public static String getModeDataStorage() {

		return modeDataStorage;

	}

	private static void setModeDataStorage(String mode) {

		modeDataStorage = mode;

	}
	
	private static Session createHibernateSession()
	{
		SessionFactory   sessionFactory  = null;
		ServiceRegistry  serviceRegistry = null;
		try {
			try {
				Configuration cfg = new Configuration();
				cfg.addResource("management\\appsetting.hbm.xml");
				cfg.configure();
				serviceRegistry = new StandardServiceRegistryBuilder().
						              applySettings(cfg.getProperties()).build();
				sessionFactory = cfg.buildSessionFactory(serviceRegistry);
			} catch (Throwable e) {
				System.err.println("Failed to create sessionFactory object." + e);
				throw new ExceptionInInitializerError(e);
			}
			session = sessionFactory.openSession();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return session;
	}

	public static Session getSession() {
		return session;
	}

	

}
