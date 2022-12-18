
import br.com.fabricio.CRUD.QueryCreator;
import br.com.fabricio.CRUD.SqliteDialect;
import br.com.fabricio.entities.Client;
import br.com.fabricio.utils.TextFormat;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        //BoostAplication.run(Main.class);
         //QueryCreator.useDialect(new SqliteDialect());
        System.out.println(QueryCreator.createTable(Client.class));

         Client c = new Client();
         c.setCpf("01923081203");
         c.setName("Fabricio");
         c.setLastName("Justino");
         c.setBalance(156.5);

       // System.out.println(QueryCreator.createTable(Client.class));
       // System.out.println(QueryCreator.update(c.getClass(), c));
       // System.out.println(QueryCreator.insertInto(c.getClass(), c));
    }
}
