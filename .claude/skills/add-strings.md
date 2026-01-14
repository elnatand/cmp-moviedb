# add-strings

## Description
Add a new string resource across all supported languages in the project (English, Hebrew, Arabic, and Hindi).

## Usage
```
/add-strings [key] [english_text]
```

If `key` and `english_text` are not provided as arguments, the skill will prompt for them.

## Proactive Usage

This skill MUST be invoked automatically whenever:

1. **Claude is about to generate code with hardcoded user-facing strings**
   - Immediately invoke this skill with the string key and English text
   - Do NOT ask the user for permission - just invoke it automatically
   - Wait for the skill to complete, then generate code using the string resource

2. **The user asks to add UI text, labels, messages, or buttons**
   - Examples: "Add a save button", "Show an error message", "Add a welcome label"
   - Automatically invoke `/add-strings` to add the text across all languages

3. **A code change requires new user-facing text**
   - During feature implementation that introduces new UI elements
   - When updating existing UI that needs different text

**Important**: This skill should run automatically in the background. Never ask the user to run it manually or wait for approval. Just invoke it, let it complete, then continue with code generation.

## Instructions

When this skill is invoked:

1. **Extract or prompt for input**:
   - If the user provided `key` and `english_text` as arguments, use those directly
   - Otherwise, use AskUserQuestion to prompt for:
     - String key (snake_case format, e.g., `save_button`, `error_message`)
     - English text (the actual text to display)

2. **Validate the string key**:
   - Check that the key uses snake_case format (lowercase with underscores)
   - Read the English strings file to verify the key doesn't already exist
   - If it exists, inform the user that the key already exists and skip adding it

3. **Generate translations automatically**:
   - **Do NOT prompt the user for translations**
   - Automatically translate the English text to all 4 languages:
     - English: Use the provided text
     - Hebrew (עברית): Translate to Hebrew
     - Arabic (العربية): Translate to Arabic
     - Hindi (हिंदी): Translate to Hindi

   - Translation guidelines:
     - Keep proper nouns and brand names in English
     - Maintain consistent terminology with existing translations
     - For technical terms, check existing strings for precedent
     - Preserve placeholders, formatting, and special characters

4. **Add strings to all XML files**:

   Read each strings file and add the new string entry in the appropriate location:

   - **English**: `core/ui/src/commonMain/composeResources/values/Strings.xml`
   - **Hebrew**: `core/ui/src/commonMain/composeResources/values-he/Strings.xml`
   - **Arabic**: `core/ui/src/commonMain/composeResources/values-ar/Strings.xml`
   - **Hindi**: `core/ui/src/commonMain/composeResources/values-hi/Strings.xml`

   **Important**:
   - Add the new string before the closing `</resources>` tag
   - Maintain the existing file structure and formatting
   - Add the string at the end unless the user specifies a category comment (e.g., `<!-- Search Feature -->`)
   - Escape special XML characters:
     - `&` → `&amp;`
     - `<` → `&lt;`
     - `>` → `&gt;`
     - `"` → `&quot;`
     - `'` → `&apos;`

5. **Verify the changes**:
   - After adding strings to all files, read back one of the files to confirm the change was applied correctly
   - Show the user the generated code reference (e.g., `Res.string.your_key_name`)

6. **Output summary**:
   - Briefly confirm that the string was added to all 4 language files
   - Show the key name for reference: `Res.string.your_key_name`
   - Keep the summary concise - this should be a seamless, automatic process

## Example Interactions

### Example 1: Automatic invocation during code generation
```
User: "Add a logout button to the profile screen"
Claude: *Automatically invokes /add-strings logout_button "Logout"*
Claude: *Adds translations to all 4 language files*
Claude: *Generates code with stringResource(Res.string.logout_button)*
```

### Example 2: Manual invocation with arguments
```
/add-strings save_button "Save Changes"
```
→ Automatically translates and adds the string to all 4 files

### Example 3: Manual invocation without arguments
```
/add-strings
```
→ Prompts for key and text, then automatically translates and adds to all files

## Notes
- All user-facing strings MUST use the string resources system (never hardcoded)
- The project supports 4 languages: English, Hebrew (RTL), Arabic (RTL), Hindi
- Hebrew and Arabic are RTL (right-to-left) languages
- Generated resources are available via `Res.string.*` from the `com.elna.moviedb.resources` package
- Strings are organized with XML comments for categories (e.g., `<!-- Search Feature -->`)