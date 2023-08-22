package src;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class CfgProperties extends Properties {

    public void update(File cfgFile) throws IOException {

        // Read the contents of the cfg file
        String content = new String(Files.readAllBytes(Paths.get(cfgFile.getAbsolutePath())));

        // Loop through all keys and values
        Enumeration<?> propertyNames = this.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            String value = this.getProperty(key);

            // Ignore possible included comments
            if (key.startsWith(";")) {
                continue;
            }

            // Create regex strings
            String regex = "(?m)^" + Pattern.quote(key) + "\\s*=\\s*.*?$";
            String replacement = key + "=" + value;

            // Check if the regex matches anything in the content
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                // Perform the replacement in the CFG data
                content = content.replaceAll(regex, replacement);
            } else {

                // Make sure the end line of already existing content is consistent
                if (!content.endsWith("\n")) {
                    content = content + "\n";
                }

                // Add the new CFG key
                content = content + "\n" + replacement + "\n";
                System.out.println("Added new CFG key: " + replacement);
            }

        }

        // Write the modified content back to the cfg file
        BufferedWriter writer = new BufferedWriter(new FileWriter(cfgFile.getAbsolutePath()));
        writer.write(content);
        writer.close();
    }

}
