package quickcache.logic.parser;

import static quickcache.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static quickcache.logic.parser.CliSyntax.PREFIX_ANSWER;
import static quickcache.logic.parser.CliSyntax.PREFIX_DIFFICULTY;
import static quickcache.logic.parser.CliSyntax.PREFIX_QUESTION;
import static quickcache.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import quickcache.logic.commands.AddOpenEndedQuestionCommand;
import quickcache.logic.parser.exceptions.ParseException;
import quickcache.model.flashcard.Difficulty;
import quickcache.model.flashcard.Flashcard;
import quickcache.model.flashcard.Question;
import quickcache.model.flashcard.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddOpenEndedQuestionCommandParser implements Parser<AddOpenEndedQuestionCommand> {

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddOpenEndedQuestionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_QUESTION, PREFIX_ANSWER, PREFIX_TAG, PREFIX_DIFFICULTY);

        if (!arePrefixesPresent(argMultimap, PREFIX_QUESTION, PREFIX_ANSWER)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddOpenEndedQuestionCommand.MESSAGE_USAGE));
        }


        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        String questionInString = ParserUtil.parseQuestion(argMultimap.getValue(PREFIX_QUESTION).get());
        Question question = ParserUtil.parseOpenEndedQuestion(questionInString,
                argMultimap.getValue(PREFIX_ANSWER).get());

        if (arePrefixesPresent(argMultimap, PREFIX_DIFFICULTY)) {
            Difficulty difficulty = ParserUtil.parseDifficulty(argMultimap.getValue(PREFIX_DIFFICULTY).get());
            Flashcard flashcard = new Flashcard(question, tagList, difficulty);
            return new AddOpenEndedQuestionCommand(flashcard);
        } else {
            Flashcard flashcard = new Flashcard(question, tagList);
            return new AddOpenEndedQuestionCommand(flashcard);
        }
    }

}
