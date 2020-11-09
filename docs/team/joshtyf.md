---
layout: page
title: Joshua's Project Portfolio Page
---

## Joshua Tan - Project Portfolio for QuickCache(tP)

### Table of contents

* [Introduction](#introduction)
  * [About the team](#about-the-team)
  * [About the project](#about-the-project)
* [Summary of Contributions](#summary-of-contributions)
* [Code Contributions](#code-contributions)
  * [Creation of base classes](#creation-of-base-classes)
  * [Find flashcards](#find-flashcards)
  * [Delete by tag](#delete-by-tag)
  * [Improve the GUI](#improved-the-gui)
* [Documentation Contributions](#documentation-contributions)
  * [Management of overall documentation and website](#management-of-overall-documentation-and-website)
  * [Creation of diagrams](#creation-of-diagrams)
* [Other notable contributions](#other-notable-contributions)
  * [Team management](#team-management)
  * [Defensive coding and bug fixes](#defensive-coding-and-bug-fixes)
  * [Community Contributions](#community-contributions)

### Introduction

This document serves as a project portfolio for QuickCache, and outlines my contributions to the project, including the features that I have implemented.

#### About the team

My team of five consists of  five Year 2 Computer Science students, all taking the module CS2103T Software Engineering.

#### About the project

This was a project part of the module CS2103T Software Engineering. The main aim of the project was to develop a Command
Line Interface (CLI) desktop application. The application had to capitalise on the speed and efficiency of using a CLI.
My team then decided to create an application that would allow students to create, use and manage flashcards.

QuickCache was built on top of an existing CLI application called [AddressBook3](https://se-education.org/addressbook-level3/) (AB3).
We took the entire semester (13 weeks) to build QuickCache and added 15,000 lines of new code to the existing codebase.

### Summary of Contributions

My primary role was to help develop these enhancements for QuickCache:

  * Develop a system to organise the flashcards in QuickCache
  * Delete flashcards based on their categories
  * Find flashcards based on their categories
  * Improve the GUI from the original AB3
  
In addition, I served as the Documentation Lead and my responsibilities included:

  * Managing the accuracy and correctness of UML diagrams
  * Planning the overall structure of both the User Guide and Developer Guide
  * Coordinating and delegating jobs to my teammates to ensure proper documentation
  * Ensuring the correctness of the contents of all documentations

### Code Contributions

This section describes my detailed code contributions to the project.

Click on the following links to view the code that I have contributed:
  * [RepoSense link](https://nus-cs2103-ay2021s1.github.io/tp-dashboard/#breakdown=true&search=joshtyf)
  * [Functional Code](https://github.com/AY2021S1-CS2103T-T13-2/tp/tree/master/src/main/java/quickcache)
  * [Test Code](https://github.com/AY2021S1-CS2103T-T13-2/tp/tree/master/src/test/java/quickcache)

#### Creation of base classes

**Description:** I kickstarted the project by creating the `Flashcard` class and other dependencies classes.
The design architecture was inspired by the original `AddressBook3` design. In order to follow the original design, the whole team
had to spend a considerable amount of time understanding the architecture of AB3.

**Highlights:** As we had to follow an OOP design principle and to reduce coupling across classes, we had to plan ahead
as to how we wanted to build our classes. This lead to me creating the base classes such as `Answer`, `Question`, `Tag` 
and `Choice` classes for us to use in the future for other purposes such as searching and testing of flashcards.

Pull requests: [\#35](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/35), [\#62](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/62)

#### Find flashcards

**Description:** A user of QuickCache would want to be able to search for flashcards based on their categories. This was
implemented through the use of the `Tag` organisational system. The user would be using our tagging system to label 
flashcards accordingly such as by their modules. Designing a search and filter system would also be a necessity for
future enhancements such as (deleting by categories)[#delete-by-tag].

**Highlights:** Implementing this enhancement was pretty straight forward as the original AB3 had such a feature. I had to implement
a `FlashcardContainsTagPredicate` class and additional tests to make sure that the feature worked as intended.

Pull requests: [\#60](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/60)

#### Delete by Tag

**Description:** A user of QuickCache would want to delete flashcards based on categories. I developed the enhancement
to allow QuickCache to search and delete for flashcards using the `Tag` organisational system. This feature enhances the
 existing delete by index mechanism as the user can now mass delete flashcards.
 
 **Highlights:** This enhancement was slightly challenging to implement as I had to consider whether or not to create a seperate command
  or to merge together with the original delete by index command. After much deliberation, I decided to merge both functionalities together into
  the delete command. As a result, I had to tweak the `DeleteCommand` and `DeleteCommandParser` classes accordingly to ensure proper implementation of
  both features. I also had to design extensive tests to make sure that there wouldn't be any problems from this added enhancement.

Pull requests: [\#129](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/129)

#### Improved the GUI

**Description:** The original GUI was built using [JavaFx](https://openjfx.io). In order to make it original to AB3, I tweaked
the layout of the GUI. In addition, I worked with my teammate Xing Jian to pick the colour scheme.

**Highlights:** I had to consider the UX of the application and design it such that the information presented could be
viewed easily. I also got to work on JavaFx and CSS.

Pull requests: [\#214](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/214)

### Documentation Contributions

This section describes my contributions as the Documentation Lead as well as other notable documentations.

#### Management of overall documentation and website

Our team website is hosted on Github pages and I was primarily in charge of managing the contents.
Part of my responsibilites included ensuring the accuracy of diagrams and information presented in our User Guide (UG)
and Developer Guide (DG). Being the documentation lead, I also took the initiative to learn how to use [PlantUml](https://plantuml.com)
and taught the rest of my team how to use it.

Pull requests but not limited to: [\#170](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/170),
[\#245](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/245),
[\#253](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/253),
[\#254](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/254),
[\#280](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/280)

In addition, I also had to plan the overall look and structure of the UG and DGs. As such, I was in charge of
delegating documentation jobs to my teammates and telling them what needed to be included. In total, I managed 23 issues
(not limited to Documentation) on Github.

Issues created but not limited to: [\#149](https://github.com/AY2021S1-CS2103T-T13-2/tp/issues/149),
[\#165](https://github.com/AY2021S1-CS2103T-T13-2/tp/issues/165), [\#279](https://github.com/AY2021S1-CS2103T-T13-2/tp/issues/279)

#### Creation of diagrams

In addition, I played my part in the creation of UML diagrams for our DG.

Pull requests: [\#81](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/81), [\#278](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/278)

### Other notable contributions

This section describes other contributions I made to the team that I believe were noteworthy.

#### Team management

I helped to organise our weekly zoom meetings which included reminders and creation of the zoom calls. In addition,
I also assisted our team lead, Josiah, in setting the agenda for meetings.

#### Defensive coding and bug fixes

During the course of development, my team adopted a strong defensive coding approach. We worked hard in ensuring that we left little
room for errors. In addition, we developed extensive tests to ensure that our features were working as intended. Nevertheless, we still
found bugs and I managed to fix some of the bugs.

Pull requests: [\#227](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/227),
[\#228](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/228),
[\#234](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/234),
[\#235](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/235),
[\#258](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/258)

#### Community Contributions

Working together as a team, we had to consciously put in effort to give good feedback to each other's code. In addition,
we were also tasked to test other teams applications and to find bugs.

PRs reviewed (with non-trivial review comments): [\#69](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/69),
[\#37](https://github.com/AY2021S1-CS2103T-T13-2/tp/pull/37)

Reported bugs and suggestions for other teams in the class: 
[1](https://github.com/AY2021S1-CS2103T-T11-2/tp/issues/188),
[2](https://github.com/AY2021S1-CS2103T-T11-2/tp/issues/187),
[3](https://github.com/AY2021S1-CS2103T-T11-2/tp/issues/186)
