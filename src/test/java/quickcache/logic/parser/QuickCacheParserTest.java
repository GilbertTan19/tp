package quickcache.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static quickcache.logic.commands.EditCommand.EditFlashcardDescriptor;
import static quickcache.logic.parser.CliSyntax.PREFIX_ANSWER;
import static quickcache.testutil.Assert.assertThrows;
import static quickcache.testutil.TypicalFlashcards.RANDOM1;
import static quickcache.testutil.TypicalIndexes.INDEX_FIRST_FLASHCARD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import quickcache.commons.core.Messages;
import quickcache.logic.commands.AddOpenEndedQuestionCommand;
import quickcache.logic.commands.ClearCommand;
import quickcache.logic.commands.DeleteCommand;
import quickcache.logic.commands.EditCommand;
import quickcache.logic.commands.ExitCommand;
import quickcache.logic.commands.ExportCommand;
import quickcache.logic.commands.FindCommand;
import quickcache.logic.commands.HelpCommand;
import quickcache.logic.commands.ImportCommand;
import quickcache.logic.commands.ListCommand;
import quickcache.logic.commands.OpenCommand;
import quickcache.logic.commands.StatsCommand;
import quickcache.logic.commands.TestCommand;
import quickcache.logic.parser.exceptions.ParseException;
import quickcache.model.flashcard.Flashcard;
import quickcache.model.flashcard.FlashcardContainsTagPredicate;
import quickcache.model.flashcard.FlashcardPredicate;
import quickcache.model.flashcard.Tag;
import quickcache.testutil.EditFlashcardDescriptorBuilder;
import quickcache.testutil.FlashcardBuilder;
import quickcache.testutil.FlashcardUtil;

public class QuickCacheParserTest {

    private final QuickCacheParser parser = new QuickCacheParser();

    @Test
    public void parseCommand_add() throws Exception {
        Flashcard flashcard = new FlashcardBuilder().withTags().build();
        AddOpenEndedQuestionCommand command = (AddOpenEndedQuestionCommand)
            parser.parseCommand(FlashcardUtil.getAddCommand(flashcard));
        assertEquals(new AddOpenEndedQuestionCommand(flashcard), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
            DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_FLASHCARD.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_FLASHCARD), command);
    }

    @Test
    public void parseCommand_open() throws Exception {
        OpenCommand command = (OpenCommand) parser.parseCommand(
                OpenCommand.COMMAND_WORD + " " + INDEX_FIRST_FLASHCARD.getOneBased());
        assertEquals(new OpenCommand(INDEX_FIRST_FLASHCARD), command);
    }

    @Test
    public void parseCommand_stats() throws Exception {
        StatsCommand command = (StatsCommand) parser.parseCommand(
                StatsCommand.COMMAND_WORD + " " + INDEX_FIRST_FLASHCARD.getOneBased());
        assertEquals(new StatsCommand(INDEX_FIRST_FLASHCARD), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Flashcard flashcard = new FlashcardBuilder().build();
        EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder(flashcard).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_FLASHCARD.getOneBased() + " "
                + FlashcardUtil.getEditFlashcardDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_FLASHCARD, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD
                + " t/" + keywords.stream().collect(Collectors.joining(" t/")));
        Set<Tag> tagsToMatch =
                new HashSet<>(keywords.stream().map(Tag::new).collect(Collectors.toCollection(ArrayList::new)));
        FlashcardPredicate predicate =
                new FlashcardPredicate(List.of(new FlashcardContainsTagPredicate(tagsToMatch)));
        assertEquals(new FindCommand(predicate), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_test() throws Exception {
        assertTrue(parser.parseCommand(TestCommand.COMMAND_WORD
            + " "
            + INDEX_FIRST_FLASHCARD.getOneBased()
            + " "
            + PREFIX_ANSWER
            + RANDOM1.getAnswer()
        ) instanceof TestCommand);
    }

    @Test
    public void parseCommand_export() throws Exception {
        assertTrue(parser.parseCommand(ExportCommand.COMMAND_WORD + " test.json") instanceof ExportCommand);
    }

    @Test
    public void parseCommand_import() throws Exception {
        assertTrue(parser.parseCommand(ImportCommand.COMMAND_WORD + " test.json") instanceof ImportCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
            HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(
            "unknownCommand"));
    }
}
