# Expense Tracker App

## Overview
A simple yet powerful Expense Tracker Android application to record daily expenses, view category-wise breakdowns, and export data as PDF or CSV.  
Built with **MVVM architecture** and **Jetpack libraries**, ensuring clean, modular, and maintainable code.

## AI Usage Summary
I used ChatGPT for:
- Guidance on implementing PDF and CSV export features.
- Generating code snippets for opening PDFs within the app.
- Suggestions on UI/UX optimizations.
- Debugging and fixing IndexOutOfBoundsException during RecyclerView updates.

## Prompt Logs
Key prompts used during development:

1. *“Fix java.lang.SecurityException when loading Photo Picker URI — can't access content://media/picker…” — retried with code fixes and takePersistableUriPermission suggestions.*

2. *“Capture image and save to cache using FileProvider — error No persistable permission grants found” — fixed by removing unnecessary takePersistableUriPermission for app-owned FileProvider URIs.*

3. *“Carousel adapter and layout look bad; suggestions for modularizing and using CarouselLayoutManager” — iterated layout & item size changes, added padding and center scaling.*

4. *“MPAndroidChart dependency fails to resolve” — added JitPack repository and corrected settings.gradle.kts vs settings.gradle syntax.*

5. *“Export PDF/CSV, share with FileProvider — provider authority exceptions” — fixed manifest vs code authority mismatch and file_paths.xml.*

6. *“How to detect duplicate expenses” — proposed DB unique indices plus pre-insert duplicate dialog and fuzzy matching options.*

## Features Implemented ✅
- [x] Add, edit, and delete expense entries
- [x] Daily total expense calculation
- [x] Category-wise expense summary
- [x] PDF report export
- [x] CSV report export
- [x] In-app PDF viewer
- [x] Share expense reports via other apps
- [x] MVVM architecture with ViewModel, LiveData, and Repository pattern
- [x] Clean, modular code with separate layers for UI, Data, and Domain

## APK Download
[**Download APK**](https://drive.google.com/file/d/17iPH3u2IwCKQht-mnPqwhVSJH_prt3iQ/view?usp=sharing)

## Screenshots


## Resume
[**View Resume**](/resume.pdf)

## Source Code
The repository includes:
- Full Android Studio project (XML layouts, MVVM architecture)
- PDF and CSV generation logic
- FileProvider integration for sharing
- Screenshots & README
