(defproject lurodrigo/clj-code-diff "0.1.0-SNAPSHOT"
  :description "experiments with clojure code diffing"
  :url "https://github.com/lurodrigo/clj-code-diff"
  :license {:name "MIT License"
            :url "https://github.com/lurodrigo/clj-code-diff/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [juji/editscript "0.4.6"]
                 [rewrite-clj "0.6.1"]]
  :repl-options {:init-ns parser.core})
