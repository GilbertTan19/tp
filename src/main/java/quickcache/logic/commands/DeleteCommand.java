package quickcache.logic.commands;

import static java.util.Objects.requireNonNull;
import static quickcache.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import quickcache.commons.core.Messages;
import quickcache.commons.core.index.Index;
import quickcache.logic.commands.exceptions.CommandException;
import quickcache.model.Model;
import quickcache.model.flashcard.Flashcard;
import quickcache.model.flashcard.FlashcardPredicate;
import quickcache.model.flashcard.Tag;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes flashcards from flashcard list.\n"
            + "If an INDEX is given, then the flashcard identified by the index number is deleted from the displayed flashcard list.\n"
            + "If tag prefixes is given, the all flashcards with those tags will be deleted as well.\n"
            + "An error will be thrown if both index and tag prefixes are given together.\n"
            + "Parameters (either but not both): \n"
            + "1. INDEX (must be a positive integer)\n"
            + "2. [TAGS]\n"
            + "Examples: \n"
            + "1. " + COMMAND_WORD + " 1\n"
            + "2. " + COMMAND_WORD + " " + PREFIX_TAG + "MCQ";

    public static final String MESSAGE_DELETE_FLASHCARD_SUCCESS = "Deleted Flashcard: %1$s";

    private final Index targetIndex;
    private final FlashcardPredicate predicate;
    private final Set<Tag> tagsToMatch;
    private final boolean isDeleteByTag;

    private DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
        this.predicate = null;
        this.tagsToMatch = null;
        this.isDeleteByTag = false;
    }

    private DeleteCommand(FlashcardPredicate predicate, Set<Tag> tagsToMatch) {
        this.targetIndex = null;
        this.predicate = predicate;
        this.tagsToMatch = tagsToMatch;
        this.isDeleteByTag = true;
    }

    public static DeleteCommand withIndex(Index targetIndex) {
        return new DeleteCommand(targetIndex);
    }

    public static DeleteCommand withPredicate(FlashcardPredicate predicate, Set<Tag> tagsToMatch) {
        return new DeleteCommand(predicate, tagsToMatch);
    }

    /**
     * Create the delete result message for tag delete
     * @param tags the set of tags that are used for deletion
     * @return the result message
     */
    // Method is currently public static so that it can be used by DeleteCommandTest class
    // TODO: should this really be a public static method? is there a better way?
    public static String createDeleteWithTagsMessage(Set<Tag> tags) {
        StringBuilder sb = new StringBuilder("with tags ");
        assert tags != null;
        tags.forEach(tag -> sb.append(tag.toString()).append(" "));
        return sb.toString().trim();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (isDeleteByTag) {
            assert(predicate != null && tagsToMatch != null && !tagsToMatch.isEmpty());
            model.updateFilteredFlashcardList(predicate);
            List<Flashcard> filteredList = model.getFilteredFlashcardList();

            // filtered list is a mutable list and the indices change with every model.deleteFlashcard execution
            // hence a for loop will not possible. A workaround would be to use a while loop
            while (!filteredList.isEmpty()) {
                Flashcard flashcardToDelete = filteredList.get(0);
                model.deleteFlashcard(flashcardToDelete);
            }

            String deleteWithTagsMessage = createDeleteWithTagsMessage(tagsToMatch);
            return new CommandResult(String.format(MESSAGE_DELETE_FLASHCARD_SUCCESS, deleteWithTagsMessage));
        } else {
            List<Flashcard> lastShownList = model.getFilteredFlashcardList();
            assert(targetIndex != null);

            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_FLASHCARD_DISPLAYED_INDEX);
            }

            Flashcard flashcardToDelete = lastShownList.get(targetIndex.getZeroBased());
            model.deleteFlashcard(flashcardToDelete);
            return new CommandResult(String.format(MESSAGE_DELETE_FLASHCARD_SUCCESS, flashcardToDelete));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
