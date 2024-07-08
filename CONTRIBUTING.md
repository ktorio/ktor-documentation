# Contributing to Ktor Documentation

Thank you for your interest in contributing to the Ktor Documentation repository! We are excited to have you on board as
a contributor.

## Getting Started

The core of the documentation lives inside the `/topics` folder and is written in Markdown and XML.

You are welcome to fix typos, reword or add new content, update outdated topics and code snippets or suggest new
features.

To get an overview of the project, read the [README](README.md) file.

If you'd like to contribute, but don't know where to start, take a look at the [open issues
on YouTrack](https://youtrack.jetbrains.com/issues/KTOR?q=Subsystem:%20Docs%20%20State:%20Submitted%20%20Type:%20Bug).

## Environment set-up

This project is built with [Writerside](https://www.jetbrains.com/help/writerside/discover-writerside.html). You can
[get Writerside](https://www.jetbrains.com/writerside/download/#section=mac) as a standalone tool or by installing the
Writerside plugin into your JetBrains IDE, which is our
personal preference.

## Make changes

1. If this is your first time contributing to Ktor Docs, go to the official documentation repository and click on the
   **Fork** button to [fork the repository](https://github.com/ktorio/ktor-documentation/fork) to your personal account.
2. Clone the forked repository to your local machine:

   ```Bash
   git clone git@github.com:YOUR-USERNAME/ktor-documentation.git
   ```
3. Add the original Ktor docs repository as a "Git remote" executing this command:

   ```Bash
   git remote add upstream https://github.com/ktorio/ktor-documentation.git
   ```
4. Create a new branch from `main`:

   ```Bash
   git checkout -b your_branch_name upstream/main
   ```

5. Make changes.
    1. If you want to add code examples to a topic, consider creating a runnable code example
       in [codeSnippets](codeSnippets) and [add a reference](codeSnippets/README.md#referencing-code-snippets) of it to
       the relevant topic.
    2. Inspect your code for problems and make sure it is well formatted.
    3. For elements usage and syntax, refer to
       the [Writerside documentation](https://www.jetbrains.com/help/writerside/markup-reference.html#markdown).
6. Commit and push your changes.

## Pull request

When you are ready to submit your changes, open
a [pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) and
include the following information:

- A short description.
- Relevant links, such as a link to a YouTrack issue or a community channel thread.
- Enable the checkbox
  to [allow maintainer edits](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/allowing-changes-to-a-pull-request-branch-created-from-a-fork)
  so the branch can be updated for a merge.

Once you submit your PR, a Ktor team member will review your proposal. We may ask for changes to be made or add minor
edits ourselves.

## That's it!

Once your PR is merged, your contributions will be publicly visible on the [Ktor docs](https://ktor.io/docs). We thank
you for your efforts!