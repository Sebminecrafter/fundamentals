# AGENTS.md

Notice: this was written heavily using AI to ensure completeness.

Instructions for AI coding agents (and the humans supervising them) working on Fundamentals.
Read this fully before making changes.

## Project overview

Fundamentals is a Bukkit/Spigot plugin (Java 25, built with Maven) providing commands,
custom chat, join/leave/death messages, and server checks. It currently targets Spigot API 1.21.3
and is planned to migrate to Paper in the future.

- Source: `src/main/java/io/github/sebminecrafter/fundamentals/`
  (`Commands/`, `Chat/`, `IO/`, `Checks/`, `Other/`)
- Resources: `src/main/resources/` (`plugin.yml`, `config.yml`, `lang.yml`)
- Docs: `docs/` (built with Zensical, see `zensical.toml`)
- Roadmap: `TODO.md`

## Core rules

### 1. Human audit is required

- All AI-generated changes must be reviewed by a human maintainer before being merged.
  Never merge your own work or bypass review.
- Never push directly to `main`. Work on a branch and open a pull request.
- Do not alter CI workflows, release workflows, or repository settings
  to skip checks or reviews.
- If you are unsure about something, **stop and ask**. Do not guess and hope the review catches it.
- Flag anything the reviewer should look at closely: security-sensitive code, permission checks,
  file/data handling, and anything you could not test.

### 2. You are responsible for what you push

- Treat every line you write as your own. "The AI wrote it" is not an excuse for broken code.
- Understand the code you submit. Do not commit code you cannot explain.
- Build and test before committing. At minimum, `mvn package` should succeed,
  and changed commands/features should be tested on a real server when possible.
- Do not commit code you have not run or compiled. If something could not be verified,
  say so clearly in the PR description.
- Never invent APIs, methods, config keys, or dependencies. Verify against the Spigot API
  version in `pom.xml` and the existing codebase.
- Do not leave debug code, dead code, commented-out blocks, or stray TODOs behind.
- Fix what you break. If your change causes a regression, fixing it is your job.

### 3. Do not remove major features without reason

- Never delete or substantially rewrite a command, system, config option, or language key
  unless the task explicitly requires it.
- If removal is necessary, explain why in the commit message and PR description,
  and call it out for the reviewer.
- Do not "simplify" by dropping functionality. Do not quietly change default behavior.
- Do not delete files, tests, or docs to make a build pass or an error go away.
- Breaking changes (renamed commands, changed permissions, changed config/lang structure)
  must be explicitly flagged and documented, or at the very least obviously noted.

## Commits and pull requests

- Keep commits small, focused, and atomic. One logical change per commit.
- Write clear, imperative commit messages in the style of the existing history
  (e.g. `Add /playtime`, `Fix time format`).
- Do not mix unrelated changes (refactors, formatting, features) in one commit or PR.
- Never rewrite or force-push shared history.
- PR descriptions should say what changed, why, how it was tested, and what is risky.
- Mark AI-assisted work as such in the PR description.

## Code conventions

- Follow the existing style and structure. Match naming, formatting, and patterns
  in neighboring files.
- Commands implement `FundamentalCommand` and are registered via the existing command system.
  New commands must also be added to `plugin.yml` with a description, usage, and permission.
- Permissions follow the existing scheme (`fundamentals.player.*`, `fundamentals.staff.*`, etc.).
  Always check permissions and sender type (player vs. console).
- All player-facing text goes in `lang.yml` and is read through the `Lang` class.
  Do not hardcode messages in Java.
- Anything an admin may want to change belongs in `config.yml`. When adding or changing keys,
  make sure the config updater handles existing installs and do not silently reset user config.
- Use the plugin's existing logger/utilities (`Logging`, `Commands.safeSend`, etc.)
  instead of `System.out` or ad-hoc helpers.
- Stay on the Spigot API targeted in `pom.xml`. Do not introduce Paper-only APIs
  unless the task is the Paper migration.
- Be careful with main-thread safety: no blocking I/O or heavy work on the server thread,
  and no unsafe Bukkit API calls from async tasks.
- Clean up after yourself: cancel tasks, clear per-player maps on quit, and avoid memory leaks.
- Do not add new dependencies without approval. Keep the shaded jar lean.
- Never hardcode secrets, tokens, or personal data. Do not add telemetry
  or network calls beyond what already exists (bStats, update checker).

## Docs and tracking

- Update `docs/` when adding or changing user-facing behavior (commands, permissions,
  config, lang keys).
- Update `TODO.md` when you complete or start an item on it.
- Keep `README.md` accurate, but do not rewrite it unprompted.

## Security and safety

- Treat all player input as untrusted: validate arguments, bounds, and targets.
- Staff and privileged commands need correct permission checks. Do not weaken them.
- Do not add features that could be abused for griefing or privilege escalation
  without explicit approval.
- Respect the license (Apache 2.0). Do not paste in code from other projects
  unless the license allows it and attribution is included.

## What agents must not do

- Merge, release, tag, or publish builds.
- Modify `LICENSE`, `.github/workflows/`, or release/versioning config without being asked.
- Bump the version in `pom.xml` unless asked.
- Make sweeping refactors or mass-reformat files unprompted.
- Hide uncertainty, failed tests, or known issues. Report them plainly.
