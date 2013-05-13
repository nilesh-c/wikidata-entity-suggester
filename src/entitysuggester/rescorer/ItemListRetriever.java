package entitysuggester.rescorer;

import java.io.File;
import java.io.IOException;
import net.myrrix.common.random.MemoryIDMigrator;
import org.apache.mahout.common.iterator.FileLineIterable;

/**
 *
 * @author Nilesh Chakraborty
 */
public class ItemListRetriever {
    private MemoryIDMigrator idTranslator;
    
    ItemListRetriever(String itemlistFileName) throws IOException {
        idTranslator = new MemoryIDMigrator();
        File f = new File(itemlistFileName);
        if(f.exists()) {
            idTranslator.initialize(new FileLineIterable(f));
            System.out.println("Finished id translator init");
        } else {
            throw new RuntimeException("propfile doesn't exist!");
        }
    }
    
    String getStringIDFor(long longID) {
        return idTranslator.toStringID(longID);
    }
    
}
