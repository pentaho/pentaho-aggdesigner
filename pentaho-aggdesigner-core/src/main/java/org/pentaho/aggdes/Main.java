/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU General Public License, version 2 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
*
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.Progress;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils.ValidationException;
import org.pentaho.aggdes.model.Component;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.SchemaLoader;
import org.pentaho.aggdes.output.ResultHandler;

/**
 * Command line utility to run the aggregate designer algorithm.
 *
 * <p>Example usage:
 *
 * <blockquote>
 * <pre>
 * java org.pentaho.aggdes.algorithm.Main
 * --loaderClass org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader
 * --loaderParam connectString 'Provider=mondrian;Jdbc=jdbc:odbc:MondrianFoodMart;Catalog=/open/mondrian/demo/FoodMart.xml;JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver'
 * --loaderParam cube Sales
 * --algorithmClass org.pentaho.aggdes.algorithm.impl.AdaptiveMonteCarloAlgorithm
 * --algorithmParam timeLimitSeconds 300
 * --algorithmParam aggregateLimit 10
 * --resultClass org.pentaho.aggdes.output.impl.ResultHandlerImpl
 * --resultParam tables true
 * --resultParam indexes false
 * --resultParam populate true
 * </pre>
 * </blockquote>
 *
 * The components:
 * <ul>
 * <li>The loader is a class that implements the {@link SchemaLoader}
 * interface. Example: MondrianSchemaLoader.
 * <li>The algorithm is a class that implements the {@link Algorithm}
 * interface. Examples: AdaptiveMonteCarloAlgorithm, MonteCarloAlgorithm,
 * ExhaustiveLatticeAlgorithm
 * </ul>
 *
 * @author jhyde
 * @version $Id: Main.java 85 2008-04-28 22:20:13Z jhyde $
 * @since Mar 13, 2008
 */
public class Main {
    private final PrintWriter pw = new PrintWriter(System.out);
    private final String[] args;
    private SchemaLoader loader;
    private Algorithm algorithm;
    private ResultHandler resultHandler;
    private final Map<String, String> loaderRawParams =
        new LinkedHashMap<String, String>();
    private final Map<String, String> algorithmRawParams =
        new LinkedHashMap<String, String>();
    private final Map<String, String> resultHandlerRawParams =
        new LinkedHashMap<String, String>();

    /**
     * Creates an instance of Main with the command-line parameters.
     *
     * @param args Command-line parameters
     */
    private Main(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) {
        final Main main = new Main(args);
        try {
            main.run();
        } finally {
            main.pw.flush();
        }
    }

    /**
     * Prints usage.
     *
     * @param component Component; if not null, describes the parameters
     *   accepted by given component
     */
    private void usage(Component component) {
        pw.println("Usage: java " + Main.class.getName());
        pw.println("  --help");
        pw.println("  --loaderClass <class>");
        pw.println("  [ --loaderParam <name> <value> ]...");
        pw.println("  --algorithmClass <class>");
        pw.println("  [ --algorithmParam <name> <value> ]...");
        pw.println("  --resultClass <class>");
        pw.println("  [ --resultParam <name> <value> ]...");

        if (component != null) {
            pw.println();
            pw.println("Parameters for component " + component.getName()
                + " are:");
            for (Parameter parameter : component.getParameters()) {
                String desc = "  " + parameter.getName()
                    + " (" + parameter.getType()
                    + (parameter.isRequired() ? ", required" : "")
                    + ") " + parameter.getDescription();
                pw.println(desc);
            }
        }
    }

