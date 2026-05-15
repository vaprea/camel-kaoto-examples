//DEPS org.apache.camel:camel-api:RELEASE
//DEPS org.postgresql:postgresql:42.7.3

import org.apache.camel.BindToRegistry;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;

public class DataSourceConfig {

    @BindToRegistry("dataSource")
    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setPortNumber(5433);
        ds.setDatabaseName("ordersdb");
        ds.setUser("camel");
        ds.setPassword("camel");
        return ds;
    }
}
