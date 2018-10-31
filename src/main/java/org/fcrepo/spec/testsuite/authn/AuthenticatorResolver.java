/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.spec.testsuite.authn;

import static org.fcrepo.spec.testsuite.TestParameters.AUTHENTICATOR_CLASS_PARAM;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * @author dbernstein
 */
public class AuthenticatorResolver {

    private static String authenticatorClass = null;

    private static Authenticator instance = null;

    private AuthenticatorResolver() {
    }

    /**
     * Initialize the resolver
     *
     * @param authenticatorClazz Optionally specify the class name of the Authenticator implementation.
     */
    public static void initialize(final String authenticatorClazz) {
        authenticatorClass = authenticatorClazz;
        instance = findAuthenticator();
    }

    private static Authenticator findAuthenticator() {
        try (PrintStream log = TestSuiteGlobals.logFile()) {

            final List<ClassLoader> classLoadersList = new LinkedList<>();
            final String path =
                AuthenticatorResolver.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            final File jar = new File(path);

            final String authDirName = "authenticators";

            //look for jars an authenticators directory sitting in the same
            //directory as the executing jar.
            File dir = new File(jar.getParentFile(), authDirName);
            //look for an authenticators dir where the executing jar lives.
            if (!dir.exists()) {
                dir = null;
            }

            //if a valid directory was found, try to find an implementation of the Authenticator
            if (dir != null) {
                final List<File> jars =
                    Arrays.asList(
                        dir.listFiles((FilenameFilter) FileFilterUtils.suffixFileFilter(".jar", IOCase.INSENSITIVE)));
                jars.stream().forEach(x -> {
                    try {
                        final URLClassLoader cl = URLClassLoader.newInstance(new URL[] {x.toURI().toURL()});
                        classLoadersList.add(cl);
                    } catch (MalformedURLException ex) {
                        //Should never happen
                        throw new RuntimeException(ex);
                    }
                });

                final Reflections reflections = new Reflections(new ConfigurationBuilder()
                                                                    .setScanners(new SubTypesScanner())
                                                                    .addClassLoaders(
                                                                        classLoadersList.toArray(new ClassLoader[0]))
                                                                    .setUrls(ClasspathHelper.forClassLoader(
                                                                        classLoadersList.toArray(new ClassLoader[0]))));

                final List<Class<? extends Authenticator>> authClasses =
                    reflections.getSubTypesOf(Authenticator.class).stream()
                               .filter(x -> !x.equals(DefaultAuthenticator.class)).collect(Collectors.toList());

                Class authClass = null;
                //if the user supplied an authenticator class, ensure that it matches one in the list
                if (authenticatorClass != null) {
                    authClass = authClasses.stream().filter(x -> x.getName().equals(authenticatorClass)).findFirst()
                                           .orElse(null);

                    if (authClass == null) {
                        //otherwise throw an exception
                        final StringBuilder message = new StringBuilder();
                        message.append("The Authenticator implementation you specified, " + authenticatorClass +
                                       " was not found on the class path.\n");
                        if (authClasses.size() > 0) {
                            message.append("We did however find the following implementations:\n");
                            authClasses.stream().forEach(x -> message.append(" * " + x.getName() + "\n"));
                        }
                        message.append(
                            "Please verify that your classname is correct and that you've packaged it in a jar that " +
                            "you have placed in '" + jar.getParentFile().getPath()  + File.separator + authDirName +
                            "'. Otherwise you may opt not to specify an Authenticator class. In that case if there " +
                            "is a single Authenticator found we will use that one, otherwise we will use the default " +
                            "authenticator.");
                        throw new RuntimeException(message.toString());
                    }

                } else {
                    //if no user supplied authenticator was specified, ensure there is only one
                    //that was resolved
                    if (authClasses.size() == 1) {
                        authClass = authClasses.get(0);
                    } else if (authClasses.size() > 1) {
                        //otherwise throw an exception
                        final StringBuilder message = new StringBuilder();
                        message.append("We found more than one Authenticator implementations in your classpath.\n");
                        message.append(
                            "Please specify one of the following class names in the \"" +
                            AUTHENTICATOR_CLASS_PARAM + "\" parameter:\n");
                        authClasses.stream().forEach(x -> message.append(" * " + x.getName() + "\n"));
                        throw new RuntimeException(message.toString());
                    }
                }

                //if an auth class was resolved create a new instance
                if (authClass != null) {
                    log.println(
                        "Using user specified " + Authenticator.class + " implementation: " + authClass.getName() +
                        "\n\n");
                    return (Authenticator) authClass.newInstance();
                }
            }

            //no auth class was found
            log.println(
                "No " + Authenticator.class + " implementation found in classpath. Using default: " +
                DefaultAuthenticator.class.getName() + "\n\n");

            return new DefaultAuthenticator();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the authenticator
     *
     * @return the configured authenticator instance
     */
    public static Authenticator getAuthenticator() {
        if (instance == null) {
            throw new RuntimeException("You must all initialize() on this class before accessing the Authenticator.");
        }
        return instance;
    }
}
