import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RenameTool {

    public static Predicate<Path> commonFilter = path -> path.toFile().isFile();
    public static Predicate<Path> directFilter = path -> path.toString().endsWith(".java") | path.toString().endsWith(".kt");
    public static Predicate<Path> reversedFilter = path -> path.toString().endsWith(".2019");

    private static void printMessage(boolean renameTo, Path oldPath, Path newPath) {
        if (renameTo) {
            System.out.println(newPath);
        } else {
            System.out.println("Can't rename " + oldPath);
        }
    }

    public static void directRenameFunc(Path path) {
        String newName = path.toString();
        boolean renameTo = path.toFile().renameTo(new File(newName + ".2019"));
        Path newPath = Paths.get(newName + ".2019");
        printMessage(renameTo, path, newPath);
    }

    private static void reversedRenameFunc(Path path) {
        int index = path.toString().lastIndexOf(".2019");
        String newName = path.toString().substring(0, index);
        path.toFile().renameTo(new File(newName));
    }

    public static void rename(String dir, Predicate<Path> filter, Consumer<Path> mode) {
        try (Stream<Path> walk = Files.walk(Paths.get(dir))) {
            walk.filter(commonFilter)
                    .filter(filter)
                    .forEach(mode);
        } catch (SecurityException e) {
            System.err.println("Security manager denied access to the file" + e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println("Can't read the file" + e.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("Need starting directory for renaming process");
        } else if (args[0] == null) {
            System.out.println("Starting directory can't be null");
        } else if (!Paths.get(args[0]).toFile().isDirectory()) {
            System.out.println("Need directory to start");
        } else if (args.length == 1) {
            rename(args[0], directFilter, RenameTool::directRenameFunc);
        } else {
            rename(args[0], reversedFilter, RenameTool::reversedRenameFunc);
        }
    }
}
