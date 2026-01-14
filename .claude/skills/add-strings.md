# add-strings

## Description
Add a new string resource across all supported languages in the project (English, Hebrew, Arabic, and Hindi).

## Usage
```
/add-strings [key] [english_text]
```

If `key` and `english_text` are not provided as arguments, the skill will prompt for them.

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
   - If it exists, inform the user and ask if they want to update it instead

3. **Get translations**:
   - Use AskUserQuestion to ask if the user wants to:
     - Provide translations manually for all languages
     - Skip translations for now (will add placeholders)

   - If providing manually, collect translations for:
     - Hebrew (עברית)
     - Arabic (العربية)
     - Hindi (हिंदी)

   - If skipping, use the English text as a placeholder for all languages (to be translated later)

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
   - Confirm that the string was added to all 4 language files
   - Show the usage example: `stringResource(Res.string.your_key_name)`
   - Remind the user that they can update translations later if placeholders were used

## Example Interactions

### Example 1: With arguments
```
/add-strings save_button "Save Changes"
```
→ Prompts for translations, then adds the string to all files

### Example 2: Without arguments
```
/add-strings
```
→ Prompts for key, text, and translations, then adds to all files

## Notes
- All user-facing strings MUST use the string resources system (never hardcoded)
- The project supports 4 languages: English, Hebrew (RTL), Arabic (RTL), Hindi
- Hebrew and Arabic are RTL (right-to-left) languages
- Generated resources are available via `Res.string.*` from the `com.elna.moviedb.resources` package
- Strings are organized with XML comments for categories (e.g., `<!-- Search Feature -->`)