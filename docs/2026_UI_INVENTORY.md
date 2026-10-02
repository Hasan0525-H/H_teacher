# H_teacher UI inventory — Workbench 2026

## Entry point and active destinations

`MainActivity.kt` is the sole application shell. It routes to these six Compose screens in
`ui/premium`. There are five dock destinations; the sixth is a focused PDF reader.

| Route | Active screen | New information architecture |
| --- | --- | --- |
| home | `HomeDashboard.kt` | Daily workbench, live stats, workstream shortcuts and horizontal book shelf. |
| curricula | `EducationLibrary.kt` | Searchable two-column cover library and separate bottom-sheet workspace for a course, units and lessons. |
| exams | `ExamStudio.kt` | Source → design → actual question-paper preview; exports question/answer PDFs. |
| portfolio | `ProfessionalPortfolio.kt` | Teacher identity and a chronological editorial achievement journal with attachments. |
| tools | `ClassroomHub.kt` | Attendance overview, tabbed roster, gradebook and report export. |
| pdf | `EducationPdfReader.kt` | Immersive reader, pinch zoom, page scrubber and direct page number navigation. |

## Design system

- `EducationDesign.kt` defines the **Workbench 2026** token palette, spacing, custom-drawn
  custom floating dock, inputs, actions and empty/loading states.
- `WorkbenchGlyphs.kt` defines the independent H/Line vector icon family (rounded 24-unit originals, RTL-aware navigation arrows).\n- `ExperienceComponents.kt` defines mastheads, workstream rows, illustrated book covers,
  progress stages and journal timeline layouts, rather than recoloring repeated card lists.
- `ui/theme/{Theme,Type,Shapes}.kt` supplies the matching Material 3 color scheme,
  Arabic-capable type scale and shape hierarchy.
- `DesignPreviewGallery.kt` contains six isolated Android Studio Compose previews that
  use representative sample text. Preview numbers are **not live** app statistics.
- `MainActivity.kt` provides RTL and subtle animated navigation between destinations.

## Data and behavior boundaries

- All active screens use existing ViewModels and `HTeacherApplication` repositories.
  No Room schema, DAO or business service is modified as part of the visual redesign.
- Library's `ACTION_OPEN_DOCUMENT` remains directly reachable without first selecting
  a grade. Metadata is requested *after* choosing the PDF. The existing file importer
  validates, copies to private storage, and creates the Room record.
- The exam studio retains question bank, AI generation, and separate PDF exports.
  Portfolio preserves profile edits, attachments and PDF export. Classroom preserves
  students, attendance, grades and report export.
- The PDF reader renders off the main thread and disposes each page's bitmap on
  removal.

## Removed legacy presentation layer

The older inactive display implementations were deleted, rather than retaining
recolorable backups that could accidentally become active again:

- `ui/home/HomeScreen.kt`
- `ui/curriculum/CurriculumScreen.kt`
- `ui/exam/ExamGeneratorScreen.kt`
- `ui/portfolio/PortfolioScreen.kt`
- `ui/tools/TeacherToolsScreen.kt`
- `ui/pdf/PdfViewerScreen.kt`
- `ui/modern/ModernScreens.kt`
- `ui/components/*` (unused components of those old screens)

All five feature ViewModels, their public APIs, data stores, Room repositories,
AI integration and PDF engines remain untouched. All six active screens are now
under `ui/premium`, with a single custom Design System and a single navigation
shell. If any feature depends on a deleted presentation class, treat that as a
migration defect and fix its call site; do not restore the old visual layout.

## Quality gates

- Android debug and release compile successfully.
- Instrumentation PDF import test opens the picker, imports a generated PDF,
  confirms its local path and Room record, and checks persistence after
  Activity recreation. Invalid/oversized inputs have separate regression tests.
- Six Android Studio previews compile. CI compilation alone is **not** a human
  visual inspection or an on-device usability test; design acceptance requires
  reviewing the actual screen previews and exercising real interactions.
