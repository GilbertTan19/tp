package quickcache.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import quickcache.commons.core.LogsCenter;
import quickcache.commons.exceptions.DataConversionException;
import quickcache.logic.commands.exceptions.CommandException;
import quickcache.model.Model;
import quickcache.model.ReadOnlyQuickCache;
import quickcache.model.flashcard.Flashcard;
import quickcache.storage.JsonQuickCacheStorage;
import quickcache.storage.QuickCacheStorage;

/**
 * Imports the set of flashcards from a specified file.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";
    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Imports the flashcards from a specified file into your local QuickCache. "
        + "Parameters: FILE_NAME\n"
        + "Example: " + COMMAND_WORD + " CS2103_Flashcards.json";
    public static final String MESSAGE_IMPORT_FLASHCARD_SUCCESS =
        "Flashcards from %1$s has been successfully imported.";
    public static final String MESSAGE_IMPORT_FLASHCARD_CORRUPTED_FILE_FAILURE =
        "Flashcards from %1$s are corrupted.";
    public static final String MESSAGE_IMPORT_FLASHCARD_EMPTY_FILE_FAILURE =
        "The file %1$s is empty or does not exist.";
    public static final String MESSAGE_IMPORT_FLASHCARD_ERROR_READING_FAILURE =
        "There was an error reading the file %1$s.";
    private static final Logger logger = LogsCenter.getLogger(ImportCommand.class);
    private final Path path;
    private final QuickCacheStorage storage;

    /**
     * Instantiates an import command.
     *
     * @param path {@code Path} representation of the destination file to import from.
     */
    public ImportCommand(Path path) {
        requireNonNull(path);
        this.path = path;
        this.storage = new JsonQuickCacheStorage(path);
    }

    /**
     * Instantiates an import command.
     *
     * @param path {@code Path} representation of the destination file to import from.
     * @param storage {@code QuickCacheStorage} to use for saving operations.
     */
    public ImportCommand(Path path, QuickCacheStorage storage) {
        requireNonNull(path);
        this.path = path;
        this.storage = storage;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            Optional<ReadOnlyQuickCache> readBack = storage.readQuickCache();
            if (readBack.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_IMPORT_FLASHCARD_EMPTY_FILE_FAILURE, path));
            }
            List<Flashcard> additionalFlashcards = readBack.get().getFlashcardList();
            additionalFlashcards.forEach((flashcard -> checkAndAddFlashcardToModel(flashcard, model)));
            return new CommandResult(String.format(MESSAGE_IMPORT_FLASHCARD_SUCCESS, path));
        } catch (DataConversionException dce) {
            logger.info(path + " is corrupted");
            throw new CommandException(String.format(MESSAGE_IMPORT_FLASHCARD_CORRUPTED_FILE_FAILURE, path));
        } catch (IOException ioe) {
            logger.info("Unable to read from " + path);
            throw new CommandException(String.format(MESSAGE_IMPORT_FLASHCARD_ERROR_READING_FAILURE, path));
        }
    }

    private void checkAndAddFlashcardToModel(Flashcard flashcard, Model model) {
        if (model.hasFlashcard(flashcard)) {
            return;
        }
        model.addFlashcard(flashcard);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ImportCommand that = (ImportCommand) object;
        return Objects.equals(path, that.path);
    }

}
