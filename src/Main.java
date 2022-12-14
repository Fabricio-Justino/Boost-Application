import br.com.fabricio.CRUD.QueryCreator;
import br.com.fabricio.CRUD.SqliteDialect;
import br.com.fabricio.entities.Client;

public class Main {

    public static void main(String[] args) {
        //BoostAplication.run(Main.class);
         QueryCreator.useDialect(new SqliteDialect());
         QueryCreator.createTable(Client.class);

         Client c = new Client();
         c.setCpf("01923081203");
         c.setName("Fabricio");
         c.setLastName("Justino");
         c.setBalance(156.5);

        System.out.println(QueryCreator.update(c.getClass(), c));
        System.out.println(QueryCreator.insertInto(c.getClass(), c));
        //CRUD.INSTANCE.createCrud("database\\Application.db");
        //CRUD.INSTANCE.createTable(Client.class);
        //System.out.println("coonection : " + ReadProperties.INSTANCE.get("URL_CONNECTION"));
    }
}
