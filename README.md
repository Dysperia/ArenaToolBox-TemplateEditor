TemplateEditor (currently developed version)
==============

# Summary

TemplateEditor is part of the ArenaToolBox project which want to provide translators and modders various tools for creating, editing and update the different TES1: Arena file formats.
TemplateEditor provides support for exploring and editing the TEMPLATE.DAT file. This file contains the majority of Arena in-game texts. However, the game will look for these texts at specific offsets and thus when translating the game one can have a limited number of characters. Since the offset are located in POINTER1.DAT, this tool was imagine to edit the text and update the offsets. Thanks to that, the limitation is removed. Also, keep in mind that this tool is in under active development and has not yet all his functionalities.
BSATool was created by David Aussourd alias Dysperia (softwatermermaid@hotmail.fr) with Java 8 and JavaFX.

Note: Arena will use special character like #, % and &. Do not use them in your edited text or Arena could not appreciate it.

# ArenaToolBox parts :
* [BSATool](https://github.com/Dysperia/ArenaToolBox-BSATool "BSATool")
* [TemplateEditor](https://github.com/Dysperia/ArenaToolBox-TemplateEditor "TemplateEditor")

# Various links
## French translation project (PFA)
[PFA Wiki](http://www.projet-french-arena.org/wiki/ "PFA Wiki") - [PFA forum](http://www.projet-french-arena.org/forum/ "PFA Forum")

## ArenaToolBox download from the PFA
Be aware that the template editor is working but in testing phase and some localization bugs have been found while using it in Turkey. The BSATool should be working for everyone.
[ArenaToolBox](http://www.projet-french-arena.org/files/ArenaToolBox_win32bit.zip "ArenaToolBox")
