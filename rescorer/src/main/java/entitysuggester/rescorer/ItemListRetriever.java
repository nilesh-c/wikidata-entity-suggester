package entitysuggester.rescorer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.myrrix.common.random.MemoryIDMigrator;
import org.apache.mahout.common.iterator.FileLineIterable;

/**
 *
 * @author Nilesh Chakraborty
 */
public class ItemListRetriever {

    private MemoryIDMigrator idTranslator;

    ItemListRetriever(URI itemlistFileName) throws IOException {
        idTranslator = new MemoryIDMigrator();
        File f = new File(itemlistFileName);
        idTranslator.initialize(new FileLineIterable(f));
        System.out.println("Finished id translator init");
    }

    String getStringIDFor(long longID) {
        return idTranslator.toStringID(longID);
    }
}
