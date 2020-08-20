(ns parser.core
  (:require
    [editscript.core :as e]
    [rewrite-clj.node :as node]
    [rewrite-clj.node.seq :as seq]
    [rewrite-clj.node.protocols :as protocols]
    [rewrite-clj.parser :as parser]))

(defn node->data
  "Provides a representation of the node that's convenient for diffing."
  [node]
  (let [children (->> (try
                        (node/children node)
                        (catch Throwable _ []))
                      (map node->data))]
    (if (empty? children)
      (node/string node)
      (vec (cons (node/tag node) children)))))

(defn data->node
  [data]
  (if (string? data)
    (parser/parse-string data)
    (let [[tag & children] data
          constructor (case tag
                        :list node/list-node
                        :reader-macro node/reader-macro-node
                        :vector node/vector-node
                        :set node/set-node
                        :meta node/meta-node
                        :quote node/quote-node
                        :syntax-quote node/syntax-quote-node
                        :var node/var-node)]
      (constructor (map data->node children)))))

(def as-code (comp node/string data->node))

(defn apply-edit
  [data [location op new-value :as edit]]
  (case op
    :- (printf "Delete «%s»:\n" (as-code (get-in data location)))
    :+ (printf "Add «%s»:\n"  (as-code new-value))
    :r (printf "Replace «%s» with «%s»:\n" (as-code (get-in data location))
               (as-code new-value)))
  (let [result (e/patch data (edit/edits->script [edit]))]
    (println (as-code result) "\n")
    result))

(defn apply-edits
  [data edits]
  (printf "Starting with\n%s\n\n" (as-code data))
  (reduce apply-edit data (edit/get-edits edits)))

(comment
  (def code1 (parser/parse-string "(defn x 1)"))
  (def code2 (parser/parse-string "(defn y [x] (* x 1))"))
  (def data1 (node->data code1))
  (def data2 (node->data code2))
  (def edits (e/diff data1 data2))
  (apply-edits (node->data code1) edits)
  )