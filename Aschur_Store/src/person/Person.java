package person;

/**
 * Created by Aschur on 03.11.2016.
 */
public abstract class Person implements Runnable{

    private String name = "";
    private static int id = 0;

    public Person() {

        id = ++id;
        name = getClass().getSimpleName() + "_" + id;

    }

    public void run() {

    }

    public String getName() {
        return name;
    }

    public static int getId() {
        return id;
    }
}
