(ns roam-bib.core
  (:refer-clojure :exclude [replace split])
  (:require
   [clojure.data.json :as json]
   [pdfboxing.text :as text]
   [pdfboxing.info :as info]
   [me.raynes.fs :as fs]
   [clojure.string :refer [lower-case replace includes? split join trim]]
   [remus :refer [parse-url parse-file]])
  (:gen-class))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; Can find paper either by title or arXiv id.
;; Can try to look for title in pdf

(def arxiv-regex #"arXiv:([0-9]+\.[0-9]+)")

(defn get-title-md [pdf]
  (let [title (info/metadata-value pdf "Title")]
    (if (empty? title) nil title)))

(defn get-title-text [pdf]
  (first (split (text/extract pdf) #"\n"))) ;Random guessing.

(defn get-id-text [pdf]
  (println pdf)
  (second (re-find arxiv-regex (replace (text/extract pdf) "\n" ""))))

(defn fuzzy [str substr] ;slightly weak string matching
  (includes? (lower-case str) (lower-case substr)))

(defn mk-query [title]
  (str "http://export.arxiv.org/api/query?search_query=all:"
       (-> title
           (replace " " "+ ")
           (replace ":" " ")))) ;colons are syntax in search strings
(def test-title "LLOV: A Fast Static Data-Race Checker for OpenMP Programs")

(defn find-entry-title [title]
  (->> title
       mk-query
       parse-url
       :feed
       :entries
       (filter #(fuzzy (:title %) title))
       first))

(defn find-entry-id [id]
  (->> id
       (str "http://export.arxiv.org/api/query?search_query=id:")
       parse-url
       :feed
       :entries
       (filter #(includes? (:link %) (str "http://arxiv.org/abs/" id )))
       first))

;Just work directly with arxiv entries?
(defn entry-to-bib [entry]
  (when entry {:title (:title entry)
   :arxiv (:link entry)
   :abstract (:value (:description entry))
   :date (:published-date entry)
   :authors (:authors entry)})) ;note that authors is a seq of {:name _ :uri _ :email _} maps

(defn year [date]
  (.format (java.text.SimpleDateFormat. "yyyy") date))

;hack this a bit
(defn pagetitle [bib]
  (println bib)
  (str (-> bib
           :authors
           first
           :name
           (split #" ")
           last) (year (:date bib))))

(defn authors [bib];;Linkify? Add emails and stuff?
  {:string "Authors"
   :heading 3
   :children [{:string (join " and " (map :name (:authors bib)))}]})

(defn link [bib]
  {:string (str "[arXiv link](" (:arxiv bib) ")")})

(defn abstract [bib]
  {:string "Abstract"
   :heading 3
   :children [{:string (replace (trim (:abstract bib)) "\n" " ")}]})
(defn title [bib]
  {:string (:title bib)
   :heading 2})

(defn bib-to-roam [bib misc]
  (when bib {:title (pagetitle bib)
             :children [ {:string (:title bib)
                          :heading 2
                          :children [(authors bib)
                                     (link bib)
                                     (abstract bib)
                                     misc]}]})) ;fallthrough

(defn find-entry-pdf [pdf]
  (let [id (get-id-text pdf)
        title (get-title-md pdf)]
    (println title id pdf)
    (or (when title (find-entry-title title))
        (when id (find-entry-id id)))))

(defn make-roam-bibs
  [& {:keys [ids titles filenames misc] :or {ids [] titles [] filenames [] misc {:string ""}}}]
  (let [id-bibs (map #(entry-to-bib (find-entry-id %)) ids)
        title-bibs (map #(entry-to-bib (find-entry-title %)) titles)
        filename-bibs (map #(entry-to-bib (find-entry-pdf %)) filenames)]
    (vec (filter identity (map #(bib-to-roam % misc) (concat id-bibs title-bibs filename-bibs))))))

(defn run-dir [misc]
  (let [pdfs (map fs/absolute-path (fs/glob "*.pdf"))]
    (println pdfs)
    (spit "roambib.json" (json/write-str (make-roam-bibs :filenames pdfs :misc misc)) :encoding "UTF-8")))

(defn -main [misc-str & args]
  (println misc-str)
  (run-dir {:string misc-str}))