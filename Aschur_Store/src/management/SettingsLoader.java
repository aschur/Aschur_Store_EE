package management;

import java.io.*;

/**
 * Created by Aschur on 15.01.2017
 */
public class SettingsLoader {

    public static String[] getSettings() {

        String[] settings = null;
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        try {

            ClassLoader myCL = SettingsLoader.class.getClassLoader();
            inputStream = myCL.getResourceAsStream("Settings.txt");

            try {

                int c;
                while((c = inputStream.read()) != -1){

                    sb.append(Character.toString((char) c));

                }

            }finally {


                inputStream.close();}


        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        String allString = sb.toString().trim();

        settings = allString.split("\n");

        return settings;

    }

}
