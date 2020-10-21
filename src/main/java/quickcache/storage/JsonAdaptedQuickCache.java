package quickcache.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import quickcache.commons.exceptions.IllegalValueException;
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
 * Jackson-friendly version of {@link Flashcard}.
 */
class JsonAdaptedQuickCache {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Flashcard's %s field is missing!";
    public static final String INVALID_TYPE = "Invalid flashcard type!";

    private final String type;
    private final String question;
    private final List<String> choices;
    private final String answer;
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();
    private final String difficulty;
    private final Statistics statistics;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedQuickCache(@JsonProperty("type") String type,
                                 @JsonProperty("question") String question,
                                 @JsonProperty("choices") List<String> choices,
                                 @JsonProperty("answer") String answer,
                                 @JsonProperty("tagged") List<JsonAdaptedTag> tagged,
                                 @JsonProperty("difficulty") String difficulty,
                                 @JsonProperty("statistics") Statistics statistics) {
        this.type = type;
        this.question = question;
        this.choices = choices;
        this.answer = answer;
        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
        this.difficulty = difficulty;
        this.statistics = statistics;
    }

    /**
     * Converts a given {@code Flashcard} into this class for Jackson use.
     */
    public JsonAdaptedQuickCache(Flashcard source) {

        if (source.getQuestion() instanceof MultipleChoiceQuestion) {
            this.type = MultipleChoiceQuestion.TYPE;
        } else if (source.getQuestion() instanceof OpenEndedQuestion) {
            this.type = OpenEndedQuestion.TYPE;
        } else {
            this.type = "";
        }

        this.question = source.getQuestion().getValue();

        if (source.getQuestion().getChoices().isPresent()) {
            this.choices = Arrays.stream(source.getQuestion().getChoices().get()).map(Choice::toString)
                    .collect(Collectors.toList());
        } else {
            this.choices = null;
        }

        answer = source.getAnswer().getValue();

        tagged.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));

        this.difficulty = source.getDifficulty().value;

        this.statistics = source.getStatistics();

    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Flashcard} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Flashcard toModelType() throws IllegalValueException {
        final List<Tag> flashcardTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tagged) {
            flashcardTags.add(tag.toModelType());
        }

        if (type == null || ((!(type.equals(MultipleChoiceQuestion.TYPE)))
                && (!(type.equals(OpenEndedQuestion.TYPE))))) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, String.class.getSimpleName()));
        }

        if (type.equals(MultipleChoiceQuestion.TYPE)) {

            if (question == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, String.class.getSimpleName()));
            }
            if (!MultipleChoiceQuestion.isValidQuestion(question)) {
                throw new IllegalValueException(Answer.MESSAGE_CONSTRAINTS);
            }

            if (choices == null || choices.size() == 0) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Choice.class.getSimpleName()));
            }

            if (answer == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Answer.class.getSimpleName()));
            }
            if (!Answer.isValidAnswer(answer)) {
                throw new IllegalValueException(Answer.MESSAGE_CONSTRAINTS);
            }
            final Answer modelAnswer = new Answer(answer);

            final Question modelQuestion = new MultipleChoiceQuestion(question, choices, modelAnswer);

            if (statistics == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Integer.class.getSimpleName()));
            }

            final Statistics modelStatistics = statistics;

            final Set<Tag> modelTags = new HashSet<>(flashcardTags);

            if (difficulty == null) {
                return new Flashcard(modelQuestion, modelTags, modelStatistics);
            } else {
                final Difficulty modelDifficulty = new Difficulty(difficulty);
                return new Flashcard(modelQuestion, modelTags, modelDifficulty, modelStatistics);
            }

        } else if (type.equals(OpenEndedQuestion.TYPE)) {

            if (question == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, String.class.getSimpleName()));
            }
            if (!OpenEndedQuestion.isValidQuestion(question)) {
                throw new IllegalValueException(Answer.MESSAGE_CONSTRAINTS);
            }
            if (answer == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Answer.class.getSimpleName()));
            }
            if (!Answer.isValidAnswer(answer)) {
                throw new IllegalValueException(Answer.MESSAGE_CONSTRAINTS);
            }
            final Answer modelAnswer = new Answer(answer);

            final Question modelQuestion = new OpenEndedQuestion(question, modelAnswer);



            if (statistics == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Integer.class.getSimpleName()));
            }

            final Statistics modelStatistics = statistics;

            final Set<Tag> modelTags = new HashSet<>(flashcardTags);

            if (difficulty == null) {
                return new Flashcard(modelQuestion, modelTags, modelStatistics);
            } else {
                final Difficulty modelDifficulty = new Difficulty(difficulty);
                return new Flashcard(modelQuestion, modelTags, modelDifficulty, modelStatistics);
            }

        } else {
            throw new IllegalValueException(String.format(INVALID_TYPE));
        }
    }

}
