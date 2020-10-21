package quickcache.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import quickcache.commons.core.Messages;
import quickcache.commons.core.index.Index;
import quickcache.logic.commands.EditCommand;
import quickcache.logic.parser.exceptions.ParseException;
import quickcache.model.flashcard.Choice;
import quickcache.model.flashcard.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_ANSWER, CliSyntax.PREFIX_QUESTION,
                        CliSyntax.PREFIX_CHOICE, CliSyntax.PREFIX_TAG, CliSyntax.PREFIX_DIFFICULTY);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_USAGE), pe);
        }

        EditCommand.EditFlashcardDescriptor editFlashcardDescriptor = new EditCommand.EditFlashcardDescriptor();

        if (argMultimap.getValue(CliSyntax.PREFIX_QUESTION).isPresent()) {
            editFlashcardDescriptor.setQuestion(
                    ParserUtil.parseQuestion(argMultimap.getValue(CliSyntax.PREFIX_QUESTION).get()));
        }
        if (argMultimap.getValue(CliSyntax.PREFIX_ANSWER).isPresent()) {
            editFlashcardDescriptor.setAnswer(
                    ParserUtil.parseAnswer(argMultimap.getValue(CliSyntax.PREFIX_ANSWER).get()));
        }
        if (argMultimap.getValue(CliSyntax.PREFIX_DIFFICULTY).isPresent()) {
            editFlashcardDescriptor.setDifficulty(
                    ParserUtil.parseDifficulty(argMultimap.getValue(CliSyntax.PREFIX_DIFFICULTY).get()));
        }

        parseTagsForEdit(argMultimap.getAllValues(CliSyntax.PREFIX_TAG)).ifPresent(editFlashcardDescriptor::setTags);
        parseChoicesForEdit(argMultimap.getAllValues(CliSyntax.PREFIX_CHOICE)).ifPresent(
                editFlashcardDescriptor::setChoices);

        if (!editFlashcardDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editFlashcardDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

    /**
     * Parses {@code Collection<String> choices} into a {@code String[]} if {@code choices} is non-empty.
     * If {@code choicess} contain only one element which is an empty string, it will be parsed into a
     * {@code String[]} containing zero choices.
     *
     * @return
     */
    private Optional<Choice[]> parseChoicesForEdit(Collection<String> choices) throws ParseException {
        assert choices != null;

        if (choices.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = choices.size() == 1 && choices.contains("") ? Collections.emptySet() : choices;
        return Optional.of(ParserUtil.parseChoices(tagSet));
    }

}
