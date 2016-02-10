package es.litesolutions.cache;

import com.intersys.classes.Dictionary.ClassDefinition;
import com.intersys.objects.CacheException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public final class Main
{
    private static final String CACHEDB_HOST = "cachedb.host";
    private static final String CACHEDB_PORT = "cachedb.port";
    private static final String CACHEDB_USER = "cachedb.user";
    private static final String CACHEDB_PASSWORD = "cachedb.password";
    private static final String CACHEDB_NAMESPACE = "cachedb.namespace";

    private static final String CACHEDB_HOST_DEFAULT = "localhost";
    private static final String CACHEDB_PORT_DEFAULT = "1972";

    private static final String JDBC_URL_TEMPLATE = "jdbc:Cache://%s:%s/%s";

    private static final String SQL_CLASSNAME_FIELD = "Name";

    private Main()
    {
        throw new Error("instantiation not permitted");
    }

    public static void main(final String... args)
        throws IOException, CacheException, SQLException
    {
        if (args.length == 0)
            throw new IllegalArgumentException("missing arguments");

        final Properties properties = new Properties();

        final Path path = Paths.get(args[0]).toRealPath();

        try (
            final Reader reader = Files.newBufferedReader(path);
        ) {
            properties.load(reader);
        }

        final String jdbcUrl = String.format(JDBC_URL_TEMPLATE,
            readProperty(properties, CACHEDB_HOST, CACHEDB_HOST_DEFAULT),
            readProperty(properties, CACHEDB_PORT, CACHEDB_PORT_DEFAULT),
            readProperty(properties, CACHEDB_NAMESPACE));

        final String user = readProperty(properties, CACHEDB_USER);
        final String password = readProperty(properties, CACHEDB_PASSWORD);

        try (
            final CacheDb db = new CacheDb(jdbcUrl, user, password);
            final CacheSqlQuery query
                = db.query(ClassDefinition::query_Summary);
            final ResultSet rs = query.execute();
        ) {
            while (rs.next())
                System.out.println(rs.getString(SQL_CLASSNAME_FIELD));
        } catch (CacheException e) {
            System.err.println(e.getCause());
            throw e;
        }
    }

    private static String readProperty(final Properties properties,
        final String key)
    {
        final String ret = properties.getProperty(key);
        if (ret == null)
            throw new IllegalArgumentException("required property " + key
                + " is missing");
        return ret;
    }

    private static String readProperty(final Properties properties,
        final String key, final String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }
}