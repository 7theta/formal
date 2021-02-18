(defproject com.7theta/formal "0.1.1"
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojure/clojurescript "1.10.773"]
                 [com.7theta/utilis "1.12.1"]
                 [metosin/malli "0.2.1"]
                 [borkdude/sci "0.2.1"]
                 [reagent "1.0.0"]]
  :profiles {:dev {:source-paths ["src" "example/src"]
                   :dependencies [[thheller/shadow-cljs "2.11.18"]]}})
