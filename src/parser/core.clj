(ns parser.core
  (:require
    [editscript.core :as e]
    [rewrite-clj.node :as node]
    [rewrite-clj.node.seq :as seq]
    [rewrite-clj.node.protocols :as protocols]
    [rewrite-clj.parser :as parser]))

(defn node->data
  "Provides an representation of the node that's convenient for diffing."
  [node]
  (let [tag      (node/tag node)
        children (->> (try
                        (node/children node)
                        (catch Throwable _ []))
                      (map node->data))]
    (if (empty? children)
      (node/string node)
      (vec (cons tag children)))))

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

(defn apply-edit
  [data [location op new-value :as edit]]
  (case op
    :- (printf "Delete «%s»:\n" (-> (get-in data location)
                                    ; print*
                                    data->node
                                    node/string))
    :+ (printf "Add «%s»:\n"  (-> new-value data->node node/string))
    :r (printf "Replace «%s» with «%s»:\n" (-> (get-in data location)
                                               ; print*
                                               data->node
                                               node/string)
               (-> new-value data->node node/string)))
  (let [result (e/patch data (edit/edits->script [edit]))]
    (println (-> result data->node node/string) "\n")
    result))

(defn apply-edits
  [data edits]
  (printf "Starting with\n%s\n\n" (-> data data->node node/string))
  (reduce apply-edit data (edit/get-edits edits)))

(comment
  (def code1 (parser/parse-string "(defn x 1)"))
  (def code2 (parser/parse-string "(defn y [1])"))
  (def data1 (node->data code1))
  (def data2 (node->data code2))
  (def edits (e/diff data1 data2))
  (apply-edits (node->data code1) edits)
  )