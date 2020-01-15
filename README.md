# roam-bib

A utility for generating "bibliography" pages for [roam](https://roamresearch.com), using arXiv metadata.

## Devnotes/TODO

- [ ] Add command-line arguments
  - [ ] Better ways of adding custom text
  - [ ] Adding custom titles
  - [ ] Custom output files
  - [ ] Choose how to handle duplicates
  - [ ] Choose how to format titles
- [ ] Web interface
- [ ] Command-line interface using titles/ids instead of files.
- [ ] Non-arXiv sources

Also need to do some major cleaning up of the code.

## Usage

Run the uberjar in a directory containg arXiv pdfs. `roambib.json` contains the resulting collection of roam pages.

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
