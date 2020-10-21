package quickcache.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static quickcache.logic.commands.CommandTestUtil.assertCommandFailure;
import static quickcache.logic.commands.CommandTestUtil.assertCommandSuccess;
import static quickcache.testutil.TypicalFlashcards.RANDOM1;
import static quickcache.testutil.TypicalFlashcards.RANDOM2;
import static quickcache.testutil.TypicalFlashcards.getTypicalQuickCache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quickcache.commons.exceptions.DataConversionException;
import quickcache.model.Model;
import quickcache.model.ModelManager;
import quickcache.model.QuickCache;
import quickcache.model.ReadOnlyQuickCache;
import quickcache.model.UserPrefs;
import quickcache.model.flashcard.Flashcard;
import quickcache.storage.JsonQuickCacheStorage;
import quickcache.storage.QuickCacheStorage;
import quickcache.testutil.FlashcardBuilder;
import quickcache.testutil.TestUtil;

class ImportCommandTest {

    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");
    private static final DataConversionException DUMMY_DATA_CONVERSION_EXCEPTION =
        new DataConversionException(DUMMY_IO_EXCEPTION);
    private static final String TEST_FILE_NAME = "test_import.json";
    @TempDir
    public Path temporaryFolder;
    private final Model model = new ModelManager(getTypicalQuickCache(), new UserPrefs());

    @Test
    public void execute_importSameFlashcardSet_success() throws Exception {
        // Set up the export file
        Path testFilePath = TestUtil.getFilePathInSandboxFolder(TEST_FILE_NAME);
        ExportCommand exportCommand = new ExportCommand(testFilePath);
        exportCommand.execute(model);

        // Set up expected model
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());

