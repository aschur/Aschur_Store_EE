package management;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;


/**
 * Created by Aschur on 15.01.2017
 */



/**
 * @author Yulay
 *
 */
public class SettingsLoader {


	/**
	 * Settings - Map<String, String>:
	 * modeDataStorage - "fileApp", "dataBase"
	 * usageMode - "handMode", "automaticMode"
	*/
	public static Map<String, String> getPrimarySettings(){

		Map<String, String> settings = new HashMap<String, String>();
		settings.put("modeDataStorage", "");
		settings.put("usageMode", "");

		String modeDataStorage = "";
		String usageMode = "";
		
		InputStream inputStream = null;
		StringBuilder sb = new StringBuilder();

		try {

			ClassLoader myCL = SettingsLoader.class.getClassLoader();
			inputStream = myCL.getResourceAsStream("PrimarySettings.txt");

			try {

				int c;
				while ((c = inputStream.read()) != -1) {

					sb.append(Character.toString((char) c));

				}

			} finally {

				inputStream.close();
			}

		} catch (Exception x) {
			
			x.printStackTrace();
			
			return settings;
			
		}

		String allString = sb.toString().trim();

		String[] primarySettings = allString.split("\r\n");

	
		
		if (primarySettings.length == 2) {
			
			modeDataStorage = primarySettings[0];
			settings.put("modeDataStorage", modeDataStorage);
			
			usageMode = primarySettings[1];
			settings.put("usageMode", usageMode);
			
		}
		
	
		return settings;

	}

	/**
	 * Settings - Map<String, String>:
	 * fileNameProcurementPlan
	 * fileNameProducts
	 * countConsumers
	 * maxCountEnterStore
	 * timeWorkStore
	*/
	public static Map<String, String> getSettings() {

		Map<String, String> settings = new HashMap<String, String>();
		settings.put("fileNameProcurementPlan", "");
		settings.put("fileNameProducts", "");
		settings.put("countConsumers", "");
		settings.put("maxCountEnterStore", "");
		settings.put("timeWorkStore", "");
		
		String modeDataStorage = Manager.getModeDataStorage();
		if (modeDataStorage.equals("fileApp")) {

			settings = getFileAppSettings(settings);
			
		} else {

			settings = getDataBaseSettings(settings);
			
		}
		
	
		return settings;

	}

	private static Map<String, String> getFileAppSettings(Map<String, String> settings) {

		InputStream inputStream = null;
		StringBuilder sb = new StringBuilder();

		try {

			ClassLoader myCL = SettingsLoader.class.getClassLoader();
			inputStream = myCL.getResourceAsStream("FileAppSettings.txt");

			try {

				int c;
				while ((c = inputStream.read()) != -1) {

					sb.append(Character.toString((char) c));

				}

			} finally {

				inputStream.close();
			}

		} catch (Exception x) {
			
			x.printStackTrace();
			
			return settings;
			
		}

		String allString = sb.toString().trim();

		String[] fileAppSettings = allString.split("\n");

		if (fileAppSettings.length == 5) {
			settings.put("fileNameProcurementPlan", fileAppSettings[0]);
			settings.put("fileNameProducts", fileAppSettings[1]);
			settings.put("countConsumers", fileAppSettings[2]);
			settings.put("maxCountEnterStore", fileAppSettings[3]);
			settings.put("timeWorkStore", fileAppSettings[4]);
		}
		
		return settings;

	}

	private static  Map<String, String> getDataBaseSettings(Map<String, String> settings) {
		
		Session session = Manager.getSession();
		
		String query = "select p from " + AppSetting.class.getSimpleName() + " p";
			
        @SuppressWarnings("unchecked")
		List<AppSetting> list = (List<AppSetting>)session.createQuery(query).list();
		
        for (AppSetting appSetting : list) {
			
        	String nameSetting = appSetting.getName();
        	String valueSetting = Long.toString(appSetting.getValue());
        	if (settings.get(nameSetting) != null) {
        		settings.put(nameSetting, valueSetting);
			}
        	
		}
        
		return settings;
		
	}
	
}	