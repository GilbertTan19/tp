package quickcache.logic.commands;

import static java.util.Objects.requireNonNull;
import static quickcache.logic.parser.CliSyntax.PREFIX_ANSWER;
import static quickcache.logic.parser.CliSyntax.PREFIX_CHOICE;
import static quickcache.logic.parser.CliSyntax.PREFIX_QUESTION;
import static quickcache.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import quickcache.commons.core.LogsCenter;
import quickcache.commons.core.Messages;
import quickcache.commons.core.index.Index;
import quickcache.commons.util.CollectionUtil;
import quickcache.logic.commands.exceptions.CommandException;
import quickcache.model.Model;
import quickcache.model.flashcard.Answer;
import quickcache.model.flashcard.Choice;
import quickcache.model.flashcard.Difficulty;
import quickcache.model.flashcard.Flashcard;
import quickcache.model.flashcard.MultipleChoiceQuestion;
import quickcache.model.flashcard.OpenEndedQuestion;
import quickcache.model.flashcard.Question;
import quickcache.model.flashcard.Statistics;
import quickcache.model.flashcard.Tag;


/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_QUESTION + "QUESTION] "
            + "[" + PREFIX_ANSWER + "ANSWER] "
            + "[" + PREFIX_CHOICE + "CHOICE]...\n"
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_QUESTION + "New Question "
            + PREFIX_ANSWER + "New Answer";

    public static final String MESSAGE_EDIT_FLASHCARD_SUCCESS = "Edited Flashcard: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_FLASHCARD = "This Flashcard already exists in the address book.";
    public static final String MESSAGE_DIFFERENT_TYPE = "The question do not have choices";
    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditFlashcardDescriptor editFlashcardDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editFlashcardDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditFlashcardDescriptor editFlashcardDescriptor) {
        requireNonNull(index);
        requireNonNull(editFlashcardDescriptor);

        this.index = index;
        this.editFlashcardDescriptor = new EditFlashcardDescriptor(editFlashcardDescriptor);
    }

    /**
     * Creates and returns a {@code Flashcard} with the details of {@code flashcardToEdit}
     * edited with {@code editFlashcardDescriptor}.
     */
    private static Flashcard createEditedFlashcard(Flashcard flashcardToEdit,
                                                   EditFlashcardDescriptor editFlashcardDescriptor)
            throws CommandException {
        assert flashcardToEdit != null;

        boolean isMcq = flashcardToEdit.getQuestion() instanceof MultipleChoiceQuestion;

        Statistics statistics = flashcardToEdit.getStatistics();
        Question previousQuestion = flashcardToEdit.getQuestion();
        Answer updatedAnswer = editFlashcardDescriptor.getAnswer()
                .orElse(flashcardToEdit.getAnswerOrIndex());
        String updatedQuestion = editFlashcardDescriptor.getQuestion()
                .orElse(flashcardToEdit.getQuestion().getValue());
        Set<Tag> updatedTags = editFlashcardDescriptor.getTags().orElse(flashcardToEdit.getTags());
        Difficulty difficulty = editFlashcardDescriptor.getDifficulty()
                .orElse(flashcardToEdit.getDifficulty());
        Choice[] updatedChoices;
        Question finalQuestion;
        if (isMcq) {
            updatedChoices = editFlashcardDescriptor.getChoices()
                    .orElse(flashcardToEdit.getQuestion().getChoices().get());
            int ans;
            try {
                ans = Integer.parseInt(updatedAnswer.getValue());
                if (ans > updatedChoices.length) {
                    throw new CommandException("Answer must be smaller than number of choices");
                }
            } catch (NumberFormatException e) {
                logger.info("Answer is not integer" + updatedAnswer.getValue());
                throw new CommandException("Answer must be integer");
            }
            Answer finalAnswer = new Answer(updatedChoices[ans - 1].getValue());
            finalQuestion = new MultipleChoiceQuestion(updatedQuestion, finalAnswer, updatedChoices);
        } else {
            finalQuestion = new OpenEndedQuestion(updatedQuestion, updatedAnswer);
        }
        return new Flashcard(finalQuestion, updatedTags, difficulty, statistics);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Flashcard> lastShownList = model.getFilteredFlashcardList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FLASHCARD_DISPLAYED_INDEX);
        }

        Flashcard flashcardToEdit = lastShownList.get(index.getZeroBased());
        Flashcard editedFlashcard = createEditedFlashcard(flashcardToEdit, editFlashcardDescriptor);

        if (flashcardToEdit.isSameFlashcard(editedFlashcard) || model.hasFlashcard(editedFlashcard)) {
            logger.info("Edited flashcard is already inside the QuickCache");
            throw new CommandException(MESSAGE_DUPLICATE_FLASHCARD);
        }

        model.setFlashcard(flashcardToEdit, editedFlashcard);
        model.updateFilteredFlashcardList(Model.PREDICATE_SHOW_ALL_FLASHCARDS);
        return new CommandResult(String.format(MESSAGE_EDIT_FLASHCARD_SUCCESS, editedFlashcard));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editFlashcardDescriptor.equals(e.editFlashcardDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditFlashcardDescriptor {
        private Answer answer;
        private String question;
        private Choice[] choices;
        private Set<Tag> tags;
        private Difficulty difficulty;



        public EditFlashcardDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditFlashcardDescriptor(EditFlashcardDescriptor toCopy) {
            setAnswer(toCopy.answer);
            setQuestion(toCopy.question);
            setChoices(toCopy.choices);
            setTags(toCopy.tags);
            setDifficulty(toCopy.difficulty);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(answer, question, choices, tags, difficulty);
        }

        public Optional<Answer> getAnswer() {
            return Optional.ofNullable(answer);
        }

        public void setAnswer(Answer answer) {
            this.answer = answer;
        }

        public Optional<String> getQuestion() {
            return Optional.ofNullable(question);
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Optional<Difficulty> getDifficulty() {
            return Optional.ofNullable(difficulty);
        }

        public void setDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
        }


        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable String array.
         * Returns {@code Optional#empty()} if {@code choices} is null.
         */
        public Optional<Choice[]> getChoices() {
            return (choices != null) ? Optional.of(choices) : Optional.empty();
        }

        /**
         * Sets {@code choices} to this object's {@code choices}.
         */
        public void setChoices(Choice[] choices) {
            this.choices = choices;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditFlashcardDescriptor)) {
                return false;
            }

            // state check
            EditFlashcardDescriptor e = (EditFlashcardDescriptor) other;

            return getAnswer().equals(e.getAnswer())
                    && getQuestion().equals(e.getQuestion())
                    && getChoices().equals(e.getChoices())
                    && getTags().equals(e.getTags())
                    && getDifficulty().equals(e.getDifficulty());
        }
    }
}
