(defproject sakazuki "0.1.0-SNAPSHOT"
  :description "A design of a keyboard that fits your hand in the shape of the bottom of a sphere."
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :main sakazuki.core
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [unicode-math "0.2.0"]
                 [scad-clj "0.5.3"]
                 [cljfmt "0.6.5"]]
  :repl-options {:init-ns sakazuki.core})
