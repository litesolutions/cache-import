package es.litesolutions.cache.commands;

import es.litesolutions.cache.CachedbImport;
import es.litesolutions.cache.db.CacheDb;
import es.litesolutions.cache.CacheRunner;
import es.litesolutions.cache.Util;

import java.util.Map;

/**
 * Abstract class for a command run by {@link CachedbImport}
 */
public abstract class CachedbCommand
{
    protected final CacheDb cacheDb;
    protected final CacheRunner runner;
    protected final Map<String, String> arguments;

    /**
     * Constructor
     *
     * @param cacheDb the database
     * @param arguments map of arguments
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    protected CachedbCommand(final CacheDb cacheDb,
        final Map<String, String> arguments)
    {
        this.cacheDb = cacheDb;
        runner = new CacheRunner(cacheDb);
        this.arguments = arguments;
    }

    /**
     * Execute this command
     *
     * @throws Exception refined in each individual command
     */
    public abstract void execute()
        throws Exception;

    protected final String getArgument(final String name)
    {
        return Util.readArgument(name, arguments);
    }

    protected final String getArgumentOrDefault(final String name,
        final String defaultValue)
    {
        return Util.readOrDefault(name, arguments, defaultValue);
    }
}
