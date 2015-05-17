(ns boop.core
  (:gen-class)
  (:require [boop.handler :refer [app]]
            [org.httpkit.server :refer [run-server]]))

(defn -main
  [config-file & _]
  (let [config (read-string (slurp config-file))]
    (println "Starting server" (:server-opts config))
    (run-server (app config)
                (:server-opts config))))
