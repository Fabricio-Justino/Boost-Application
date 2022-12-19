
import br.com.fabricio.CRUD.CRUD;
import br.com.fabricio.CRUD.QueryCreator;
import br.com.fabricio.CRUD.SqliteDialect;
import br.com.fabricio.entities.Client;
import br.com.fabricio.utils.TextFormat;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        //BoostAplication.run(Main.class);
         QueryCreator.useDialect(new SqliteDialect());
        Collection<Client> all = CRUD.INSTANCE.findAll(Client.class);

        all.forEach(System.out::println);


    }
}
