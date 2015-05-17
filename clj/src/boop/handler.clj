(ns boop.handler
  (:require [boop.db :as db]
            [boop.fb-auth :as fb-auth]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [resource-response
                                        content-type
                                        redirect]]))


(defn app-routes
  [config]

  (db/set-connection! (:dynamodb config)) ; TODO: create a 'with-connection'

  (routes

    (GET "/" []
      (content-type (resource-response "index.html")
                    "text/html"))

    (GET "/api/boops" {user-id :user-id}
      {:body (db/get-counter user-id)})

    (POST "/api/boop" {user-id :user-id}
      {:body (db/inc-counter! user-id)})

    (PUT "/api/boop" {user-id :user-id}
      {:body (db/reset-counter! user-id)})

    (route/resources "/")

    (route/not-found "Not Found")))


(defn print-req
  [handler]
  (fn [req]
    (clojure.pprint/pprint req)
    (handler req)))


(defn app
  [config]
  (-> (app-routes config)
      (fb-auth/wrap-fb-auth config)
      wrap-json-response
      wrap-session
      wrap-params))


(def app-debug
  (delay
    (app (read-string (slurp "config/ring-local.edn")))))
