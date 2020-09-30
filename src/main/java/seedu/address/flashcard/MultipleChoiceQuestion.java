package seedu.address.flashcard;

import java.util.Arrays;

import seedu.address.commons.core.index.Index;

/**
 * Represents a mcq question.
 * Options for the mcq will be stored in an array while the
 * question itself will be stored as a {@code String}.
 */
public class MultipleChoiceQuestion implements Question {

    public static final String VALIDATION_REGEX = "[^\\s].*";

    public static final String MESSAGE_CONSTRAINTS = "MultipleChoiceQuestion can take any values, "
            + "and it should not be blank";

    private final String[] options;
    private final String question;

    /**
     * A constructor to create MCQ Question object.
     */
    public MultipleChoiceQuestion(String question, String... options) {
        this.question = question;
        this.options = options;
    }

    @Override
    public String getQuestion() {
        StringBuilder sb = new StringBuilder(question + "\n");
        int i = 1;
        for (String option : options) {
            sb.append(i).append(". ").append(option).append("\n");
            i++;
        }
        return sb.toString();
    }

    public String getOnlyQuestion() {
        StringBuilder sb = new StringBuilder(question);
        return sb.toString();
    }

    public int getAnswerIndex(String answer) {
        for (int i = 0; i < options.length; i++) {
            if (answer.equals(options[i])) {
                return i + 1;
            }
        }
        return 0;
    }

    public String[] getChoices() {
        return this.options;
    }

    public static boolean isValidQuestion(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Retrieves the option from {@code index} as an Answer.
     * @param index to get option from.
     * @return option as an {@code Answer}.
     */
    public Answer getAnswerFromIndex(Index index) {
        String option = options[index.getZeroBased()];
        return new Answer(option);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion temp = (MultipleChoiceQuestion) o;
            return this.toString().equals(temp.toString())
                    && Arrays.equals(this.getChoices(), temp.getChoices());
        }
        return false;
    }

    public String toString() {
        return question;
    }
}
