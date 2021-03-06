package club.bytecode.the.jda;

import org.objectweb.asm.tree.ClassNode;

/**
 * Used to represent whenever a file has been opened
 *
 * @author Konloch
 */

public interface FileChangeNotifier {
    void openClassFile(String name, FileContainer container, ClassNode cn);

    void openFile(String name, FileContainer container, byte[] contents);
}
