package quickcache.logic.parser;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import quickcache.commons.core.Messages;
import quickcache.logic.commands.ImportCommand;
import quickcache.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ImportCommand object.
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ImportCommand
     * and returns an ImportCommand object for execution.
     *
     * @throws ParseException If the given user input does not conform to the expected format.
     */
    @Override
    public ImportCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String fileName;
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);
        try {
            fileName = ParserUtil.parseFileName(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                ImportCommand.MESSAGE_USAGE), pe);
        }
        Path path = Paths.get("import", fileName);
        return new ImportCommand(path);
    }
}
