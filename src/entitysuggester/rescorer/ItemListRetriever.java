/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysuggester.rescorer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;

/**
 *
 * @author nilesh
 */
public class ItemListRetriever {
    private MemoryIDMigrator idTranslator;
    
    ItemListRetriever(String itemlistFileName) throws IOException {
        idTranslator = new MemoryIDMigrator();
        List<String> lines = Files.readAllLines(Paths.get(itemlistFileName), StandardCharsets.US_ASCII);
        idTranslator.initialize(lines);
    }
    
    String getStringIDFor(long longID) {
        return idTranslator.toStringID(longID);
    }
    
}