        // Import the same list
        ImportCommand importCommand = new ImportCommand(testFilePath);
        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_FLASHCARD_SUCCESS, testFilePath);
        assertCommandSuccess(importCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_importNewSingleFlashcard_success() throws Exception {
        // Set up the export file
        Path testFilePath = TestUtil.getFilePathInSandboxFolder(TEST_FILE_NAME);
        ExportCommand exportCommand = new ExportCommand(testFilePath);

        // Set up single import
        QuickCache exportQuickCache = new QuickCache();

        // New flashcard
        Flashcard flashcard = new FlashcardBuilder(RANDOM1).withQuestion(RANDOM2.getQuestion().toString()).build();
        exportQuickCache.addFlashcard(flashcard);
        Model exportModel = new ModelManager(exportQuickCache, new UserPrefs());
        exportCommand.execute(exportModel);

        // Set up expected model
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());
        expectedModel.addFlashcard(flashcard);

        // Import the single flashcard
        ImportCommand importCommand = new ImportCommand(testFilePath);
        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_FLASHCARD_SUCCESS, testFilePath);
        assertCommandSuccess(importCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_importEmpty_success() throws Exception {
        // Set up the export file
        Path testFilePath = TestUtil.getFilePathInSandboxFolder(TEST_FILE_NAME);
        ExportCommand exportCommand = new ExportCommand(testFilePath);

        // Set up empty import
        QuickCache exportQuickCache = new QuickCache();

        // New flashcard
        Model exportModel = new ModelManager(exportQuickCache, new UserPrefs());
        exportCommand.execute(exportModel);

        // Set up expected model
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());

        // Import the single flashcard
        ImportCommand importCommand = new ImportCommand(testFilePath);
        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_FLASHCARD_SUCCESS, testFilePath);
        assertCommandSuccess(importCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_storageReadBackEmpty_throwsCommandException() {
        // Setup with JsonQuickCacheEmptyReadBackStub
        Path invalidTestFilePath = temporaryFolder.resolve("ioExceptionQuickCache.json");
        QuickCacheStorage quickCacheStorage = new JsonQuickCacheEmptyReadBackStub(invalidTestFilePath);

        // Execute import command
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());
        ImportCommand importCommand = new ImportCommand(invalidTestFilePath, quickCacheStorage);
        String expectedMessage = String.format(
            ImportCommand.MESSAGE_IMPORT_FLASHCARD_EMPTY_FILE_FAILURE, invalidTestFilePath);
        assertCommandFailure(importCommand, expectedModel, expectedMessage);

    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        // Setup with QuickCacheIoExceptionThrowingStub
        Path invalidTestFilePath = temporaryFolder.resolve("ioExceptionQuickCache.json");
        QuickCacheStorage quickCacheStorage = new QuickCacheIoExceptionThrowingStub(invalidTestFilePath);

        // Execute import command
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());
        ImportCommand importCommand = new ImportCommand(invalidTestFilePath, quickCacheStorage);
        String expectedMessage = String.format(
            ImportCommand.MESSAGE_IMPORT_FLASHCARD_ERROR_READING_FAILURE, invalidTestFilePath);
        assertCommandFailure(importCommand, expectedModel, expectedMessage);
    }

    @Test
    public void execute_storageThrowsDateConversionException_throwsCommandException() {
        // Setup with JsonQuickCacheDataConversionExceptionThrowingStub
        Path invalidTestFilePath = temporaryFolder.resolve("ioExceptionQuickCache.json");
        QuickCacheStorage quickCacheStorage =
            new JsonQuickCacheDataConversionExceptionThrowingStub(invalidTestFilePath);

        // Execute import command
        Model expectedModel = new ModelManager(getTypicalQuickCache(), new UserPrefs());
        ImportCommand importCommand = new ImportCommand(invalidTestFilePath, quickCacheStorage);
        String expectedMessage = String.format(
            ImportCommand.MESSAGE_IMPORT_FLASHCARD_CORRUPTED_FILE_FAILURE, invalidTestFilePath);
        assertCommandFailure(importCommand, expectedModel, expectedMessage);
    }

    @Test
    public void equals() {
        String fileNameSecond = "non_test_import.json";

        Path testFilePathFirst = TestUtil.getFilePathInSandboxFolder(TEST_FILE_NAME);
        Path testFilePathSecond = TestUtil.getFilePathInSandboxFolder(fileNameSecond);
        ImportCommand importFirstCommand = new ImportCommand(testFilePathFirst);
        ImportCommand importSecondCommand = new ImportCommand(testFilePathSecond);

        // same object -> returns true
        assertTrue(importFirstCommand.equals(importFirstCommand));

        // same values -> returns true
        ImportCommand importFirstCommandCopy = new ImportCommand(testFilePathFirst);
        assertTrue(importFirstCommand.equals(importFirstCommandCopy));

        // different types -> returns false
        assertFalse(importFirstCommand.equals(1));

        // null -> returns false
        assertFalse(importFirstCommand.equals(null));

        // different path -> returns false
        assertFalse(importFirstCommand.equals(importSecondCommand));
    }

    private static class JsonQuickCacheEmptyReadBackStub extends JsonQuickCacheStorage {

        public JsonQuickCacheEmptyReadBackStub(Path filePath) {
            super(filePath);
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache() {
            return Optional.empty();
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache(Path filePath) {
            return Optional.empty();
        }
    }

    /**
     * A stub class to throw an {@code IOException} when the read method is called.
     */
    private static class QuickCacheIoExceptionThrowingStub implements QuickCacheStorage {

        private Path filePath;

        private QuickCacheIoExceptionThrowingStub(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache(Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache() throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }

        @Override
        public void saveQuickCache(ReadOnlyQuickCache quickCache) throws IOException {

        }

        @Override
        public void saveQuickCache(ReadOnlyQuickCache quickCache, Path filePath) throws IOException {

        }

        @Override
        public Path getQuickCacheFilePath() {
            return null;
        }

    }

    /**
     * A stub class to throw a {@code DateConversionException} when the read method is called.
     */
    private static class JsonQuickCacheDataConversionExceptionThrowingStub extends JsonQuickCacheStorage {

        private JsonQuickCacheDataConversionExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache() throws DataConversionException {
            throw DUMMY_DATA_CONVERSION_EXCEPTION;
        }

        @Override
        public Optional<ReadOnlyQuickCache> readQuickCache(Path filePath) throws DataConversionException {
            throw DUMMY_DATA_CONVERSION_EXCEPTION;
        }
    }
}
