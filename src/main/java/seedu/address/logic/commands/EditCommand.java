package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANSWER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CHOICE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUESTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FLASHCARDS;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.flashcard.Answer;
import seedu.address.flashcard.Flashcard;
import seedu.address.flashcard.MultipleChoiceQuestion;
import seedu.address.flashcard.OpenEndedQuestion;
import seedu.address.flashcard.Question;
import seedu.address.flashcard.Tag;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;


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
            throw new CommandException(MESSAGE_DUPLICATE_FLASHCARD);
        }

        model.setFlashcard(flashcardToEdit, editedFlashcard);
        model.updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARDS);
        return new CommandResult(String.format(MESSAGE_EDIT_FLASHCARD_SUCCESS, editedFlashcard));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Flashcard createEditedFlashcard(Flashcard flashcardToEdit,
                                                   EditFlashcardDescriptor editFlashcardDescriptor)
            throws CommandException {
        assert flashcardToEdit != null;

        boolean isMcq = flashcardToEdit.getQuestion() instanceof MultipleChoiceQuestion;

        Answer updatedAnswer = editFlashcardDescriptor.getAnswer()
                .orElse(new Answer(flashcardToEdit.getAnswer().getAnswer()));
        Question updatedQuestion = editFlashcardDescriptor.getQuestion()
                .orElse(new OpenEndedQuestion(flashcardToEdit.getQuestion().getOnlyQuestion()));
        Set<Tag> updatedTags = editFlashcardDescriptor.getTags().orElse(flashcardToEdit.getTags());
        String[] emptyArray = new String[0];
        String[] updatedChoices = editFlashcardDescriptor.getChoices().orElse(emptyArray);
        boolean isMcqEdit = editFlashcardDescriptor.getIsMcq();
        if (isMcq) {
            String question = updatedQuestion.getQuestion();
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) flashcardToEdit.getQuestion();
            String[] choices = mcq.getChoices();
            if (Arrays.equals(updatedChoices, emptyArray)) {
                updatedQuestion = new MultipleChoiceQuestion(question, choices);
                int ans;
                try {
                    ans = Integer.parseInt(updatedAnswer.getAnswer());
                    if (ans > choices.length) {
                        throw new CommandException("Answer must be smaller than number of choices");
                    }
                } catch (NumberFormatException e) {
                    MultipleChoiceQuestion temp = (MultipleChoiceQuestion) updatedQuestion;
                    ans = temp.getAnswerIndex(updatedAnswer.getAnswer());
                    if (ans == -1) {
                        throw new CommandException("Answer must be integer");
                    }
                }
                updatedAnswer = new Answer(choices[ans - 1]);
            } else {
                int ans;
                updatedQuestion = new MultipleChoiceQuestion(question, updatedChoices);
                try {
                    ans = Integer.parseInt(updatedAnswer.getAnswer());
                    if (ans > updatedChoices.length) {
                        throw new CommandException("Number of choices must be larger than answer");
                    }
                } catch (NumberFormatException e) {
                    MultipleChoiceQuestion temp = new MultipleChoiceQuestion(question, choices);
                    ans = temp.getAnswerIndex(updatedAnswer.getAnswer());
                    if (ans == 0) {
                        throw new CommandException("Answer must be integer");
                    }
                }
                updatedAnswer = new Answer(updatedChoices[ans - 1]);
            }
        } else {
            if (!Arrays.equals(updatedChoices, emptyArray)) {
                throw new CommandException(MESSAGE_DIFFERENT_TYPE);
            }
        }
        return new Flashcard(updatedQuestion, updatedAnswer, updatedTags);
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
        private Question question;
        private String[] choices;
        private Set<Tag> tags;
        private boolean isMcq;


        public EditFlashcardDescriptor(boolean isMcq) {
            this.isMcq = isMcq;
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
            setIsMcq(toCopy.isMcq);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(answer, question, choices, tags);
        }

        public void setAnswer(Answer answer) {
            this.answer = answer;
        }

        public Optional<Answer> getAnswer() {
            return Optional.ofNullable(answer);
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public Optional<Question> getQuestion() {
            return Optional.ofNullable(question);
        }

        public boolean getIsMcq() {
            return isMcq;
        }

        public void setIsMcq(Boolean isMcq) {
            this.isMcq = isMcq;
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
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
         * Sets {@code choices} to this object's {@code choices}.
         */
        public void setChoices(String[] choices) {
            this.choices = (choices != null) ? choices : null;
        }

        /**
         * Returns an unmodifiable String array.
         * Returns {@code Optional#empty()} if {@code choices} is null.
         */
        public Optional<String[]> getChoices() {
            return (choices != null) ? Optional.of(choices) : Optional.empty();
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
                    && getTags().equals(e.getTags());
        }
    }
}
