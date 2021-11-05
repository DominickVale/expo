---
title: Troubleshooting build errors and crashes
---

> This document is under active development; the topic it covers is expansive and finding the right way to explain how to troubleshoot issues will take some trial and error. Your suggestions for improvements are welcome as pull requests.

import TerminalBlock from '~/components/plugins/TerminalBlock';

When something goes wrong, it probably will go wrong in one of two ways: 1) your build will fail, or 2) the build will succeed but encounter a runtime error, eg: it crashes or hangs when you run it.

All standard advice around [narrowing down the source of an error](https://expo.fyi/manual-debugging) applies here; this document provides information that may be useful on top of your typical troubleshooting processes and techniques.

## Build errors

If your project builds locally in release mode, it should also build on EAS Build -- provided that:

- Relevant Build tool versions (eg: Xcode, Node, npm, Yarn) are the same in both environments.
- Relevant environment variables are the same in both environments.
- The archive that is uploaded to EAS Build includes the same relevant source files.

### Find and read the error message

Before you go further, you need to be sure that you have located the error message and read it.

Go to your build details page (find it on the [build dashboard](https://expo.dev/accounts/[account]/projects/[project]/builds) if you don't have it open already) and find any of the build phases that errored and expand them. Often the earliest phase that errors will contain the most useful information.

You will likely see many log entries prefixes with `[stderr]`, but don't be thrown off. It's common for CLI tools to use [stderr](https://en.wikipedia.org/wiki/Standard_streams#Standard_error_(stderr)) to output warnings and other diagnostics, not just errors. So how do you know which log lines are responsible for your build failing? To some extent, you have to use your own judgement, but there are some common signs you can look out for.

### Verify that your project builds locally

You can verify that your project builds on your local machine with `expo run`:

<TerminalBlock cmd={['# Locally compile and run the Android app in release mode', 'expo run:android --variant release', '', '# Locally compile and run the iOS app in release mode', 'expo run:ios --configuration Release']} />

> It's important that we use the release variant/configuration with these commands, because there are meaningful differences between debug and release builds.

**These commands require that native toolchains for the respective platforms are installed and configured correctly**, which may not be the case if you are building a [managed project](/introduction/managed-vs-bare.md). [Learn how to set up your local development environment](https://reactnative.dev/docs/environment-setup).

<details><summary><h4>💡 Managed workflow: Are you unable to install native toolchains on your machine? Or prefer to avoid it?</h4></summary>
<p>

**If you do not have native toolchains installed locally**, for example because you do not have an Apple computer and therefore cannot build an iOS app on your machine, it can be trickier to get to the bottom of build errors. The feedback loop of making small changes locally and then seeing the result on EAS Build is slower than doing the same steps locally, because the EAS Build worker must set up its environment, download your project, and install dependencies before starting the build.

</p>
</details>

In managed projects, these commands will run `expo prebuild` to generate native projects &mdash; you likely want to [clean up the changes](https://expo.fyi/prebuild-cleanup) once you are done troubleshooting.

If your native toolchains are installed correctly and you are unable to build and run your project in release mode on your local machine, it will not build on EAS Build. Fix the issues locally, then try again on EAS Build. The other advice in this doc may be useful to help you resolve the issue locally, but often this requires some knowledge of native tooling or judicious application of Google, StackOverflow, and GitHub Issues.

### Native build errors

The most common types of native build errors are:

- .
- .
- .

- Use `expo doctor` to determine if you have any incompatible library versions.

### JavaScript build errors

The most common types of JavaScript build errors are:

- .
- .

......

## Runtime errors

The [debugging guide](/workflow/debugging.md#production-errors) gives good advice ...

## Still not working?

How to report your issue and where