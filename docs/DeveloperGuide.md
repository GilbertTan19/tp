---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="450" />

The ***Architecture Diagram*** given above explains the high-level design of the App. Given below is a quick overview of each component.

**`Main`** has two classes called [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

Each of the four components,

* defines its *API* in an `interface` with the same name as the Component.
* exposes its functionality using a concrete `{Component Name}Manager` class (which implements the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component (see the class diagram given below) defines its API in the `Logic.java` interface and exposes its functionality using the `LogicManager.java` class which implements the `Logic` interface.

![Class Diagram of the Logic Component](images/LogicClassDiagram.png)

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

The sections below give more details of each component.

### UI component

![Structure of the UI Component](images/UiClassDiagram.png)

**API** :
[`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` data so that the UI can be updated with the modified data.

### Logic component

![Structure of the Logic Component](images/LogicClassDiagram.png)

**API** :
[`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

1. `Logic` uses the `AddressBookParser` class to parse the user command.
1. This results in a `Command` object which is executed by the `LogicManager`.
1. The command execution can affect the `Model` (e.g. adding a person).
1. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
1. In addition, the `CommandResult` object can also instruct the `Ui` to perform certain actions, such as displaying help to the user.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")` API call.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

### Model component

![Structure of the Model Component](images/ModelClassDiagram.png)

**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user’s preferences.
* stores the quick cache flashcard data.
* exposes an unmodifiable `ObservableList<Flashcard>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.


<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique `Tag`, instead of each `Person` needing their own `Tag` object.<br>
![BetterModelClassDiagram](images/BetterModelClassDiagram.png)

</div>


### Storage component

![Structure of the Storage Component](images/StorageClassDiagram.png)

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the address book data in json format and read it back.

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how the undo operation works:

![UndoSequenceDiagram](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

![CommitActivityDiagram](images/CommitActivityDiagram.png)

#### Design consideration:

##### Aspect: How undo & redo executes

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Tags

#### Proposed Implementation

The proposed tags mechanism is facilitated by `Flashcard` upon creation. It is stored internally as an `Set<Tag>` inside the `flashcard` object.

Given below is an example usage scenario and how the tag mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `QuickCache` will be initialized with the initial QuickCache state.

Step 2. The user executes `add q/ question... t/tag` command to add a flashcard with tag. The `add` command will cause the addition of a flashcard with a tag inside the QuickCache. 

Step 3. The user executes `edit 1 t/tag` to edit the tag in the first flashcard of the list. The edit command will change the internal structure of flashcard such that the `Set<Tag>` is updated.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not be saved in the QuickCache, so the flashcard inside the QuickCache will not be updated.
</div>

#### Design consideration:

##### Aspect: How tag executes

* **Alternative 1 (current choice):** Tag is saved upon creation.
  * Pros: Easy to implement.
  * Cons: May be complicated as there will be too many fields in the `add` command.

* **Alternative 2:** Individual command knows how to tag by
  itself.
  * Pros: Will be less complicated.
  * Cons: There may be too many commands which can be combined to one.


### \[Proposed\] Add Flashcard with open-ended question feature

The proposed Add mechanism is facilitated by `QuickCache` . It is stored internally as a `UniqueFlashcardList` inside the `QuickCache` object.

Given below is an example usage scenario and how the add mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `QuickCache` will be initialized with the initial QuickCache state.

Step 2. The user executes `add q/ question... t/tag` command to add a flashcard with tag. The `add` command will cause the addition of a flashcard with open-ended question inside the QuickCache. 

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not be saved in the QuickCache, so the flashcard inside the QuickCache will not be updated.
</div>

#### Design consideration:

##### Aspect: How Add executes

* **Alternative 1 (current choice):** Flashcard is saved upon creation inside the QuickCache.
  * Pros: Easy to implement and CLI-optimized.
  * Cons: May be complicated as there will be too many fields in the `add` command.

### \[Proposed\] Add Flashcard with Multiple Choice question feature

The proposed Add Multiple Choice Question mechanism is facilitated by `QuickCache` . It is stored internally as a `UniqueFlashcardList` inside the `QuickCache` object.

Given below is an example usage scenario and how the addmcq mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `QuickCache` will be initialized with the initial QuickCache state.

Step 2. The user executes `addmcd q/ question ans/1 c/first choice c/second choice c/third choice... t/tag` command to add a flashcard with tag. The `addmcq` command will cause the addition of a flashcard with multiple choice question inside the QuickCache. 

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not be saved in the QuickCache, so the flashcard inside the QuickCache will not be updated.
</div>

#### Design consideration:

##### Aspect: How addmcq executes

* **Alternative 1 (current choice):** Flashcard is saved upon creation inside the QuickCache.
  * Pros: Easy to implement and CLI-optimized.
  * Cons: May be complicated as there will be too many fields in the `add` command.

### \[Proposed\] Delete Flashcard feature

The proposed Delete mechanism is facilitated by `QuickCache` . It will delete the flashcard at the provided index stored in the `UniqueFlashcardList` inside the `QuickCache` object.

Given below is an example usage scenario and how the delete mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `QuickCache` will be initialized with the initial QuickCache state.

Step 2. The user executes `delete 1` command to delete the first flashcard. The `delete` command will cause the deletion of a flashcard inside the QuickCache. 

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not be saved in the QuickCache, so the flashcard inside the QuickCache will not be updated.
</div>

#### Design consideration:

##### Aspect: How delete executes

* **Alternative 1 (current choice):** Provide the index of the flashcard to be deleted.
  * Pros: Easy to implement and CLI-optimized.
  * Cons: User have to know the index of the specified flashcard.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_



--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Students with many modules who want to memorize points
* prefer desktop apps over other types
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps
* wants to monitor his/her progress

**Value proposition**: manage flashcards faster than a typical mouse/GUI driven app with 
a test feature and track the progress later.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                |
| -------- | ------------------------------------------ | ------------------------------ | ------------------------------------------------------------------------- |
| `* * *`  | new user                                   | see usage instructions         | refer to instructions when I forget how to use the App                    |
| `* * *`  | new user                                   | have a get started             | see the features available in the app                                     |
| `* * *`  | new user                                   | have a sample data             | explore more about the app                                                |
| `* * *`  | new user                                   | purge all current data         | delete the sample data for exploring the app                              |
| `* * *`  | user                                       | add a new flashcard            |                                                                           |
| `* * *`  | user                                       | delete a flashcard             | remove flashcards that I no longer need                                   |
| `* *`    | user                                       | find a flashcard by name       | locate details of flashcards without having to go through the entire list |
| `* *`    | user                                       | edit a flashcard               |                                                                           |
| `* *`    | user                                       | quiz myself                    | memorize things better                                                    |
| `* *`    | user                                       | organize my flashcard          |                                                                           |
| `* *`    | user                                       | categorize my flashcards       | easily find the flashcard associated with the category                    |
| `* *`    | user                                       | import a set of flashcard      | easily add new category of flashcard                                      |
| `* *`    | user                                       | export a set of flashcard      | easily transfer a category of flashcard                                   |
| `*`      | user who has created a lot of flashcard    | delete a category of flashcard | easily transfer a category of flashcard                                   |
| `*`      | user with many flashcards in the list      | specify the importance         |                                                                           |
| `*`      | user                                       | randomize the question         | test myself better                                                        |
| `*`      | user                                       | get the statistic of quiz      | get a visualisation form of my performance                                |
| `*`      | user                                       | track the progress             | know what I have done when using the app                                  |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `QuickCache` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - Explore QuickCache**

**MSS**

1.  User double clicks on QuickCache.jar
2.  QuickCache opens and shows a list of flashcards
3.  User plays around with QuickCache features to familiarize with them
4.  User purges all "get started" data

    Use case ends.

**Extensions**

* 1a. Error message indicating that QuickCache.jar cannot be opened pops up.
    
  * 1a1. User opens up CLI in the directory containing QuickCache and runs `java -jar QuickCache.jar`.
    
    Use case resumes at step 2.

* 3a. User wants to know all the available commands present in QuickCache.

  * 3a1. User requests for all the instructions available in QuickCache.
    
  * 3a2. QuickCache displays all available commands.
    
    Use case resumes at step 3.
    
* 3b. User quits QuickCache while trying out quiz feature.

  * 3b1. When opening QuickCache again, quiz resumes from where the User left off.
    
    Use case resumes at step 3.

* *a. User dislikes the GUI.

  * *a1. User quits and deletes QuickCache.
    
    Use case ends.
    
    
**Use case: UC02 - View Statistics**

**Preconditions: User has QuickCache open.**

**MSS**

1.  User requests for statistics
2.  QuickCache displays statistics
3.  User completes a quiz and requests for statistics
4.  QuickCache displays updated statistics

    Use case ends.

**Extensions**

* 1a. User has not done any quiz on QuickCache.
    
  * 1a1. QuickCache shows an error message.
    
    Use case resumes at step 3.
    

**Use case: UC03 - Delete a flashcard**

**Preconditions: User has QuickCache open.**

**MSS**

1.  User requests to list flashcards
2.  QuickCache shows a list of flashcards
3.  User requests to delete a specific flashcard in the list
4.  QuickCache deletes the flashcard
5.  QuickCache updates flashcard save file (UC07)

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

  * 3a1. QuickCache shows an error message.

    Use case resumes at step 2.
    

**Use case: UC04 - Create a flashcard**

**Preconditions: User has QuickCache open.**

**MSS**

1.  User requests to add a flashcard
2.  QuickCache adds it to the list
3.  QuickCache updates flashcard save file (UC07)
4.  User requests to list flashcards
5.  QuickCache shows the list of flashcards including the recently added flashcard

    Use case ends.

**Extensions**

* 1a. The question is empty.

  * 1a1. QuickCache shows an error message.
    
    Use case ends.
    
* 1b. The answer is empty.

  * 1b1. QuickCache shows an error message.
    
    Use case ends.
    

**System: QuickCache**

**Use case: UC05 - Test a single flashcard**

**Actor: User**

**MSS:**

1. User requests to list flashcards
2. QuickCache shows a list of flashcards
3. User requests to test a specific flashcard in the list with a specific answer
4. QuickCache displays whether the answer is correct

    Use case ends.

**Extensions:**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

  * 3a1. AddressBook shows an error message.

    Use case resumes at step 2.
    

**System: QuickCache**

**Use case: UC06 - Test a set of flashcards by category**

**Actor: User**

MSS:

1. User requests to list categories
2. QuickCache shows a list of categories
3. User requests to test a specific category in the list
4. User tests each flashcard on the list (UC06)
5. QuickCache shows the number of successful questions at the end

    Use case ends.

**Extensions:**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

  * 3a1. QuickCache shows an error message.

    Use case resumes from step 2.

* 3b. The user specifies a time limit.

  * 3b1. QuickCache starts a timer.

    Use case resumes from step 4.

* 3c. The user specifies to randomly sort the questions.

  * 3c1. QuickCache randomizes the flashcards from the category.

    Use case resumes from step 4.

* 4a. The user answers the question.

  * 4a1. QuickCache shows the next question to answer.

  * Step 4a. is repeated until all questions in the category is answered.

    Use case resumes from step 5.

* 4b. The user cancels the test midway.

  * Use case resumes from step 5.

* 4c. The user runs out of time midway.

  * Use case resumes from step 5.

* 4d. The user closes QuickCache

  Use case ends.
  
  
**Use case: UC07 - Update flashcard save file**

**Actor: QuickCache**

MSS:

1. QuickCache accepts update request
2. QuickCache updates save file
  
    Use case ends.

**Extensions:**

* 2a. No save file.

  * 3a1. QuickCache creates new save file.
  
    Use case resumes from step 2.

* 2b. Save file corrupted.

  * 2b1. QuickCache shows an error message.
  
  * 2b2. QuickCache creates new save file.

    Use case resumes from step 2.
    

**Use case: UC08 - Import flashcard data file**

**Actor: User**

MSS:

1. User requests to import data file
2. QuickCache requests for the file location
3. User specifies the file location
4. QuickCache imports the file
  
    Use case ends.

**Extensions:**

* 3a. File not found.

  * 3a1. QuickCache shows an error message.
  
    Use case ends.

* 3b. Data file corrupted

  * 3b1. QuickCache shows an error message.

    Use case ends.
    

**Use case: UC09 - Add tags during creation of a Flashcard**

**Actor: User**

MSS:

1. User creates a flashcard.
2. QuickCache shows a list of existing tags.
3. QuickCache asks the user to type in the tags one by one.
4. User types in the tags one by one.
5. User signals that he is finished.
6. QuickCache adds the tags to the flashcard.

    Use case ends.

**Extensions:**

* 3a. User wishes to remove a tag that he entered.

  * 3a1. User inputs the remove command to remove the tag.

    Use case resumes at step 3.

* 5a. Some of the tags are new tags

  * 5a1. QuickCache stores and remembers the tags.

    Use case ends.
    

**Use case: UC10 - Deleting a tag category**

**Actor: User**

MSS:

1. User wants to remove a tag category.
2. User enters the delete tag command and the name of the tag to be deleted.
3. QuickCache asks for a confirmation before deleting.
4. User gives the confirmation.
5. QuickCache deletes the tag from every Flashcard.

    Use cased ends.

**Extensions:**

* 4a. User does not give confirmation

  * 4a1. QuickCache does not delete anything.

    Use case ends.
    

**Use case: UC11 - Edit tags on an existing Flashcard**

**Actor: User**

MSS:

1. User wants to add tags on an existing Flashcard
2. QuickCache asks for the name of the new tag to be added.
3. User enters the new tag name
4. QuickCache adds the tag onto the Flashcard

    Use case ends.

**Extensions:**

* 1a. User wants to remove a tag

	* 1a1. User asks QuickCache to list out all the tags
  
	* 1a2. QuickCache lists out all the tags
  
	* 1a3. User enters the name of the tag to be removed
  
	* 1a4. QuickCache removes the tag from the Flashcard

    Use case ends.

* 1b. User wants to edit a tag

	* 1b1. User asks QuickCache to rename a tag
  
	* 1b2. QuickCache asks for the new name of the tag
  
	* 1b3. User enters the new name
  
	* 1b4. QuickCache updates the tag with it's new name.  

    Use case ends.
    

**Use case: UC12 - Search for Flashcards based on Tags**

**Actor: User**

MSS:

1. User enters the name of the tag he wants to search
2. QuickCache filters through all existing Flashcards based on the tag and returns a list of Flashcards.

    Use case ends.
    
**Use case: UC13 - Open a single flashcard**

**MSS**

1. User double clicks on QuickCache.jar
2. QuickCache opens and shows a list of flashcards
3. User forgets the options of a question
4. User requests to open a specific flashcard in the list
5. QuickCache opens the flashcard and displays the options

	Use case ends.
	
**Extensions**

* 2a. The list is empty.

  	Use case ends.

* 4a. The given index is invalid.

  * 4a1. QuickCache shows an error message.

    Use case resumes at step 3.

****
### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 flashcards without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a flashcard while all flashcards are being shown

   1. Prerequisites: List all flashcards using the `list` command. Multiple flashcards in the list.

   1. Test case: `delete 1`<br>
      Expected: First flashcard is deleted from the list. Details of the deleted flashcard shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No flashcard is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases … }_
