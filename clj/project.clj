(defproject boop "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure  "1.6.0"]
                 [compojure            "1.3.4"]
                 [ring/ring-json       "0.3.1"]
                 [com.taoensso/faraday "1.6.0"]
                 [clj-oauth2           "0.3.1"]
                 [org.apache.httpcomponents/httpclient "4.4.1"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler boop.handler/app-debug}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
