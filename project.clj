(defproject classfinder "0.1.0-SNAPSHOT"
  :description "An API for WWU registration information."
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-defaults "0.1.2"]
                 [compojure "1.3.1"]
                 [liberator "0.12.2"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler classfinder.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
