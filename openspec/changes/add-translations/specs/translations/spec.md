# Spec Delta

## Purpose

Makes every piece of Fertile Grounds text translatable and gives contributors a clear way to add languages.

## ADDED Requirements

### Requirement: No hardcoded text
Every player-facing string SHALL come from a translation key with an English entry in `en_us.json`.

#### Scenario: Hardcoded message
- **WHEN** a developer sends a message built from a plain string literal
- **THEN** review or the asset consistency test catches it before release

### Requirement: Language fallback
A player using a language with a partial translation SHALL see English for the missing keys, never raw keys.

#### Scenario: Partial translation
- **WHEN** a German file translates half the keys and a German player opens the guide
- **THEN** untranslated pages show in English

### Requirement: Contributor guide
`CONTRIBUTING.md` SHALL explain how to add or update a language, including the file location and the rule that translations are written by people.

#### Scenario: New translator
- **WHEN** a contributor reads the translation section
- **THEN** they can add `assets/fertilegrounds/lang/<code>.json` and open a pull request without asking questions
