package club.bytecode.the.jda.decompilers;

import club.bytecode.the.jda.FileContainer;
import club.bytecode.the.jda.JDA;
import club.bytecode.the.jda.api.ExceptionUI;
import club.bytecode.the.jda.settings.JDADecompilerSettings;
import org.objectweb.asm.tree.ClassNode;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Used to represent all of the decompilers/disassemblers BCV contains.
 *
 * @author Konloch
 */

public abstract class JDADecompiler {
    protected JDADecompilerSettings settings = new JDADecompilerSettings(this);

    public abstract String decompileClassNode(FileContainer container, ClassNode cn);

    public abstract void decompileToZip(String zipName);

    public abstract String getName();

    public JDADecompilerSettings getSettings() {
        return settings;
    }

    protected String parseException(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        e.printStackTrace();
        String exception = "JDA v" + JDA.version + JDA.nl + JDA.nl + sw.toString();
        return getName() + " encountered a problem! Send the stacktrace to https://github.com/ecx86/jda/issues" + JDA.nl +
                JDA.nl +
                "Suggested Fix: Click refresh class, if it fails again try another decompiler." + JDA.nl +
                JDA.nl +
                exception;
    }

    protected void handleException(Exception e) {
        new ExceptionUI(e);
    }

    public static void ensureInitted() {
        // Just to make sure the classes is loaded so all decompilers are loaded
    }
}
