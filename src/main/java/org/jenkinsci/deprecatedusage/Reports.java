package org.jenkinsci.deprecatedusage;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Reports {
    private final DeprecatedApi deprecatedApi;
    private final Map<String, DeprecatedUsage> deprecatedUsageByPlugin;

    public Reports(DeprecatedApi deprecatedApi, Map<String, DeprecatedUsage> deprecatedUsageByPlugin) {
        super();
        this.deprecatedApi = deprecatedApi;
        this.deprecatedUsageByPlugin = deprecatedUsageByPlugin;
    }

    public void report() {
        log("deprecated classes in jenkins.war : " + format(deprecatedApi.getClasses()));
        log("");
        log("deprecated methods in jenkins.war : " + format(deprecatedApi.getMethods()));
        log("");
        log("deprecated fields in jenkins.war : " + format(deprecatedApi.getFields()));
        log("");

        final Set<String> deprecatedClassesUsed = new TreeSet<String>();
        final Set<String> deprecatedMethodsUsed = new TreeSet<String>();
        final Set<String> deprecatedFieldsUsed = new TreeSet<String>();
        for (final Map.Entry<String, DeprecatedUsage> entry : deprecatedUsageByPlugin.entrySet()) {
            final String plugin = entry.getKey();
            final DeprecatedUsage deprecatedUsage = entry.getValue();
            deprecatedClassesUsed.addAll(deprecatedUsage.getClasses());
            deprecatedMethodsUsed.addAll(deprecatedUsage.getMethods());
            deprecatedFieldsUsed.addAll(deprecatedUsage.getFields());

            log("deprecated api used in plugin " + plugin + " :");
            if (!deprecatedUsage.getClasses().isEmpty()) {
                log("   classes : " + format(deprecatedUsage.getClasses()));
            }
            if (!deprecatedUsage.getMethods().isEmpty()) {
                log("   methods : " + format(deprecatedUsage.getMethods()));
            }
            if (!deprecatedUsage.getFields().isEmpty()) {
                log("   fields : " + format(deprecatedUsage.getFields()));
            }
        }
        log("");

        log("deprecated classes used in plugins : " + format(deprecatedClassesUsed));
        log("");
        log("deprecated methods used in plugins : " + format(deprecatedMethodsUsed));
        log("");
        log("deprecated fields used in plugins : " + format(deprecatedFieldsUsed));
        log("");

        final Set<String> deprecatedClassesNotUsed = filterOnJenkins(deprecatedApi.getClasses());
        final Set<String> deprecatedMethodsNotUsed = filterOnJenkins(deprecatedApi.getMethods());
        final Set<String> deprecatedFieldsNotUsed = filterOnJenkins(deprecatedApi.getFields());
        deprecatedClassesNotUsed.removeAll(deprecatedClassesUsed);
        deprecatedMethodsNotUsed.removeAll(deprecatedMethodsUsed);
        deprecatedFieldsNotUsed.removeAll(deprecatedFieldsUsed);
        log("deprecated and public Jenkins classes not used in latest published plugins : "
                + format(deprecatedClassesNotUsed));
        log("");
        log("deprecated and public Jenkins methods not used in latest published plugins : "
                + format(deprecatedMethodsNotUsed));
        log("");
        log("deprecated and public Jenkins fields not used in latest published plugins : "
                + format(deprecatedFieldsNotUsed));
        log("");

        log("plugins using a deprecated api : "
                + new TreeSet<String>(deprecatedUsageByPlugin.keySet()));
        log("");

        log(deprecatedApi.getClasses().size() + " deprecated and public classes in jenkins.war");
        log(deprecatedApi.getMethods().size() + " deprecated and public methods in jenkins.war");
        log(deprecatedApi.getFields().size() + " deprecated and public fields in jenkins.war");
        log(deprecatedUsageByPlugin.size() + " plugins using a deprecated api");
        log(deprecatedClassesUsed.size() + " deprecated classes used in plugins");
        log(deprecatedMethodsUsed.size() + " deprecated methods used in plugins");
        log(deprecatedFieldsUsed.size() + " deprecated fields used in plugins");
        log(deprecatedClassesNotUsed.size()
                + " deprecated and public Jenkins classes not used in latest published plugins");
        log(deprecatedMethodsNotUsed.size()
                + " deprecated and public Jenkins methods not used in latest published plugins");
        log(deprecatedFieldsNotUsed.size()
                + " deprecated and public Jenkins fields not used in latest published plugins");
    }

    private static String format(Set<String> classesOrFieldsOrMethods) {
        // replace "org/mypackage/Myclass" by "org.mypackage.Myclass"
        return classesOrFieldsOrMethods.toString().replace('/', '.');
    }

    private static Set<String> filterOnJenkins(Set<String> classesOrFieldsOrMethods) {
        final Set<String> filtered = new LinkedHashSet<>();
        for (final String classOrFieldOrMethod : classesOrFieldsOrMethods) {
            if (classOrFieldOrMethod.startsWith("jenkins/")
                    || classOrFieldOrMethod.startsWith("hudson/")
                    || classOrFieldOrMethod.startsWith("org/kohsuke/")) {
                filtered.add(classOrFieldOrMethod);
            }
        }
        return filtered;
    }

    private static void log(String message) {
        Log.log(message);
    }
}