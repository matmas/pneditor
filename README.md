PNEditor (Petri Net editor)
========
Status of the different branches : 

- Invariant : adds a token limit on places
- MacroRecorder : abandonned branche (a version of macro that had modifications from invariant)
- MacroRecorderClean  : adds a macro manager
- MacroLimit : integrates the token limit and the macro manager
- macroAndTokenLimit  : integrates the token limit, the macro manager and the fire N modification
from https://github.com/e17goudi/pneditor. due to some modifications on the marking class there,
this version compile but has some bugs (try to find them !)



You can download PNEditor from [www.pneditor.org](http://www.pneditor.org/)

Features:

- Petri net editor
- Subnets
- Role definitions
- Static places (shared resources)
- PNML import and export (Viptool non-standard dialect)
- EPS and PNG export

Code license: [GNU GPL v3](http://www.gnu.org/licenses/gpl.html)

Requirements: Java SE 6+
