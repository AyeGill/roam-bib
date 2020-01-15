(defproject roam-bib "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [pdfboxing "0.1.14"]
                 [org.clojure/data.json "0.2.7"]
                 [remus "0.1.0-SNAPSHOT"]
                 [clj-time "0.15.2"]
                 [clj-glob "1.0.0"]
                 [me.raynes/fs "1.4.5"]]
  :main roam-bib.core
  :aot [roam-bib.core]
  :repl-options {:init-ns roam-bib.core})
