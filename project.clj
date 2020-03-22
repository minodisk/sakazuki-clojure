(defproject sakazuki "0.1.0-SNAPSHOT"
  :description "A design of a keyboard that fits your hand in the shape of the bottom of a sphere."
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :main sakazuki.core
  :plugins [[lein-git-deps "0.0.2-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [unicode-math "0.2.0"]
                 [cljfmt "0.6.5"]
                 [net.mikera/core.matrix "0.62.0"]]
  :git-dependencies [["https://github.com/farrellm/scad-clj.git" "master"]]
  :repl-options {:init-ns sakazuki.core})