    private void parseArgs() {
        assert loader == null;
        assert algorithm == null;
        assert loaderRawParams.isEmpty();
        assert algorithmRawParams.isEmpty();

        ListIterator<String> argIter = Arrays.asList(args).listIterator();
        while (argIter.hasNext()) {
            String arg = argIter.next();
            if (arg.equals("--loaderClass")) {
                loader = readComponentClass(argIter, SchemaLoader.class);
            } else if (arg.equals("--loaderParam")) {
                readParam(arg, argIter, loaderRawParams, loader);
            } else if (arg.equals("--algorithmClass")) {
                algorithm = readComponentClass(argIter, Algorithm.class);
            } else if (arg.equals("--algorithmParam")) {
                readParam(arg, argIter, algorithmRawParams, algorithm);
            } else if (arg.equals("--resultClass")) {
                resultHandler = readComponentClass(argIter, ResultHandler.class);
            } else if (arg.equals("--resultParam")) {
                readParam(arg, argIter, resultHandlerRawParams, resultHandler);
            } else {
                throw new ValidationException(
                    null,
                    "Unknown parameter '" + arg + "'");
            }
        }
        if (loader == null) {
            throw new ValidationException(
                null,
                "Missing required component. "
                    + "Please specify '--loaderClass' argument");
        }
        if (algorithm == null) {
            throw new ValidationException(
                null,
                "Missing required component. "
                    + "Please specify '--algorithmClass' argument");
        }
        if (resultHandler == null) {
            throw new ValidationException(
                null,
                "Missing required component. "
                    + "Please specify '--resultClass' argument");
        }
    }

    private void readParam(
        String argName,
        ListIterator<String> argIter,
        Map<String, String> params,
        Component component)
    {
        if (argIter.hasNext()) {
            String paramName = argIter.next();
            if (argIter.hasNext()) {
                String paramValue = argIter.next();
                params.put(paramName, paramValue);
                return;
            }
        }
        throw new ValidationException(
            component,
            "Expected arguments <name> <value> following " + argName);
    }

    private <T> T readComponentClass(
        ListIterator<String> argIter,
        Class<T> iface)
    {
        if (!argIter.hasNext()) {
            throw new ValidationException(
                null,
                "Expected argument <className>");
        }
        String arg = argIter.next();
        try {
            final Class<?> clazz = Class.forName(arg);
            if (!iface.isAssignableFrom(clazz)) {
                throw new ValidationException(
                    null,
                    "Class '" + arg
                        + "' does not implement required interface '"
                        + iface.getName() + "'");
            }
            final Object o = clazz.newInstance();
            return iface.cast(o);
        } catch (ClassNotFoundException e) {
            throw new ValidationException(
                null,
                "Class '" + arg + "' not found");
        } catch (IllegalAccessException e) {
            throw new ValidationException(
                null,
                "Error while instantiating class '" + arg + "'");
        } catch (InstantiationException e) {
            throw new ValidationException(
                null,
                "Error while instantiating class '" + arg + "'");
        }
    }

    void run() {
        if (args.length == 0) {
            usage(null);
            return;
        }
        Map<Parameter, Object> loaderParams;
        Map<Parameter, Object> algorithmParams;
        Map<Parameter, Object> resultHandlerParams;
        try {
            parseArgs();

            // Validate all parameters up front, so we fail fast.
            loaderParams =
              ArgumentUtils.validateParameters(loader, loaderRawParams);
            algorithmParams =
              ArgumentUtils.validateParameters(algorithm, algorithmRawParams);
            resultHandlerParams =
              ArgumentUtils.validateParameters(resultHandler, resultHandlerRawParams);
        } catch (ValidationException e) {
            pw.println(e.getMessage());
            usage(e.getComponent());
            return;
        } catch (RuntimeException e) {
            usage(null);
            return;
        }

        final Schema schema = loader.createSchema(loaderParams);

        // Run the algorithm.
        final ArgumentUtils.TextProgress progress = new ArgumentUtils.TextProgress(pw);
        Result result = algorithm.run(schema, algorithmParams, progress);
        if (result == null) {
            System.out.println("Algorithm was canceled.");
            return;
        }

        // Process the results.
        resultHandler.handle(resultHandlerParams, schema, result);
    }

    /**
     * Converts spaces and punctuation to underscores.
     *
     * @param name Column identifier
     * @return identifier with punctuation removed
     */
    public static String depunctify(final String name) {
        String s = name.replaceAll("[\\[\\]\\. _]+", "_");
        s = s.replaceAll("^_", "");
        s = s.replaceAll("_$", "");
        return s;
    }
}

// End Main.java
