# clj-code-diff


## Usage

```clojure
(let [code1 (parser/parse-string-all "(def x 1)")
      code2 (parser/parse-string-all "(defn y [x] (* x 1))")
      data1 (node->data code1)
      data2 (node->data code2)
      edits (e/diff data1 data2)]
  (apply-edits (node->data code1) edits))
```

Shows
```
Starting with
(def x 1)

Replace «def» with «defn»:
(defn x 1) 

Replace «x» with «y»:
(defn y 1) 

Add «[x]»:
(defn y [x]1) 

Add « »:
(defn y [x] 1) 

Replace «1» with «(* x 1)»:
(defn y [x] (* x 1)) 
```

## License

Copyright © 2020 Luiz Rodrigo de Souza.
