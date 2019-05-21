import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class Tester {

    private File startDir;
    private final int DEPTH = 10;
    private final int TYPICAL = 4;
    private final int NON_TYPICAL = 2;
    private final int TO_PROCESS = TYPICAL * 2 + NON_TYPICAL;
    private final int EXPECTED = DEPTH * TO_PROCESS;

    @Before
    public void before() {
        startDir = new File("temp" + System.currentTimeMillis());
        assertTrue(startDir.mkdir());
    }

    @After
    public void after() throws IOException {
        if (startDir.exists()) {
            try (Stream<Path> walk = Files.walk(startDir.toPath())) {
                walk.sorted(Collections.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    @Test
    public void test() throws IOException {
        makeTestFiles();
        RenameTool.rename(startDir.toString(), RenameTool.directFilter, RenameTool::directRenameFunc);
        try (Stream<Path> walk = Files.walk(startDir.toPath())) {
            List<Path> renamed = walk.filter(RenameTool.commonFilter)
                    .filter(RenameTool.reversedFilter)
                    .collect(Collectors.toList());

            assertEquals(EXPECTED, renamed.size());
        }
    }

    private void makeTestFiles() throws IOException {
        File curDir = startDir;
        for (int i = 0; i < DEPTH; i++) {
            for (int j = 0; j < TYPICAL; j++) {
                File javaFile = new File(curDir, j + System.currentTimeMillis() + ".java");
                File kotlinFile = new File(curDir, j + System.currentTimeMillis() + ".kt");
                assertTrue(javaFile.createNewFile());
                assertTrue(kotlinFile.createNewFile());
            }

            File nonTypicalJava = new File(curDir, ".java");
            File nonTypicalKotlin = new File(curDir, ".kt");

            File commonFile1 = new File(curDir, System.currentTimeMillis() + ".txt");
            File commonFile2 = new File(curDir, String.valueOf(System.currentTimeMillis()));
            assertTrue(nonTypicalJava.createNewFile());
            assertTrue(nonTypicalKotlin.createNewFile());
            assertTrue(commonFile1.createNewFile());
            assertTrue(commonFile2.createNewFile());

            File childDir = new File(curDir, "temp" + System.currentTimeMillis());
            assertTrue(childDir.mkdir());
            curDir = childDir;
        }
    }

}
