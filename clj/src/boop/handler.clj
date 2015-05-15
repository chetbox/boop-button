(ns boop.handler
  (:require [boop.db :as db]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [resource-response content-type]]))


(def dummy-user "some guy")


(defn app-routes
  [config]

  (db/set-connection! (:db config)) ; TODO: create a 'with-connection'

  (routes

    (GET "/" []
      (content-type (resource-response "index.html")
                    "text/html"))

    (GET "/api/boops" []
      {:body (db/get-counter dummy-user)})

    (POST "/api/boop" []
      {:body (db/inc-counter! dummy-user)})

    (PUT "/api/boop" [label]
      {:body (db/reset-counter! dummy-user label)})

    (route/resources "/")

    (route/not-found "Not Found")))


(defn app
  [config]
  (-> config
      app-routes
      wrap-params
      wrap-json-response))


(def app-debug
  (app (read-string (slurp "config-debug.edn"))))
