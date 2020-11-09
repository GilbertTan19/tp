---
layout: page
title: Xingjian's Project Portfolio Page
---

<link rel="stylesheet" href="PPP.css">

## Xingjian Chen - Project Portfolio for QuickCache (tP)

### Table of Contents
1. [Introduction](#introduction)
	1. [About the team](#about-the-team)
	2. [About the project](#about-the-project)
2. [Summary of contributions](#summary-of-contributions)
	1. [Enhancements and new features added](#enhancements-and-new-features-added)
		1. [Difficulty functionality for flashcards](#difficulty-of-a-flashcard)
		2. [Sample flashcards for starting users](#sample-flashcards)
		3. [Maintaining storage system](#storage)
		4. [GUI colour scheme](#gui-colour)
	2. [Code contributed](#code-contributed)
	3. [Other contributions](#other-contributions)
		1. [Project management](#project-management)
		2. [Enhancements to existing features](#enhancements-to-existing-features)
		3. [Documentation](#documentation)
			1. [User Guide](#user-guide)
			2. [Developer Guide](#developer-guide)
		4. [Community](#community)

<a name="introduction"></a>
### 1. Introduction

This is my project portfolio for QuickCache. The document outlines my contributions to the project, including the enhancements and features that I have implemented.

<a name="about-the-team"></a>
#### 1.1 About the team

Our team consists of five Year 2 Computer Science students from National University of Singapore, who were all taking CS2103T Software Engineering at the time of this project.

<a name="about-the-project"></a>
#### 1.2 About the project

QuickCache is developed as part of our team project for our module CS2103T Software Engineering. QuickCache is designed to be a desktop application (Windows/macOS/Linux) for managing flashcards, optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI). The software is adapted and expanded from [AddressBook Level 3](https://github.com/nus-cs2103-AY2021S1/tp).

<a name="summary-of-contributions"></a>
### 2. Summary of contributions

I served as Integration Lead for the project, and my team responsibilities include:
* Taking charge of versioning of the code, maintaining the code repository, integrating various parts of the software to create a whole.
* Facilitating the merging of code into the production branch.
* Ensuring that integration tests are performed properly and on time.

My responsibilities as a developer also include maintaining QuickCache's storage system, creating features for users to set difficulty levels to their flashcards, and creating a set of starting sample flashcards for users to experiment on.

In the following sections, I will illustrate the above-mentioned enhancements in greater detail, along with the corresponding documentation that I have written for them within the user and developer guides.

<a name="enhancements-and-new-features-added"></a>
#### 2.1 Enhancements and features added

The following describes the enhancements and new features that I added to the project.

<a name="difficulty-of-a-flashcard"></a>
**2.1.1 Feature: Difficulty functionality for flashcards**
  * What it does: allows the user to set a specific difficulty level for created flashcards. Difficulty level of a flashcard can be specified during creation through the `add` commands or during editing through the `edit` command. A difficulty tag with specified colouring corresponding to the difficulty level will then be seen under the flashcard in the GUI.
  * Justification: This feature improves the organisation of flashcards for the user as the user can more easily differentiate the flashcards that he/she has more difficulty in.
  * Highlights: This feature affects existing commands and commands to be added in the future. It required an in-depth analysis of design alternatives. The implementation too was challenging as it required changes to existing commands, such as `add`, `edit`. GUI was also adjusted to fit the functionality.

<a name="sample-flashcards"></a>
**2.1.2 Feature: Sample flashcards for starting users**
  * What it does: provides a set of sample flashcards containing sample questions and answers for new users who open QuickCache for the first time.
  * Justification: This feature provides a more user-friendly experience for the new user as he/she can play around with the sample flashcards and get familiar with the application.
  * Highlights: This feature checks for the existence of the data file stored in QuickCache. A new data file containing the sample flashcards will be added if there is no present data file.

<a name="storage"></a>
**2.1.3 Enhancement: Maintaining storage system**
  * What it does: manages QuickCache's storage system by updating the application's data file with the current set of flashcards.
  * Justification: This enhancement ensures that the application's data file is properly updated and checks for corrupted information within the data file.
  * Highlights: This enhancement required an in-depth analysis of design alternatives and how we represent data in the json file. The storage has to be updated as functionalities are added or changed.

<a name="gui-colour"></a>
**2.1.4 Enhancement: GUI colour scheme**
  * What it does: changes the colour of the application's GUI.
  * Justification: Sets a suitable theme for our application that reflects our vision for the project.

<a name="code-contributed"></a>
#### 2.2 Code contributed

Click on the following links to view the code that I have contributed:

 * [RepoSense](https://nus-cs2103-ay2021s1.github.io/tp-dashboard/#breakdown=true&search=ChenXJ98&sort=groupTitle&sortWithin=title&since=2020-08-14&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&tabOpen=true&zFR=false&tabType=authorship&until=2020-11-09&tabAuthor=ChenXJ98&tabRepo=AY2021S1-CS2103T-T13-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code)
 * [Functional Code](https://github.com/AY2021S1-CS2103T-T13-2/tp/tree/master/src/main/java/quickcache)
 * [Test Code](https://github.com/AY2021S1-CS2103T-T13-2/tp/tree/master/src/test/java/quickcache)

<a name="other-contributions"></a>
#### 2.3. Other contributions

 The following describes the various other contributions that I have made to the project.

<a name="project-management"></a>
 * **Project management**:
   * Managed 36 issues on GitHub
   * Reviewed 77 pull requests on GitHub

<a name="enhancements-to-existing-features"></a>
 * **Enhancements to existing features**:
   * Wrote additional tests for existing features (Pull request [\#140](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/140))
   * Wrote tests for difficulty feature (Pull request [\#127](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/127))
   * Refactored remnant code from AddressBook package into the QuickCache package (Pull request [\#231](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/231))

<a name="documentation"></a>
 * **Documentation**:
<a name="user-guide"></a>
   * User Guide:
     * Added documentation for the features `difficulty`, `tag` (Pull requests [\#30](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/30), [\#159](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/159))
     * Added introduction message and table of contents of the user guide (Pull requests [\#168](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/168))
     * Added user stories (Pull request [\#231](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/231))
<a name="developer-guide"></a>
   * Developer Guide:
     * Added implementation details of the `difficulty`, `tag` and `storage` features (Pull requests [\#135](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/135), [\#171](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/171))
     * Added UML diagrams for `storage` and `open` features ([\#171](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/171), [\#284](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/284))
     * Added manual testing instructions (Pull requests [\#274](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/274))

<a name="community"></a>
 * **Community**:
   * PRs reviewed (with non-trivial review comments): [\#21](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/21), [\#69](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/69), [\#136](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/136), [\#137](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/137), [\#153](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/153)
   * Reported bugs and suggestions for other teams in the class (examples: [1](https://github.com/AY2021S1-CS2103T-T09-4/tp/issues/176), [2](https://github.com/AY2021S1-CS2103T-T09-4/tp/issues/175), [3](https://github.com/AY2021S1-CS2103T-T09-4/tp/issues/174), [4](https://github.com/AY2021S1-CS2103T-T09-4/tp/issues/173), [5](https://github.com/AY2021S1-CS2103T-T09-4/tp/issues/172))
